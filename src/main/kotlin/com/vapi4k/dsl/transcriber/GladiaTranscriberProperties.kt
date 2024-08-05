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

package com.vapi4k.dsl.transcriber

import com.vapi4k.api.model.enums.GladiaModelType
import com.vapi4k.api.transcriber.enums.GladiaLanguageType

interface GladiaTranscriberProperties {
  var audioEnhancer: Boolean?
  var customLanguage: String
  var customModel: String
  var languageBehavior: String
  var prosody: Boolean?
  var transcriberLanguage: GladiaLanguageType
  var transcriberModel: GladiaModelType
  var transcriptionHint: String
}
