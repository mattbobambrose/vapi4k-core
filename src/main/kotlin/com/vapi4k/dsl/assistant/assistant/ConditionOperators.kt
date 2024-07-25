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

package com.vapi4k.dsl.assistant.assistant

import com.vapi4k.dsl.assistant.model.enums.ToolMessageType
import com.vapi4k.responses.assistant.model.ToolMessageConditionDto.Companion.toolMessageCondition
import com.vapi4k.responses.assistant.model.ToolMessageDto

infix fun String.eq(value: String) =
  toolMessageCondition(this, "eq", value)

infix fun String.neq(value: String) =
  toolMessageCondition(this, "neq", value)

infix fun String.gt(value: String) =
  toolMessageCondition(this, "gt", value)

infix fun String.gte(value: String) =
  toolMessageCondition(this, "gte", value)

infix fun String.lt(value: String) =
  toolMessageCondition(this, "lt", value)

infix fun String.lte(value: String) =
  toolMessageCondition(this, "lte", value)

infix fun String.eq(value: Int) =
  toolMessageCondition(this, "eq", value)

infix fun String.neq(value: Int) =
  toolMessageCondition(this, "neq", value)

infix fun String.gt(value: Int) =
  toolMessageCondition(this, "gt", value)

infix fun String.gte(value: Int) =
  toolMessageCondition(this, "gte", value)

infix fun String.lt(value: Int) =
  toolMessageCondition(this, "lt", value)

infix fun String.lte(value: Int) =
  toolMessageCondition(this, "lte", value)

infix fun String.eq(value: Boolean) =
  toolMessageCondition(this, "eq", value)

infix fun String.neq(value: Boolean) =
  toolMessageCondition(this, "neq", value)

infix fun String.gt(value: Boolean) =
  toolMessageCondition(this, "gt", value)

infix fun String.gte(value: Boolean) =
  toolMessageCondition(this, "gte", value)

infix fun String.lt(value: Boolean) =
  toolMessageCondition(this, "lt", value)

infix fun String.lte(value: Boolean) =
  toolMessageCondition(this, "lte", value)

internal val ToolMessageType.isMatching: (ToolMessageDto) -> Boolean
  get() = { it.type == type && it.conditions.isEmpty() }
