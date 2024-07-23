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

package com.vapi4k.dsl.assistant

import com.vapi4k.common.CacheId.Companion.UNSPECIFIED_CACHE_ID
import com.vapi4k.dsl.assistant.enums.DestinationType
import com.vapi4k.dsl.assistant.squad.Squad
import com.vapi4k.dsl.assistant.squad.SquadId
import com.vapi4k.responses.AssistantRequestMessageResponse
import com.vapi4k.responses.assistant.NumberDestinationDto
import com.vapi4k.responses.assistant.SipDestinationDto
import kotlinx.serialization.json.JsonElement
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.annotation.AnnotationTarget.PROPERTY_SETTER
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class AssistantDslMarker

object AssistantDsl {
  fun assistant(
    request: JsonElement,
    block: Assistant.() -> Unit,
  ) =
    AssistantRequestMessageResponse().apply {
      with(messageResponse) {
        Assistant(
          request,
          UNSPECIFIED_CACHE_ID,
          assistantDto,
          assistantOverridesDto
        ).apply(block)
      }
    }.messageResponse

  fun assistantId(
    request: JsonElement,
    block: AssistantId.() -> Unit,
  ) =
    AssistantRequestMessageResponse().apply {
      AssistantId(request, UNSPECIFIED_CACHE_ID, messageResponse).apply(block)
    }.messageResponse

  fun squad(
    request: JsonElement,
    block: Squad.() -> Unit,
  ) =
    AssistantRequestMessageResponse().apply {
      Squad(request, UNSPECIFIED_CACHE_ID, messageResponse.squadDto).apply(block)
    }.messageResponse

  fun squadId(
    request: JsonElement,
    block: SquadId.() -> Unit,
  ) =
    AssistantRequestMessageResponse().apply {
      SquadId(request, messageResponse).apply(block)
    }.messageResponse

  fun numberDestination(
    request: JsonElement,
    block: NumberDestination.() -> Unit,
  ) =
    AssistantRequestMessageResponse().apply {
      val numDto = NumberDestinationDto().also { it.type = DestinationType.NUMBER }
      messageResponse.destination = numDto
      NumberDestination(request, numDto).apply(block)
    }.messageResponse

  fun sipDestination(
    request: JsonElement,
    block: SipDestination.() -> Unit,
  ) =
    AssistantRequestMessageResponse().apply {
      val sipDto = SipDestinationDto().also { it.type = DestinationType.SIP }
      messageResponse.destination = sipDto
      SipDestination(request, sipDto).apply(block)
    }.messageResponse
}

@Retention(RUNTIME)
@Target(AnnotationTarget.FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
annotation class ToolCall(
  val description: String = "",
  val name: String = "",
)

@Retention(RUNTIME)
@Target(VALUE_PARAMETER)
annotation class Param(val description: String)
