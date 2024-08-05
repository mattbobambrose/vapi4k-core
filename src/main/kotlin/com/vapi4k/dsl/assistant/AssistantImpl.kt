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

import com.vapi4k.api.assistant.AnalysisPlan
import com.vapi4k.api.assistant.ArtifactPlan
import com.vapi4k.api.assistant.Assistant
import com.vapi4k.api.assistant.AssistantOverrides
import com.vapi4k.api.assistant.VoicemailDetection
import com.vapi4k.api.model.AnthropicModel
import com.vapi4k.api.model.AnyscaleModel
import com.vapi4k.api.model.CustomLLMModel
import com.vapi4k.api.model.DeepInfraModel
import com.vapi4k.api.model.GroqModel
import com.vapi4k.api.model.OpenAIModel
import com.vapi4k.api.model.OpenRouterModel
import com.vapi4k.api.model.PerplexityAIModel
import com.vapi4k.api.model.TogetherAIModel
import com.vapi4k.api.model.VapiModel
import com.vapi4k.api.transcriber.DeepgramTranscriber
import com.vapi4k.api.transcriber.GladiaTranscriber
import com.vapi4k.api.transcriber.TalkscriberTranscriber
import com.vapi4k.api.vapi4k.AssistantRequestContext
import com.vapi4k.api.vapi4k.Vapi4kConfig
import com.vapi4k.api.voice.AzureVoice
import com.vapi4k.api.voice.CartesiaVoice
import com.vapi4k.api.voice.DeepgramVoice
import com.vapi4k.api.voice.ElevenLabsVoice
import com.vapi4k.api.voice.LMNTVoice
import com.vapi4k.api.voice.NeetsVoice
import com.vapi4k.api.voice.OpenAIVoice
import com.vapi4k.api.voice.PlayHTVoice
import com.vapi4k.api.voice.RimeAIVoice
import com.vapi4k.common.SessionCacheId
import com.vapi4k.dsl.model.ModelUnion
import com.vapi4k.dsl.model.analysisPlanUnion
import com.vapi4k.dsl.model.anthropicModelUnion
import com.vapi4k.dsl.model.anyscaleModelUnion
import com.vapi4k.dsl.model.artifactPlanUnion
import com.vapi4k.dsl.model.azureVoiceUnion
import com.vapi4k.dsl.model.cartesiaVoiceUnion
import com.vapi4k.dsl.model.customLLMModelUnion
import com.vapi4k.dsl.model.deepInfraModelUnion
import com.vapi4k.dsl.model.deepgramTranscriberUnion
import com.vapi4k.dsl.model.deepgramVoiceUnion
import com.vapi4k.dsl.model.elevenLabsVoiceUnion
import com.vapi4k.dsl.model.gladiaTranscriberUnion
import com.vapi4k.dsl.model.groqModelUnion
import com.vapi4k.dsl.model.lmntVoiceUnion
import com.vapi4k.dsl.model.neetsVoiceUnion
import com.vapi4k.dsl.model.openAIModelUnion
import com.vapi4k.dsl.model.openAIVoiceUnion
import com.vapi4k.dsl.model.openRouterModelUnion
import com.vapi4k.dsl.model.perplexityAIModelUnion
import com.vapi4k.dsl.model.playHTVoiceUnion
import com.vapi4k.dsl.model.rimeAIVoiceUnion
import com.vapi4k.dsl.model.talkscriberTranscriberUnion
import com.vapi4k.dsl.model.togetherAIModelUnion
import com.vapi4k.dsl.model.vapiModelUnion
import com.vapi4k.dsl.model.voicemailDetectionUnion
import com.vapi4k.dtos.assistant.AssistantDto
import com.vapi4k.dtos.assistant.AssistantOverridesDto
import com.vapi4k.utils.AssistantCacheIdSource
import com.vapi4k.utils.DuplicateChecker

