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

package com.vapi4k.dtos.api.destination

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = DestinationSerializer::class)
interface CommonDestinationDto

private object DestinationSerializer : KSerializer<CommonDestinationDto> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CommonDestinationDto")

  override fun serialize(
    encoder: Encoder,
    value: CommonDestinationDto,
  ) {
    when (value) {
      is AssistantDestinationDto -> encoder.encodeSerializableValue(AssistantDestinationDto.serializer(), value)
      is NumberDestinationDto -> encoder.encodeSerializableValue(NumberDestinationDto.serializer(), value)
      is SipDestinationDto -> encoder.encodeSerializableValue(SipDestinationDto.serializer(), value)
      else -> error("Invalid destination provider: ${value::class.simpleName}")
    }
  }

  override fun deserialize(decoder: Decoder): CommonDestinationDto =
    throw NotImplementedError("Deserialization is not supported")
}
