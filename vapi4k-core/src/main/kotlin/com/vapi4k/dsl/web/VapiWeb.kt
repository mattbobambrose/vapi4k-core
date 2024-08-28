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

package com.vapi4k.dsl.web

import com.vapi4k.api.web.VapiHtml
import com.vapi4k.common.Constants.STATIC_BASE
import kotlinx.html.HtmlBlockTag
import kotlinx.html.script

object VapiWeb {
  fun HtmlBlockTag.vapi(block: VapiHtml.() -> Unit) {
    script { src = "$STATIC_BASE/js/vapi-web-call.js" }
    VapiHtmlImpl(this).apply(block)
  }
}
