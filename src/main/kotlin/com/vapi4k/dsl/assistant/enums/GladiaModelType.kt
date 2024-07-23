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

package com.vapi4k.dsl.assistant.enums

import com.vapi4k.common.Constants.UNSPECIFIED_DEFAULT
import com.vapi4k.dsl.assistant.enums.GladiaModelType.entries
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = GladiaModelTypeSerializer::class)
enum class GladiaModelType(internal val desc: String) {
  FAST("fast"),
  ACCURATE("accurate"),

  UNSPECIFIED(UNSPECIFIED_DEFAULT);

  fun isSpecified() = this != UNSPECIFIED
}

private object GladiaModelTypeSerializer : KSerializer<GladiaModelType> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GladiaModelType", PrimitiveKind.STRING)

  override fun serialize(
    encoder: Encoder,
    value: GladiaModelType,
  ) = encoder.encodeString(value.desc)

  override fun deserialize(decoder: Decoder) = entries.first { it.desc == decoder.decodeString() }
}
