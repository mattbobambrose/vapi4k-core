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


import com.vapi4k.dsl.assistant.AssistantIdUnion
import com.vapi4k.dsl.assistant.SquadIdUnion
import com.vapi4k.responses.assistant.AssistantDto
import com.vapi4k.responses.assistant.AssistantOverridesDto
import com.vapi4k.responses.assistant.SquadDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface CustomerUnion {
  var number: String
}

// TODO: Make polymorphic
@Serializable
data class CustomerDto(
  override var number: String = "",

  var sipUri: String = "",
  var name: String = "",
  var extension: String = "",
) : CustomerUnion

interface CallUnion {
  var phoneNumberId: String
}

@Serializable
data class CallRequest(
  override var assistantId: String = "",

  @SerialName("assistant")
  val assistantDto: AssistantDto = AssistantDto(),

  @SerialName("assistantOverrides")
  override val assistantOverridesDto: AssistantOverridesDto = AssistantOverridesDto(),

  override var squadId: String = "",

  @SerialName("squad")
  val squadDto: SquadDto = SquadDto(),

  override var phoneNumberId: String = "",

  @SerialName("customer")
  val customerDto: CustomerDto = CustomerDto(),

  var error: String = "",
) : CallUnion, AssistantIdUnion, SquadIdUnion
