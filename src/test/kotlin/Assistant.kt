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

import com.vapi4k.dsl.assistant.AssistantDsl.assistant
import com.vapi4k.dsl.assistant.AssistantDsl.assistantId
import com.vapi4k.dsl.assistant.AssistantServerMessageType
import com.vapi4k.dsl.assistant.eq
import com.vapi4k.responses.AssistantRequestResponse
import com.vapi4k.utils.JsonElementUtils.phoneNumber
import kotlinx.serialization.json.JsonElement

fun myAssistantRequest(
  ibc: JsonElement,
): AssistantRequestResponse =
  (when (ibc.phoneNumber) {
    "+14156721042" -> assistantId("44792a91-d7f9-4915-9445-0991aeef97bc")
    "+19256836490" -> getAssistant("Matthew")
    "+17039395869" -> getAssistant("Justin")
    "+14249039001" -> getAssistant("Renee")
    "+15168539128" -> getAssistant("Kyla")
    "+18675986778" -> getAssistant("Bella")
    "+17868322287" -> getAssistant("Bella")
    else -> getAssistant("")
  })

fun getAssistant(
  callerName: String = "Bill",
) =
  assistant {
    firstMessage =
      """
            Hi there. My name is Ellen. I'd like to collect some information from you
            today. Is that alright?
            """
    serverMessages -= setOf(
      AssistantServerMessageType.CONVERSATION_UPDATE,
      AssistantServerMessageType.SPEECH_UPDATE
    )

    model {
      provider = "openai"
      model = "gpt-4-turbo"

      systemMessage = """
            [Identity]
            You are the friendly and helpful voice of EO Care. Your goal is to collect the name of
            the user.

            [Style]
            - Be friendly and concise.

            [Response Guideline]
            - Present dates in a clear format (e.g., January 15, 2024).

            [Task]
            1. Ask for the user's first and last name. If they only give one, ask which name that was
            and ask for the other as well.
            <wait for user response>
            2. Confirm the user's name.
            3. Ask for the user's age, favorite color, and favorite food.
            <wait for user response>
            4. If they do not give all three, ask for the missing information. Keep asking until
            they give all of the information.
        """

      functions {
        function(WeatherLookupService0())
        function(WeatherLookupService3())
      }

//      tools {
//        tool(NameService())
//      }
//
//      functions {
//        function(ManyInfoService())
//      }

      tools {
        tool(WeatherLookupService1()) {
          condition("city" eq "Chicago", "state" eq "Illinois") {
            requestStartMessage = "This is the Chicago Illinois start message"
            requestCompleteMessage = "This is the Chicago Illinois complete message"
            requestFailedMessage = "This is the Chicago Illinois failed message"
            requestDelayedMessage = "This is the Chicago Illinois delayed message"
            delayedMillis = 2000
          }
          requestStartMessage = "This is the default start message"
          requestCompleteMessage = "This is the default complete message"
          requestFailedMessage = "This is the default failed message"
          requestDelayedMessage = "This is the default delayed message"
          delayedMillis = 1000

          condition("city" eq "Chicago") {
            requestStartMessage = "This is the Chicago start message"
          }
          condition("city" eq "Houston") {
            requestStartMessage = "This is the Houston start message"
          }
        }

        tool(WeatherLookupService2()) {
          condition("city" eq "Chicago") {
            requestStartMessage = "This is the Chicago start message"
          }
          condition("city" eq "Houston") {
            requestStartMessage = "This is the Houston start message"
          }
          requestCompleteMessage = "This is the default complete message"
          requestFailedMessage = "This is the default failed message"
          requestDelayedMessage = "This is the default delayed message"
          delayedMillis = 1000
        }
      }
    }
  }