data class AssistantImpl internal constructor(
  override val assistantRequestContext: AssistantRequestContext,
  override val sessionCacheId: SessionCacheId,
  internal val assistantCacheIdSource: AssistantCacheIdSource,
  private val assistantDto: AssistantDto,
  private val assistantOverridesDto: AssistantOverridesDto,
) : AssistantProperties by assistantDto,
  Assistant,
  ModelUnion {
  override val transcriberChecker = DuplicateChecker()
  override val modelChecker = DuplicateChecker()
  override val voiceChecker = DuplicateChecker()
  override val assistantCacheId = assistantCacheIdSource.nextAssistantCacheId()

  override val modelDtoUnion get() = assistantDto
  override val voicemailDetectionDto get() = assistantDto.voicemailDetectionDto
  override val analysisPlanDto get() = assistantDto.analysisPlanDto
  override val artifactPlanDto get() = assistantDto.artifactPlanDto
  override var videoRecordingEnabled: Boolean?
    get() = assistantDto.artifactPlanDto.videoRecordingEnabled
    set(value) {
      assistantDto.artifactPlanDto.videoRecordingEnabled = value
    }

  override fun voicemailDetection(block: VoicemailDetection.() -> Unit): VoicemailDetection =
    voicemailDetectionUnion(block)

  // Transcribers
  override fun deepgramTranscriber(block: DeepgramTranscriber.() -> Unit) = deepgramTranscriberUnion(block)

  override fun gladiaTranscriber(block: GladiaTranscriber.() -> Unit) = gladiaTranscriberUnion(block)

  override fun talkscriberTranscriber(block: TalkscriberTranscriber.() -> Unit) = talkscriberTranscriberUnion(block)

  // Models
  override fun anyscaleModel(block: AnyscaleModel.() -> Unit) = anyscaleModelUnion(block)

  override fun anthropicModel(block: AnthropicModel.() -> Unit) = anthropicModelUnion(block)

  override fun customLLMModel(block: CustomLLMModel.() -> Unit) = customLLMModelUnion(block)

  override fun deepInfraModel(block: DeepInfraModel.() -> Unit) = deepInfraModelUnion(block)

  override fun groqModel(block: GroqModel.() -> Unit) = groqModelUnion(block)

  override fun openAIModel(block: OpenAIModel.() -> Unit) = openAIModelUnion(block)

  override fun openRouterModel(block: OpenRouterModel.() -> Unit) = openRouterModelUnion(block)

  override fun perplexityAIModel(block: PerplexityAIModel.() -> Unit) = perplexityAIModelUnion(block)

  override fun togetherAIModel(block: TogetherAIModel.() -> Unit) = togetherAIModelUnion(block)

  override fun vapiModel(block: VapiModel.() -> Unit) = vapiModelUnion(block)

  // Voices
  override fun azureVoice(block: AzureVoice.() -> Unit) = azureVoiceUnion(block)

  override fun cartesiaVoice(block: CartesiaVoice.() -> Unit) = cartesiaVoiceUnion(block)

  override fun deepgramVoice(block: DeepgramVoice.() -> Unit) = deepgramVoiceUnion(block)

  override fun elevenLabsVoice(block: ElevenLabsVoice.() -> Unit) = elevenLabsVoiceUnion(block)

  override fun lmntVoice(block: LMNTVoice.() -> Unit) = lmntVoiceUnion(block)

  override fun neetsVoice(block: NeetsVoice.() -> Unit) = neetsVoiceUnion(block)

  override fun openAIVoice(block: OpenAIVoice.() -> Unit) = openAIVoiceUnion(block)

  override fun playHTVoice(block: PlayHTVoice.() -> Unit) = playHTVoiceUnion(block)

  override fun rimeAIVoice(block: RimeAIVoice.() -> Unit) = rimeAIVoiceUnion(block)

  // AssistantOverrides
  override fun assistantOverrides(block: AssistantOverrides.() -> Unit): AssistantOverrides =
    AssistantOverridesImpl(
      assistantRequestContext,
      sessionCacheId,
      assistantCacheIdSource,
      assistantOverridesDto
    ).apply(block)

  override fun analysisPlan(block: AnalysisPlan.() -> Unit): AnalysisPlan = analysisPlanUnion(block)

  override fun artifactPlan(block: ArtifactPlan.() -> Unit): ArtifactPlan = artifactPlanUnion(block)

  companion object {
    internal lateinit var config: Vapi4kConfig
  }
}
