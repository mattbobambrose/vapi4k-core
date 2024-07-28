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
import com.vapi4k.dsl.assistant.enums.FirstMessageModeType.ASSISTANT_SPEAKS_FIRST_WITH_MODEL_GENERATED_MODEL
import com.vapi4k.dsl.assistant.eq
import com.vapi4k.dsl.model.enums.DeepgramModelType
import com.vapi4k.dsl.model.enums.GladiaModelType
import com.vapi4k.dsl.model.enums.OpenAIModelType
import com.vapi4k.dsl.tools.ToolCache.resetCaches
import com.vapi4k.dsl.tools.enums.ToolMessageType
import com.vapi4k.dsl.transcriber.enums.DeepgramLanguageType
import com.vapi4k.dsl.transcriber.enums.TalkscriberModelType
import com.vapi4k.dsl.vapi4k.Vapi4kConfig
import com.vapi4k.utils.JsonElementUtils.assistantClientMessages
import com.vapi4k.utils.JsonElementUtils.assistantServerMessages
import com.vapi4k.utils.TestUtils.withTestApplication
import com.vapi4k.utils.get
import com.vapi4k.utils.intValue
import com.vapi4k.utils.jsonElementList
import com.vapi4k.utils.stringValue
import com.vapi4k.utils.toJsonElement
import kotlinx.serialization.json.JsonElement
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

  fun JsonElement.tools() = get("assistant.model.tools").jsonElementList
  fun JsonElement.firstMessage() = tools().first()["messages"].jsonElementList
  fun JsonElement.firstMessageOfType(type: ToolMessageType) =
    firstMessage().single { it.stringValue("type") == type.desc }

  @Test
  fun testRegular() {
    resetCaches()
    val (response, jsonElement) =
      withTestApplication("/json/assistantRequest.json") { request ->
        assistant(request) {
          firstMessage = messageOne
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO

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
      }

    assertEquals(
      "This is the test request start message",
      jsonElement.firstMessageOfType(ToolMessageType.REQUEST_START).stringValue("content")
    )
  }

  @Test
  fun `test reverse delay order`() {
    resetCaches()
    val (response, jsonElement) =
      withTestApplication("/json/assistantRequest.json") { request ->
        assistant(request) {
          firstMessage = messageOne
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO

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
      }

    with(jsonElement.firstMessageOfType(ToolMessageType.REQUEST_RESPONSE_DELAYED)) {
      assertEquals(delayedMessage, stringValue("content"))
      assertEquals(2000, intValue("timingMilliseconds"))
    }
  }

  @Test
  fun `test message with no millis`() {
    resetCaches()
    val (response, jsonElement) =
      withTestApplication("/json/assistantRequest.json") { request ->
        assistant(request) {
          firstMessage = messageOne
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO
            systemMessage = sysMessage
            tools {
              tool(WeatherLookupService0()) {
                requestStartMessage = startMessage
                requestCompleteMessage = completeMessage
                requestFailedMessage = failedMessage
                requestDelayedMessage = delayedMessage
                delayedMillis = 99
              }
            }
          }
        }
      }
    assertEquals(
      99, jsonElement.firstMessageOfType(ToolMessageType.REQUEST_RESPONSE_DELAYED).intValue("timingMilliseconds")
    )
  }

  @Test
  fun `test defective with no delayed message`() {
    resetCaches()
    val request = assistantRequest.toJsonElement()
    val exception =
      assertThrows(IllegalStateException::class.java) {
        assistant(request) {
          firstMessage = messageOne
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO

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
    resetCaches()
    val (response, jsonElement) =
      withTestApplication("/json/assistantRequest.json") { request ->
        assistant(request) {
          firstMessage = messageOne
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO

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
      }

    with(jsonElement.firstMessageOfType(ToolMessageType.REQUEST_RESPONSE_DELAYED)) {
      assertEquals(secondDelayedMessage, stringValue("content"))
      assertEquals(2000, intValue("timingMilliseconds"))
    }
  }

  @Test
  fun `multiple message unordered`() {
    resetCaches()
    val request = assistantRequest.toJsonElement()
    val assistant = assistant(request) {
      firstMessage = messageOne
      openAIModel {
        modelType = OpenAIModelType.GPT_3_5_TURBO

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
    with(assistant.assistantDto.modelDto!!.tools.first().messages.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.desc }) {
      assertEquals(content, secondDelayedMessage)
    }
  }

  @Test
  fun `multiple delay time`() {
    resetCaches()
    val request = assistantRequest.toJsonElement()
    val assistant = assistant(request) {
      firstMessage = messageOne
      openAIModel {
        modelType = OpenAIModelType.GPT_3_5_TURBO

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
    with(assistant.assistantDto.modelDto!!.tools.first().messages.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.desc }) {
      assertEquals(timingMilliseconds, 1000)
    }
  }

  @Test
  fun `multiple message multiple delay time`() {
    resetCaches()
    val request = assistantRequest.toJsonElement()
    val assistant = assistant(request) {
      firstMessage = messageOne
      openAIModel {
        modelType = OpenAIModelType.GPT_3_5_TURBO
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
    with(assistant.assistantDto.modelDto!!.tools.first().messages.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.desc }) {
      assertEquals(content, secondDelayedMessage)
      assertEquals(timingMilliseconds, 1000)
    }
  }

  @Test
  fun `chicago illinois message`() {
    resetCaches()
    val request = assistantRequest.toJsonElement()
    val assistant = assistant(request) {
      firstMessage = messageOne
      openAIModel {
        modelType = OpenAIModelType.GPT_3_5_TURBO
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
    with(assistant.assistantDto.modelDto!!) {
      val msgs = tools.first().messages
      with(msgs.single { it.type == ToolMessageType.REQUEST_START.desc && it.conditions.isEmpty() }) {
        assertEquals(startMessage, content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_COMPLETE.desc && it.conditions.isEmpty() }) {
        assertEquals(completeMessage, content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_FAILED.desc && it.conditions.isEmpty() }) {
        assertEquals(failedMessage, content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.desc && it.conditions.isEmpty() }) {
        assertEquals(delayedMessage, content)
        assertEquals(1000, timingMilliseconds)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_START.desc && it.conditions.isNotEmpty() }) {
        assertEquals(chicagoIllinoisStartMessage, content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_COMPLETE.desc && it.conditions.isNotEmpty() }) {
        assertEquals(chicagoIllinoisCompleteMessage, content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_FAILED.desc && it.conditions.isNotEmpty() }) {
        assertEquals(chicagoIllinoisFailedMessage, content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.desc && it.conditions.isNotEmpty() }) {
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
      openAIModel {
        modelType = OpenAIModelType.GPT_3_5_TURBO

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
    with(assistant.assistantDto.modelDto!!) {
      val msgs = tools.first().messages
      with(msgs.single { it.type == ToolMessageType.REQUEST_START.desc && it.conditions.isEmpty() }) {
        assertEquals(startMessage, content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_COMPLETE.desc && it.conditions.isEmpty() }) {
        assertEquals(completeMessage, content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_FAILED.desc && it.conditions.isEmpty() }) {
        assertEquals(failedMessage, content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.desc && it.conditions.isEmpty() }) {
        assertEquals(delayedMessage, content)
        assertEquals(1000, timingMilliseconds)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_START.desc && it.conditions.isNotEmpty() }) {
        assertEquals(chicagoIllinoisStartMessage + "2", content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_COMPLETE.desc && it.conditions.isNotEmpty() }) {
        assertEquals(chicagoIllinoisCompleteMessage + "2", content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_FAILED.desc && it.conditions.isNotEmpty() }) {
        assertEquals(chicagoIllinoisFailedMessage + "2", content)
      }
      with(msgs.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.desc && it.conditions.isNotEmpty() }) {
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
      openAIModel {
        modelType = OpenAIModelType.GPT_3_5_TURBO

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

    with(assistant.assistantDto.modelDto!!) {
      with(tools.first().messages.single { it.type == ToolMessageType.REQUEST_START.desc && it.conditions.isEmpty() }) {
        assertEquals(startMessage, content)
      }
      with(tools.first().messages.single { it.type == ToolMessageType.REQUEST_COMPLETE.desc && it.conditions.isEmpty() }) {
        assertEquals(completeMessage, content)
      }
      with(tools.first().messages.single { it.type == ToolMessageType.REQUEST_FAILED.desc && it.conditions.isEmpty() }) {
        assertEquals(failedMessage, content)
      }
      with(tools.first().messages.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.desc && it.conditions.isEmpty() }) {
        assertEquals(delayedMessage, content)
        assertEquals(1000, timingMilliseconds)
      }
      with(tools.first().messages.single { it.type == ToolMessageType.REQUEST_START.desc && it.conditions.isNotEmpty() }) {
        assertEquals(chicagoIllinoisStartMessage + "2", content)
      }
      with(tools.first().messages.single { it.type == ToolMessageType.REQUEST_COMPLETE.desc && it.conditions.isNotEmpty() }) {
        assertEquals(chicagoIllinoisCompleteMessage + "2", content)
      }
      with(tools.first().messages.single { it.type == ToolMessageType.REQUEST_FAILED.desc && it.conditions.isNotEmpty() }) {
        assertEquals(chicagoIllinoisFailedMessage + "2", content)
      }
      with(tools.first().messages.single { it.type == ToolMessageType.REQUEST_RESPONSE_DELAYED.desc && it.conditions.isNotEmpty() }) {
        assertEquals(chicagoIllinoisDelayedMessage + "2", content)
        assertEquals(3000, timingMilliseconds)
      }
    }
  }

  @Test
  fun `check non-default FirstMessageModeType values`() {
    resetCaches()
    val request = assistantRequest.toJsonElement()
    val assistant =
      assistant(request) {
        firstMessageMode = ASSISTANT_SPEAKS_FIRST_WITH_MODEL_GENERATED_MODEL
        openAIModel {
          modelType = OpenAIModelType.GPT_3_5_TURBO
        }
      }

    val element = assistant.toJsonElement()
    assertEquals(
      ASSISTANT_SPEAKS_FIRST_WITH_MODEL_GENERATED_MODEL.desc,
      element.stringValue("assistant.firstMessageMode")
    )
  }

  @Test
  fun `check assistant client messages 1`() {
    resetCaches()
    val request = assistantRequest.toJsonElement()
    val assistant =
      assistant(request) {
        clientMessages -= AssistantClientMessageType.HANG
        openAIModel {
          modelType = OpenAIModelType.GPT_3_5_TURBO
        }
      }

    val element = assistant.toJsonElement()
    assertEquals(9, element.assistantClientMessages.size)
  }

  @Test
  fun `check assistant client messages 2`() {
    resetCaches()
    val request = assistantRequest.toJsonElement()
    val assistant =
      assistant(request) {
        clientMessages -= setOf(AssistantClientMessageType.HANG, AssistantClientMessageType.STATUS_UPDATE)
        openAIModel {
          modelType = OpenAIModelType.GPT_3_5_TURBO
        }
      }

    val element = assistant.toJsonElement()
    assertEquals(8, element.assistantClientMessages.size)
  }

  @Test
  fun `check assistant server messages 1`() {
    resetCaches()
    val request = assistantRequest.toJsonElement()
    val assistant =
      assistant(request) {
        firstMessage = "Something"
        serverMessages -= AssistantServerMessageType.HANG
        openAIModel {
          modelType = OpenAIModelType.GPT_3_5_TURBO
        }
      }

    val element = assistant.toJsonElement()
    assertEquals(8, element.assistantServerMessages.size)
  }

  @Test
  fun `check assistant server messages 2`() {
    resetCaches()
    val request = assistantRequest.toJsonElement()
    val assistant =
      assistant(request) {
        serverMessages -= setOf(AssistantServerMessageType.HANG, AssistantServerMessageType.SPEECH_UPDATE)
        openAIModel {
          modelType = OpenAIModelType.GPT_3_5_TURBO
        }
      }

    val element = assistant.toJsonElement()
    assertEquals(7, element.assistantServerMessages.size)
  }

  @Test
  fun `multiple deepgram transcriber decls`() {
    assertThrows(IllegalStateException::class.java) {
      val request = assistantRequest.toJsonElement()
      assistant(request) {
        deepgramTranscriber {
          transcriberModel = DeepgramModelType.BASE
        }

        deepgramTranscriber {
          transcriberModel = DeepgramModelType.BASE
        }
      }
    }
  }

  @Test
  fun `multiple gladia transcriber decls`() {
    assertThrows(IllegalStateException::class.java) {
      val request = assistantRequest.toJsonElement()
      assistant(request) {
        gladiaTranscriber {
          transcriberModel = GladiaModelType.FAST
        }

        gladiaTranscriber {
          transcriberModel = GladiaModelType.FAST
        }
      }
    }
  }

  @Test
  fun `multiple talkscriber transcriber decls`() {
    assertThrows(IllegalStateException::class.java) {
      val request = assistantRequest.toJsonElement()
      assistant(request) {
        talkscriberTranscriber {
          transcriberModel = TalkscriberModelType.WHISPER
        }

        talkscriberTranscriber {
          transcriberModel = TalkscriberModelType.WHISPER
        }
      }
    }
  }

  @Test
  fun `multiple transcriber decls`() {
    assertThrows(IllegalStateException::class.java) {
      val request = assistantRequest.toJsonElement()
      assistant(request) {
        talkscriberTranscriber {
          transcriberModel = TalkscriberModelType.WHISPER
        }

        gladiaTranscriber {
          transcriberModel = GladiaModelType.FAST
        }
      }
    }
  }

  @Test
  fun `deepgram transcriber enum value`() {
    val request = assistantRequest.toJsonElement()
    val assistant =
      assistant(request) {
        openAIModel {
          modelType = OpenAIModelType.GPT_3_5_TURBO
        }
        deepgramTranscriber {
          transcriberModel = DeepgramModelType.BASE
          transcriberLanguage = DeepgramLanguageType.GERMAN
        }
      }
    val je = assistant.toJsonElement()
    assertEquals(
      DeepgramLanguageType.GERMAN.desc,
      je["assistant.transcriber.language"].stringValue
    )
  }

  @Test
  fun `deepgram transcriber custom value`() {
    val request = assistantRequest.toJsonElement()
    val assistant =
      assistant(request) {
        openAIModel {
          modelType = OpenAIModelType.GPT_3_5_TURBO
        }

        deepgramTranscriber {
          transcriberModel = DeepgramModelType.BASE
          customLanguage = "zzz"
        }
      }
    val jsonElement = assistant.toJsonElement()
    assertEquals(
      "zzz",
      jsonElement["assistant.transcriber.language"].stringValue
    )
  }

  @Test
  fun `deepgram transcriber conflicting values`() {
    assertThrows(IllegalStateException::class.java) {
      val request = assistantRequest.toJsonElement()
      val assistant =
        assistant(request) {
          openAIModel {
            modelType = OpenAIModelType.GPT_3_5_TURBO
          }

          deepgramTranscriber {
            transcriberModel = DeepgramModelType.BASE
            transcriberLanguage = DeepgramLanguageType.GERMAN
            customLanguage = "zzz"
          }
        }
      val je = assistant.toJsonElement()
      assertEquals(
        "zzz",
        je["assistant.transcriber.language"].stringValue
      )
    }
  }

  @Test
  fun `missing model decl`() {
    assertThrows(IllegalStateException::class.java) {
      val request = assistantRequest.toJsonElement()
      assistant(request) {
        firstMessage = "Something"
      }
    }
  }

  companion object {
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

  }
}
