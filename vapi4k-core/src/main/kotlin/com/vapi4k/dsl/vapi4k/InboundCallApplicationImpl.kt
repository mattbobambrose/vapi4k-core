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

package com.vapi4k.dsl.vapi4k

import com.vapi4k.api.assistant.InboundCallAssistantResponse
import com.vapi4k.api.vapi4k.InboundCallApplication
import com.vapi4k.common.CoreEnvVars.serverBaseUrl
import com.vapi4k.common.Headers.VAPI4K_VALIDATE_HEADER
import com.vapi4k.common.Headers.VAPI4K_VALIDATE_VALUE
import com.vapi4k.common.Headers.VAPI_SECRET_HEADER
import com.vapi4k.dsl.assistant.InboundCallAssistantResponseImpl
import com.vapi4k.utils.HttpUtils.httpClient
import com.vapi4k.utils.common.Utils.isNull
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonElement

class InboundCallApplicationImpl internal constructor() :
  AbstractApplicationImpl(ApplicationType.INBOUND_CALL),
  InboundCallApplication {
  private var assistantRequest: (suspend InboundCallAssistantResponse.(JsonElement) -> Unit)? = null

  override fun fetchContent(
    request: JsonElement,
    appName: String,
    secret: String,
  ): Pair<HttpStatusCode, String> =
    runBlocking {
      val url = "$serverBaseUrl/$appName"
      val response = httpClient.post(url) {
        contentType(Application.Json)
        headers.append(VAPI4K_VALIDATE_HEADER, VAPI4K_VALIDATE_VALUE)
        if (secret.isNotEmpty())
          headers.append(VAPI_SECRET_HEADER, secret)
        setBody(request)
      }
      response.status to response.bodyAsText()
    }

  override fun onAssistantRequest(block: suspend InboundCallAssistantResponse.(JsonElement) -> Unit) {
    if (assistantRequest.isNull())
      assistantRequest = block
    else
      error("onAssistantRequest{} can be called only once per inboundCallApplication{}")
  }

  internal suspend fun getAssistantResponse(request: JsonElement) =
    assistantRequest.let { func ->
      if (func.isNull()) {
        error("onAssistantRequest{} not called")
      } else {
        val assistantRequestContext = AssistantRequestContext(this, request)
        val assistantResponse = InboundCallAssistantResponseImpl(assistantRequestContext)
        func.invoke(assistantResponse, request)
        if (!assistantResponse.isAssigned)
          error("onAssistantRequest{} is missing a call to assistant{}, assistantId{}, squad{}, or squadId{}")
        else
          assistantResponse.assistantRequestResponse
      }
    }
}
