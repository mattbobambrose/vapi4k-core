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

package com.vapi4k.dbms

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.kotlin.datetime.duration

object Tables

object MessagesTable : IntIdTable("vapi4k.messages") {
  val created = datetime("created")
  val messageType = text("message_type")
  val requestType = text("request_type")

  //  val messageJsonb = jsonb("message_jsonb", format)
  val messageJsonb = text("message_jsonb")
  val messageJson = text("message_json")
  val elapsedTime = duration("elapsed_time")
}
