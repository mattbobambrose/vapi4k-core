/*
 * Copyright © 2024 Matthew Ambrose (mattbobambrose@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package com.vapi4k.responses

import com.vapi4k.api.vapi4k.AssistantRequestUtils.id
import com.vapi4k.api.vapi4k.AssistantRequestUtils.toolCallArguments
import com.vapi4k.api.vapi4k.AssistantRequestUtils.toolCallName
import com.vapi4k.common.AssistantId
import com.vapi4k.common.SessionId
import com.vapi4k.dsl.assistant.ManualToolCallResponseImpl
import com.vapi4k.dsl.tools.ManualToolImpl
import com.vapi4k.dsl.toolservice.RequestCompleteMessagesImpl
import com.vapi4k.dsl.toolservice.RequestFailedMessagesImpl
import com.vapi4k.dsl.vapi4k.AbstractApplicationImpl
import com.vapi4k.dtos.tools.CommonToolMessageDto
import com.vapi4k.plugin.Vapi4kServer.logger
import com.vapi4k.utils.JsonUtils.toolCallList
import com.vapi4k.utils.common.Utils.errorMsg
import com.vapi4k.utils.json.JsonElementUtils.stringValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ToolCallResponseDto(
  var results: MutableList<ToolCallResult> = mutableListOf(),
  var error: String = "",
) {
  companion object {
    suspend fun getToolCallResponse(
      application: AbstractApplicationImpl,
      request: JsonElement,
      sessionId: SessionId,
      assistantId: AssistantId,
    ) = runCatching {
      ToolCallMessageResponse()
        .also { tcmr ->
          val response = tcmr.messageResponse
          var errorMessage = ""

          request.toolCallList
            .forEach { toolCall ->
              response.also { toolCallResponse ->
                response.results +=
                  ToolCallResult()
                    .also { toolCallResult ->
                      val funcName = toolCall.toolCallName
                      val args = toolCall.toolCallArguments
                      toolCallResult.toolCallId = toolCall.id
                      toolCallResult.name = funcName.value
                      val errorAction = { errorMsg: String ->
                        toolCallResult.error = errorMsg
                        errorMessage = errorMsg
                      }
                      runCatching {
                        when {
                          application.containsServiceToolInCache(sessionId, assistantId, funcName) -> {
                            application.getServiceToolFromCache(sessionId, assistantId, funcName)
                              .also { func ->
                                logger.info { "Invoking $funcName on serviceTool method ${func.fqName}" }
                              }
                              .invokeToolMethod(
                                isTool = true,
                                request = request,
                                args = args,
                                messageDtos = toolCallResult.messageDtos,
                                successAction = { result -> toolCallResult.result = result },
                                errorAction = errorAction,
                              )
                          }

                          application.containsManualToolInCache(funcName) -> {
                            val manualToolImpl: ManualToolImpl = application.manualToolCache.getTool(funcName)
                            if (!manualToolImpl.isToolCallRequestInitialized()) {
                              error("onInvoke{} not declared in $funcName")
                            } else {
                              val completeMsgs = RequestCompleteMessagesImpl()
                              val failedMsgs = RequestFailedMessagesImpl()
                              val resp = ManualToolCallResponseImpl(completeMsgs, failedMsgs, toolCallResult)
                              runCatching {
                                manualToolImpl.toolCallRequest.invoke(resp, args)
                                toolCallResult.messageDtos.addAll(completeMsgs.messageList.map { it.dto })
                              }.onFailure {
                                with(toolCallResult) {
                                  result = ""
                                  error = it.errorMsg
                                  messageDtos.addAll(failedMsgs.messageList.map { it.dto })
                                }
                              }
                              toolCallResult.apply {
                                toolCallId = toolCall.stringValue("id")
                              }
                            }
                          }

                          else -> error("Tool not found: $funcName")
                        }
                      }.getOrElse { e ->
                        val errorMsg = "Error invoking tool: $funcName ${e.errorMsg}"
                        logger.error(e) { errorMsg }
                        errorAction(errorMsg)
                      }
                    }

                if (errorMessage.isNotEmpty()) {
                  response.error = errorMessage
                }
              }
            }
        }
    }.getOrElse { e ->
      logger.error { "Error receiving tool call: ${e.errorMsg}" }
      error("Error receiving tool call: ${e.errorMsg}")
    }
  }
}

@Serializable
data class ToolCallResult(
  var toolCallId: String = "",
  var name: String = "",
  var result: String = "",
  var error: String = "",
  @SerialName("message")
  val messageDtos: MutableList<CommonToolMessageDto> = mutableListOf(),
)
