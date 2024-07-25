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

import com.vapi4k.common.SessionId
import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.assistant.model.enums.AnthropicModelType
import com.vapi4k.dsl.assistant.tools.Functions
import com.vapi4k.dsl.assistant.tools.Tools
import com.vapi4k.responses.assistant.model.AnthropicModelDto
import kotlinx.serialization.json.JsonElement

interface AnthropicModelProperties {
  var modelType: AnthropicModelType
  var customModel: String
  val toolIds: MutableSet<String>
  var temperature: Int
  var maxTokens: Int
  var emotionRecognitionEnabled: Boolean?
  var numFastTurns: Int
}

@AssistantDslMarker
interface AnthropicModel : AnthropicModelProperties {
  var systemMessage: String
  var assistantMessage: String
  var functionMessage: String
  var toolMessage: String
  var userMessage: String
  fun tools(block: Tools.() -> Unit): Tools
  fun functions(block: Functions.() -> Unit): Functions
  fun knowledgeBase(block: KnowledgeBase.() -> Unit): KnowledgeBase
}

class AnthropicModelImpl(
  request: JsonElement,
  sessionId: SessionId,
  dto: AnthropicModelDto,
) : AnthropicModelProperties by dto, AnthropicModel, AbstractModel(request, sessionId, dto)
