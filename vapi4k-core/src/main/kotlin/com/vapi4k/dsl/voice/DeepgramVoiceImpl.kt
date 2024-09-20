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

package com.vapi4k.dsl.voice

import com.vapi4k.api.voice.DeepgramVoice
import com.vapi4k.api.voice.DeepgramVoiceProperties
import com.vapi4k.dtos.voice.DeepgramVoiceDto

internal class DeepgramVoiceImpl(
  private val dto: DeepgramVoiceDto,
) : DeepgramVoiceProperties by dto,
  DeepgramVoice
