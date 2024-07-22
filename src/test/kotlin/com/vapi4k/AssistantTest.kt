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

package com.vapi4k

import com.vapi4k.dsl.assistant.AssistantDsl.assistant
import com.vapi4k.dsl.assistant.enums.AssistantClientMessageType
import com.vapi4k.dsl.assistant.enums.AssistantServerMessageType
import com.vapi4k.dsl.assistant.enums.DeepgramModelType
import com.vapi4k.dsl.assistant.enums.FirstMessageModeType.ASSISTANT_SPEAKS_FIRST_WITH_MODEL_GENERATED_MODEL
import com.vapi4k.dsl.assistant.enums.ToolMessageType
import com.vapi4k.dsl.assistant.eq
import com.vapi4k.dsl.assistant.tools.ToolCache.resetCaches
import com.vapi4k.dsl.vapi4k.Vapi4kConfig
import com.vapi4k.utils.JsonElementUtils.assistantClientMessages
import com.vapi4k.utils.JsonElementUtils.assistantServerMessages
import com.vapi4k.utils.JsonUtils.get
import com.vapi4k.utils.JsonUtils.stringValue
import com.vapi4k.utils.JsonUtils.toJsonElement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import kotlin.test.Test

class AssistantTest {
  init {
    Vapi4kConfig().apply {
      configure {
        serverUrl = "HelloWorld"
        serverUrlSecret = "12345"
      }
    }
  }

  val messageOne = "Hi there test"
  val prov = "openai"
  val mod = "gpt-3.5-turbo"
  val sysMessage = "You are the test systemMessage voice"
  val startMessage = "This is the test request start message"
  val completeMessage = "This is the test request complete message"
  val failedMessage = "This is the test request failed message"
  val delayedMessage = "This is the test request delayed message"
  val secondStartMessage = "This is the second test request start message"
  val secondCompleteMessage = "This is the second test request complete message"
  val secondFailedMessage = "This is the second test request failed message"
  val secondDelayedMessage = "This is the second test request delayed message"
  val chicagoIllinoisStartMessage = "This is the Chicago Illinois request start message"
  val chicagoIllinoisCompleteMessage = "This is the Chicago Illinois request complete message"
  val chicagoIllinoisFailedMessage = "This is the Chicago Illinois request failed message"
  val chicagoIllinoisDelayedMessage = "This is the Chicago Illinois request delayed message"

  @Test
  fun testRegular() {
    val request = assistantRequest.toJsonElement()
    resetCaches()
    val assistant = assistant(request) {
      firstMessage = messageOne
      model {
        provider = prov
        model = mod

        systemMessage = sysMessage
        tools {
          tool(WeatherLookupService0()) {
            requestStartMessage = startMessage
            requestCompleteMessage = completeMessage
            requestFailedMessage = failedMessage
            requestDelayedMessage = delayedMessage
            delayedMillis = 2000
          }
        }
      }
    }
    assert(assistant.assistantDto.modelDto.tools.first().messages.single { it.type == ToolMessageType.REQUEST_START.type }.content == "This is the test request start message")
  }

