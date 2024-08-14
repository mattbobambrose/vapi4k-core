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

package com.vapi4k.api.squad

import com.vapi4k.api.assistant.AssistantOverrides
import com.vapi4k.dsl.assistant.AssistantDslMarker
import com.vapi4k.dsl.squad.SquadProperties

/**
This is a squad that will be used for the call. To use an existing squad, use `squadId` instead.
 */
@AssistantDslMarker
interface Squad : SquadProperties {
  /**
  <p>This is the list of assistants that make up the squad.
  <br>The call will start with the first assistant in the list.
  </p>
   */
  fun members(block: Members.() -> Unit): Members

  /**
  <p>This can be used to override all the assistants' settings and provide values for their template variables.
  <br>Both <code>membersOverrides</code> and <code>members[n].assistantOverrides</code> can be used together. First, <code>members[n].assistantOverrides</code> is applied. Then, <code>membersOverrides</code> is applied as a global override.
  </p>
   */
  fun memberOverrides(block: AssistantOverrides.() -> Unit): AssistantOverrides
}
