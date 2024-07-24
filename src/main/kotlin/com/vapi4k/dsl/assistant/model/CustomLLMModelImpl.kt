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

package com.vapi4k.dsl.assistant.model

import com.vapi4k.common.CacheId
import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.assistant.KnowledgeBase
import com.vapi4k.dsl.assistant.enums.MetaDataSendModeType
import com.vapi4k.dsl.assistant.tools.Functions
import com.vapi4k.dsl.assistant.tools.Tools
import com.vapi4k.responses.assistant.model.CustomLLMModelDto
import kotlinx.serialization.json.JsonElement

interface CustomLLMModelProperties {
  var model: String
  val toolIds: MutableSet<String>
  var temperature: Int
  var maxTokens: Int
  var emotionRecognitionEnabled: Boolean?
  var numFastTurns: Int
  var url: String
  var metadataSendMode: MetaDataSendModeType
}

@AssistantDslMarker
interface CustomLLMModel : CustomLLMModelProperties {
  var systemMessage: String
  var assistantMessage: String
  var functionMessage: String
  var toolMessage: String
  var userMessage: String
  fun tools(block: Tools.() -> Unit): Tools
  fun functions(block: Functions.() -> Unit): Functions
  fun knowledgeBase(block: KnowledgeBase.() -> Unit): KnowledgeBase
}

class CustomLLMModelImpl(
  request: JsonElement,
  cacheId: CacheId,
  dto: CustomLLMModelDto,
) : CustomLLMModelProperties by dto, CustomLLMModel, AbstractModelImpl(request, cacheId, dto)
