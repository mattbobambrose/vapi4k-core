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

package com.vapi4k.api.toolservice

import com.vapi4k.api.tools.ToolMessageFailed
import com.vapi4k.dsl.vapi4k.Vapi4KDslMarker
import com.vapi4k.dtos.tools.ToolMessageCondition

/**
<p>These are the messages that will be spoken to the user.
<br>If these are not returned, assistant will speak:
<ol><li>a request-failed message from tool.messages, if it exists
<li>a response generated by the model, if not
</ol>
</p>
 */
@Vapi4KDslMarker
interface RequestFailedMessages {
  /**
  <p>This message is triggered when the tool call fails.
  <br>This message is triggered immediately without waiting for your server to respond for async tool calls.
  <br>If this message is not provided, the model will be requested to respond.
  <br>If this message is provided, only this message will be spoken and the model will not be requested to come up with a response. It's an exclusive OR.
  </p>
   */
  fun requestFailedMessage(block: ToolMessageFailed.() -> Unit): ToolMessageFailed

  /**
  Adds a condition to an optional array of conditions that the tool call arguments must meet in order for this message to be triggered.
   */
  fun condition(
    requiredCondition: ToolMessageCondition,
    vararg additionalConditions: ToolMessageCondition,
    block: RequestFailedCondition.() -> Unit,
  )
}
