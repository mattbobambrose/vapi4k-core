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

package com.vapi4k.dtos.assistant

import com.vapi4k.dsl.assistant.AssistantProperties
import com.vapi4k.dsl.assistant.ModelDtoUnion
import com.vapi4k.dsl.assistant.enums.AssistantClientMessageType
import com.vapi4k.dsl.assistant.enums.AssistantServerMessageType
import com.vapi4k.dsl.assistant.enums.BackgroundSoundType
import com.vapi4k.dsl.assistant.enums.FirstMessageModeType
import com.vapi4k.dtos.AnalysisPlanDto
import com.vapi4k.dtos.ArtifactPlanDto
import com.vapi4k.dtos.MessagePlanDto
import com.vapi4k.dtos.TransportConfigurationDto
import com.vapi4k.dtos.VoicemailDetectionDto
import com.vapi4k.dtos.model.CommonModelDto
import com.vapi4k.dtos.transcriber.CommonTranscriberDto
import com.vapi4k.dtos.voice.CommonVoiceDto
import com.vapi4k.utils.Utils.isNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class AbstractAssistantDto(
  var backchannelingEnabled: Boolean? = null,
  var backgroundDenoisingEnabled: Boolean? = null,
  var backgroundSound: BackgroundSoundType = BackgroundSoundType.UNSPECIFIED,
  var endCallMessage: String = "",
  val endCallPhrases: MutableSet<String> = mutableSetOf(),
  var firstMessage: String = "",
  var firstMessageMode: FirstMessageModeType = FirstMessageModeType.UNSPECIFIED,
  var hipaaEnabled: Boolean? = null,
  var llmRequestDelaySeconds: Double = -1.0,
  var llmRequestNonPunctuatedDelaySeconds: Double = -1.0,
  var maxDurationSeconds: Int = -1,
  val metadata: MutableMap<String, String> = mutableMapOf(),
  var modelOutputInMessagesEnabled: Boolean? = null,
  var name: String = "",
  var numWordsToInterruptAssistant: Int = -1,
  var recordingEnabled: Boolean? = null,
  var responseDelaySeconds: Double = -1.0,
  var serverUrl: String = "",
  var serverUrlSecret: String = "",
  var silenceTimeoutSeconds: Int = -1,
  var voicemailMessage: String = "",

  // Need a copy of DEFAULT_CLIENT_MESSAGES and DEFAULT_SERVER_MESSAGES here, so call toMutableSet()
  var clientMessages: MutableSet<AssistantClientMessageType> = DEFAULT_CLIENT_MESSAGES.toMutableSet(),
  var serverMessages: MutableSet<AssistantServerMessageType> = DEFAULT_SERVER_MESSAGES.toMutableSet(),

  // TODO: Came from squad assistant
  val transportConfigurations: MutableList<TransportConfigurationDto> = mutableListOf(),

  // TODO: This needs to be added to docs - https://docs.vapi.ai/assistants/function-calling
  var forwardingPhoneNumber: String = "",

  // TODO: Not in docs or squad - https://docs.vapi.ai/assistants/function-calling
  var endCallFunctionEnabled: Boolean? = null,

  // TODO: Not in docs or squad - https://docs.vapi.ai/assistants/function-calling
  var dialKeypadFunctionEnabled: Boolean? = null,
)

@Serializable
data class AssistantDto(
  @SerialName("transcriber")
  override var transcriberDto: CommonTranscriberDto? = null,
  @SerialName("model")
  override var modelDto: CommonModelDto? = null,
  @SerialName("voice")
  override var voiceDto: CommonVoiceDto? = null,
  @SerialName("voicemailDetection")
  val voicemailDetectionDto: VoicemailDetectionDto = VoicemailDetectionDto(),
  @SerialName("analysisPlan")
  val analysisPlanDto: AnalysisPlanDto = AnalysisPlanDto(),
  @SerialName("artifactPlan")
  val artifactPlanDto: ArtifactPlanDto = ArtifactPlanDto(),
  @SerialName("messagePlan")
  val messagePlanDto: MessagePlanDto = MessagePlanDto(),
) : AbstractAssistantDto(),
  AssistantProperties,
  ModelDtoUnion {
  @Transient
  var updated = false

  internal fun verifyValues() {
    if (modelDto.isNull())
      error("An assistant{} requires a model{} decl ")
  }
}

private val DEFAULT_CLIENT_MESSAGES =
  mutableSetOf(
    AssistantClientMessageType.CONVERSATION_UPDATE,
    AssistantClientMessageType.FUNCTION_CALL,
    AssistantClientMessageType.HANG,
    AssistantClientMessageType.MODEL_OUTPUT,
    AssistantClientMessageType.SPEECH_UPDATE,
    AssistantClientMessageType.STATUS_UPDATE,
    AssistantClientMessageType.TRANSCRIPT,
    AssistantClientMessageType.TOOL_CALLS,
    AssistantClientMessageType.USER_INTERRUPTED,
    AssistantClientMessageType.VOICE_INPUT,
  )

private val DEFAULT_SERVER_MESSAGES =
  mutableSetOf(
    AssistantServerMessageType.CONVERSATION_UPDATE,
    AssistantServerMessageType.END_OF_CALL_REPORT,
    AssistantServerMessageType.FUNCTION_CALL,
    AssistantServerMessageType.HANG,
    AssistantServerMessageType.SPEECH_UPDATE,
    AssistantServerMessageType.STATUS_UPDATE,
    AssistantServerMessageType.TOOL_CALLS,
    AssistantServerMessageType.TRANSFER_DESTINATION_REQUEST,
    AssistantServerMessageType.USER_INTERRUPTED,
  )