  @Test
  fun `test reverse delay order`() {
    val request = assistantRequest.toJsonElement()
    resetCaches()
    val assistant = assistant(request) {
      firstMessage = messageOne
      model {
        provider = prov
        model = mod

        systemMessage = sysMessage
        tools {
          tool(WeatherLookupService0()) {
            requestStartMessage = startMessage
            requestCompleteMessage = completeMessage
            requestFailedMessage = failedMessage
            delayedMillis = 2000
            requestDelayedMessage = delayedMessage
          }
        }
      }
    }
    with(assistant.assistantDto.modelDto.tools.first().messages.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.type }) {
      assert(content == delayedMessage)
      assert(timingMilliseconds == 2000)
    }
  }

  @Test
  fun `test message with no millis`() {
    val request = assistantRequest.toJsonElement()
    resetCaches()
    val assistant = assistant(request) {
      firstMessage = messageOne
      model {
        provider = prov
        model = mod

        systemMessage = sysMessage
        tools {
          tool(WeatherLookupService0()) {
            requestStartMessage = startMessage
            requestCompleteMessage = completeMessage
            requestFailedMessage = failedMessage
            requestDelayedMessage = delayedMessage
          }
        }
      }
    }
    assert(assistant.assistantDto.modelDto.tools.first().messages.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.type }.timingMilliseconds == -1)
  }

  @Test
  fun `test defective with no delayed message`() {
    val request = assistantRequest.toJsonElement()
    resetCaches()
    val exception =
      assertThrows(IllegalStateException::class.java) {
        assistant(request) {
          firstMessage = messageOne
          model {
            provider = prov
            model = mod

            systemMessage = sysMessage
            tools {
              tool(WeatherLookupService0()) {
                requestStartMessage = startMessage
                requestCompleteMessage = completeMessage
                requestFailedMessage = failedMessage
                delayedMillis = 2000
              }
            }
          }
        }
      }
    assertEquals("delayedMillis must be set when using requestDelayedMessage", exception.message)
  }

  @Test
  fun `multiple message`() {
    val request = assistantRequest.toJsonElement()
    resetCaches()
    val assistant = assistant(request) {
      firstMessage = messageOne
      model {
        provider = prov
        model = mod

        systemMessage = sysMessage
        tools {
          tool(WeatherLookupService0()) {
            requestStartMessage = startMessage
            requestCompleteMessage = completeMessage
            requestFailedMessage = failedMessage
            requestDelayedMessage = delayedMessage
            requestDelayedMessage = secondDelayedMessage
            delayedMillis = 2000
          }
        }
      }
    }
    with(assistant.assistantDto.modelDto.tools.first().messages.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.type }) {
      assertEquals(content, secondDelayedMessage)
    }
  }

  @Test
  fun `multiple message unordered`() {
    val request = assistantRequest.toJsonElement()
    resetCaches()
    val assistant = assistant(request) {
      firstMessage = messageOne
      model {
        provider = prov
        model = mod

        systemMessage = sysMessage
        tools {
          tool(WeatherLookupService0()) {
            requestStartMessage = startMessage
            requestCompleteMessage = completeMessage
            requestFailedMessage = failedMessage
            requestDelayedMessage = delayedMessage
            delayedMillis = 2000
            requestDelayedMessage = secondDelayedMessage
          }
        }
      }
    }
    with(assistant.assistantDto.modelDto.tools.first().messages.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.type }) {
      assertEquals(content, secondDelayedMessage)
    }
  }

  @Test
  fun `multiple delay time`() {
    val request = assistantRequest.toJsonElement()
    resetCaches()
    val assistant = assistant(request) {
      firstMessage = messageOne
      model {
        provider = prov
        model = mod

        systemMessage = sysMessage
        tools {
          tool(WeatherLookupService0()) {
            requestStartMessage = startMessage
            requestCompleteMessage = completeMessage
            requestFailedMessage = failedMessage
            requestDelayedMessage = delayedMessage
            delayedMillis = 2000
            delayedMillis = 1000
          }
        }
      }
    }
    with(assistant.assistantDto.modelDto.tools.first().messages.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.type }) {
      assertEquals(timingMilliseconds, 1000)
    }
  }

  @Test
  fun `multiple message multiple delay time`() {
    val request = assistantRequest.toJsonElement()
    resetCaches()
    val assistant = assistant(request) {
      firstMessage = messageOne
      model {
        provider = prov
        model = mod

        systemMessage = sysMessage
        tools {
          tool(WeatherLookupService0()) {
            requestStartMessage = startMessage
            requestCompleteMessage = completeMessage
            requestFailedMessage = failedMessage
            requestDelayedMessage = delayedMessage
            requestDelayedMessage = secondDelayedMessage
            delayedMillis = 2000
            delayedMillis = 1000
          }
        }
      }
    }
    with(assistant.assistantDto.modelDto.tools.first().messages.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.type }) {
      assertEquals(content, secondDelayedMessage)
      assertEquals(timingMilliseconds, 1000)
    }
  }

  @Test
  fun `chicago illinois message`() {
    val request = assistantRequest.toJsonElement()
    resetCaches()
    val assistant = assistant(request) {
      firstMessage = messageOne
      model {
        provider = prov
        model = mod

        systemMessage = sysMessage
        tools {
          tool(WeatherLookupService0()) {
            condition("city" eq "Chicago", "state" eq "Illinois") {
              requestStartMessage = chicagoIllinoisStartMessage
              requestCompleteMessage = chicagoIllinoisCompleteMessage
              requestFailedMessage = chicagoIllinoisFailedMessage
              requestDelayedMessage = chicagoIllinoisDelayedMessage
              delayedMillis = 2000
            }
            requestStartMessage = startMessage
            requestCompleteMessage = completeMessage
            requestFailedMessage = failedMessage
            requestDelayedMessage = delayedMessage
            delayedMillis = 1000
          }
        }
      }
    }
    with(assistant.assistantDto.modelDto) {
      val msgs = tools.first().messages
      with(msgs.single { it.type == ToolMessageType.REQUEST_START.type && it.conditions.isEmpty() }) {
        assertEquals(startMessage, content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_COMPLETE.type && it.conditions.isEmpty() }) {
        assertEquals(completeMessage, content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_FAILED.type && it.conditions.isEmpty() }) {
        assertEquals(failedMessage, content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.type && it.conditions.isEmpty() }) {
        assertEquals(delayedMessage, content)
        assertEquals(1000, timingMilliseconds)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_START.type && it.conditions.isNotEmpty() }) {
        assertEquals(chicagoIllinoisStartMessage, content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_COMPLETE.type && it.conditions.isNotEmpty() }) {
        assertEquals(chicagoIllinoisCompleteMessage, content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_FAILED.type && it.conditions.isNotEmpty() }) {
        assertEquals(chicagoIllinoisFailedMessage, content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.type && it.conditions.isNotEmpty() }) {
        assertEquals(chicagoIllinoisDelayedMessage, content)
        assertEquals(2000, timingMilliseconds)
      }
    }
  }

  @Test
  fun `chicago illinois message reverse conditions 1`() {
    val request = assistantRequest.toJsonElement()
    resetCaches()
    val assistant = assistant(request) {
      firstMessage = messageOne
      model {
        provider = prov
        model = mod

        systemMessage = sysMessage
        tools {
          tool(WeatherLookupService0()) {
            condition("city" eq "Chicago", "state" eq "Illinois") {
              requestStartMessage = chicagoIllinoisStartMessage
              requestCompleteMessage = chicagoIllinoisCompleteMessage
              requestFailedMessage = chicagoIllinoisFailedMessage
              requestDelayedMessage = chicagoIllinoisDelayedMessage
              delayedMillis = 2000
            }
            condition("state" eq "Illinois", "city" eq "Chicago") {
              requestStartMessage = chicagoIllinoisStartMessage + "2"
              requestCompleteMessage = chicagoIllinoisCompleteMessage + "2"
              requestFailedMessage = chicagoIllinoisFailedMessage + "2"
              requestDelayedMessage = chicagoIllinoisDelayedMessage + "2"
              delayedMillis = 3000
            }
            requestStartMessage = startMessage
            requestCompleteMessage = completeMessage
            requestFailedMessage = failedMessage
            requestDelayedMessage = delayedMessage
            delayedMillis = 1000
          }
        }
      }
    }
    with(assistant.assistantDto.modelDto) {
      val msgs = tools.first().messages
      with(msgs.single { it.type == ToolMessageType.REQUEST_START.type && it.conditions.isEmpty() }) {
        assertEquals(startMessage, content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_COMPLETE.type && it.conditions.isEmpty() }) {
        assertEquals(completeMessage, content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_FAILED.type && it.conditions.isEmpty() }) {
        assertEquals(failedMessage, content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.type && it.conditions.isEmpty() }) {
        assertEquals(delayedMessage, content)
        assertEquals(1000, timingMilliseconds)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_START.type && it.conditions.isNotEmpty() }) {
        assertEquals(chicagoIllinoisStartMessage + "2", content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_COMPLETE.type && it.conditions.isNotEmpty() }) {
        assertEquals(chicagoIllinoisCompleteMessage + "2", content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_FAILED.type && it.conditions.isNotEmpty() }) {
        assertEquals(chicagoIllinoisFailedMessage + "2", content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.type && it.conditions.isNotEmpty() }) {
        assertEquals(chicagoIllinoisDelayedMessage + "2", content)
        assertEquals(3000, timingMilliseconds)
      }
    }
  }

  @Test
  fun `chicago illinois message reverse conditions 2`() {
    val request = assistantRequest.toJsonElement()
    resetCaches()
    val assistant = assistant(request) {
      firstMessage = messageOne
      model {
        provider = prov
        model = mod

        systemMessage = sysMessage
        tools {
          tool(WeatherLookupService0()) {
            condition("city" eq "Chicago", "state" eq "Illinois") {
              requestStartMessage = chicagoIllinoisStartMessage
              requestCompleteMessage = chicagoIllinoisCompleteMessage
              requestFailedMessage = chicagoIllinoisFailedMessage
              requestDelayedMessage = chicagoIllinoisDelayedMessage
              delayedMillis = 2000
            }
            condition("state" eq "Illinois", "city" eq "Chicago") {
              requestStartMessage = chicagoIllinoisStartMessage + "2"
              requestCompleteMessage = chicagoIllinoisCompleteMessage + "2"
              requestFailedMessage = chicagoIllinoisFailedMessage + "2"
              requestDelayedMessage = chicagoIllinoisDelayedMessage + "2"
              delayedMillis = 3000
            }
            requestStartMessage = startMessage
            requestCompleteMessage = completeMessage
            requestFailedMessage = failedMessage
            requestDelayedMessage = delayedMessage
            delayedMillis = 1000
          }
        }
      }
    }

    with(assistant.assistantDto.modelDto.tools.first().messages.single { it.type == ToolMessageType.REQUEST_START.type && it.conditions.isEmpty() }) {
      assertEquals(startMessage, content)
    }
    with(assistant.assistantDto.modelDto.tools.first().messages.single { it.type == ToolMessageType.REQUEST_COMPLETE.type && it.conditions.isEmpty() }) {
      assertEquals(completeMessage, content)
    }
    with(assistant.assistantDto.modelDto.tools.first().messages.single { it.type == ToolMessageType.REQUEST_FAILED.type && it.conditions.isEmpty() }) {
      assertEquals(failedMessage, content)
    }
    with(assistant.assistantDto.modelDto.tools.first().messages.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.type && it.conditions.isEmpty() }) {
      assertEquals(delayedMessage, content)
      assertEquals(1000, timingMilliseconds)
    }
    with(assistant.assistantDto.modelDto.tools.first().messages.single { it.type == ToolMessageType.REQUEST_START.type && it.conditions.isNotEmpty() }) {
      assertEquals(chicagoIllinoisStartMessage + "2", content)
    }
    with(assistant.assistantDto.modelDto.tools.first().messages.single { it.type == ToolMessageType.REQUEST_COMPLETE.type && it.conditions.isNotEmpty() }) {
      assertEquals(chicagoIllinoisCompleteMessage + "2", content)
    }
    with(assistant.assistantDto.modelDto.tools.first().messages.single { it.type == ToolMessageType.REQUEST_FAILED.type && it.conditions.isNotEmpty() }) {
      assertEquals(chicagoIllinoisFailedMessage + "2", content)
    }
    with(assistant.assistantDto.modelDto.tools.first().messages.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.type && it.conditions.isNotEmpty() }) {
      assertEquals(chicagoIllinoisDelayedMessage + "2", content)
      assertEquals(3000, timingMilliseconds)
    }
  }

  @Test
  fun `check non-default FirstMessageModeType values`() {
    val request = assistantRequest.toJsonElement()
    resetCaches()
    val assistant = assistant(request) {
      firstMessageMode = ASSISTANT_SPEAKS_FIRST_WITH_MODEL_GENERATED_MODEL
    }

    val element = assistant.toJsonElement()
    assertEquals(
      ASSISTANT_SPEAKS_FIRST_WITH_MODEL_GENERATED_MODEL.desc,
      element["assistant.firstMessageMode"].stringValue
    )
  }

  @Test
  fun `check assistant client messages 1`() {
    val request = assistantRequest.toJsonElement()
    resetCaches()
    val assistant = assistant(request) {
      clientMessages -= AssistantClientMessageType.HANG
    }

    val element = assistant.toJsonElement()
    assertEquals(9, element.assistantClientMessages.size)
  }

  @Test
  fun `check assistant client messages 2`() {
    val request = assistantRequest.toJsonElement()
    resetCaches()
    val assistant = assistant(request) {
      clientMessages -= setOf(AssistantClientMessageType.HANG, AssistantClientMessageType.STATUS_UPDATE)
    }

    val element = assistant.toJsonElement()
    assertEquals(8, element.assistantClientMessages.size)
  }

  @Test
  fun `check assistant server messages 1`() {
    val request = assistantRequest.toJsonElement()
    resetCaches()
    val assistant = assistant(request) {
      serverMessages -= AssistantServerMessageType.HANG
    }

    val element = assistant.toJsonElement()
    assertEquals(8, element.assistantServerMessages.size)
  }

  @Test
  fun `check assistant server messages 2`() {
    val request = assistantRequest.toJsonElement()
    resetCaches()
    val assistant = assistant(request) {
      serverMessages -= setOf(AssistantServerMessageType.HANG, AssistantServerMessageType.SPEECH_UPDATE)
    }

    val element = assistant.toJsonElement()
    assertEquals(7, element.assistantServerMessages.size)
  }

  val assistantRequest = """
    {
    "message": {
        "type": "assistant-request",
        "call": {
            "id": "00dbe917-37fd-4d3f-8cc0-ac6be0923f40",
            "orgId": "679a13ec-f40d-4055-8959-797c4ee1694b"
        },
        "timestamp": "2024-07-13T21:27:59.870Z"
    }
}
  """

  @Test
  fun `multiple transcriber decls`() {
    val request = assistantRequest.toJsonElement()
    assertThrows(IllegalStateException::class.java) {
      val assistant = assistant(request) {
        deepGramTranscriber {
          this.transcriberModel = DeepgramModelType.BASE
        }

        deepGramTranscriber {
          this.transcriberModel = DeepgramModelType.BASE
        }
      }
    }

  }
}
