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

package com.vapi4k.api.tools

import com.vapi4k.dsl.assistant.AssistantDslMarker
import kotlin.reflect.KFunction

@AssistantDslMarker
interface Tools {
  fun tool(
    obj: Any,
    vararg functions: KFunction<*>,
    block: Tool.() -> Unit = {},
  )

  fun externalTool(block: ExternalTool.() -> Unit)

  fun dtmf(block: BaseTool.() -> Unit)

  fun endCall(block: BaseTool.() -> Unit)

  fun voiceMail(block: BaseTool.() -> Unit)

  fun ghl(block: ToolWithMetaData.() -> Unit)

  fun make(block: ToolWithMetaData.() -> Unit)

  fun transfer(block: BaseTool.() -> Unit)
}
