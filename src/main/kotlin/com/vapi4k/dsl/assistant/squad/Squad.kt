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

package com.vapi4k.dsl.assistant.squad


import com.vapi4k.common.CacheId
import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.assistant.AssistantOverrides
import com.vapi4k.responses.assistant.SquadDto
import kotlinx.serialization.json.JsonElement

interface SquadUnion {
  var name: String
}

@AssistantDslMarker
data class Squad internal constructor(
  val request: JsonElement,
  internal val cacheId: CacheId,
  internal val squadDto: SquadDto,
) : SquadUnion by squadDto {
  fun members(block: Members.() -> Unit) {
    Members(this).apply(block)
  }

  fun memberOverrides(block: AssistantOverrides.() -> Unit) {
    AssistantOverrides(request, cacheId, squadDto.membersOverrides).apply(block)
  }
}
