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

import com.vapi4k.api.vapi4k.enums.ServerRequestType.ASSISTANT_REQUEST
import com.vapi4k.api.vapi4k.enums.ServerRequestType.FUNCTION_CALL
import com.vapi4k.api.vapi4k.enums.ServerRequestType.STATUS_UPDATE
import com.vapi4k.api.vapi4k.enums.ServerRequestType.TOOL_CALL
import com.vapi4k.api.vapi4k.utils.AssistantRequestUtils.hasStatusUpdateError
import com.vapi4k.api.vapi4k.utils.AssistantRequestUtils.requestType
import com.vapi4k.api.vapi4k.utils.AssistantRequestUtils.statusUpdateError
import com.vapi4k.api.vapi4k.utils.JsonElementUtils.toJsonString
import com.vapi4k.common.CoreEnvVars.isProduction
import com.vapi4k.dbms.Messages.insertRequest
import com.vapi4k.dbms.Messages.insertResponse
import com.vapi4k.server.Vapi4k
import com.vapi4k.server.Vapi4kServer.logger
import com.vapi4k.server.defaultKtorConfig
import com.vapi4k.utils.DslUtils.logObject
import com.vapi4k.utils.DslUtils.printObject
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry

fun main() {
  embeddedServer(
    factory = CIO,
    port = 8080,
    host = "0.0.0.0",
    module = Application::module,
  ).start(wait = true)
}

fun Application.module() {
  val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
  defaultKtorConfig(appMicrometerRegistry)

  install(Vapi4k) {
    vapi4kApplication {
      onAssistantRequest {
        myAssistantRequest()
      }
    }

    vapi4kApplication {
      serverPath = "/inboundRequest"
      serverSecret = "12345"

      onAssistantRequest {
        myAssistantRequest()
      }

      onAllRequests { request ->
        logger.info { "All requests: ${request.requestType}" }
        if (isProduction)
          insertRequest(request)
        logObject(request)
        printObject(request)
      }

      onRequest(TOOL_CALL) { request ->
        logger.info { "Tool call: $request" }
      }

      onRequest(FUNCTION_CALL) { request ->
        logger.info { "Function call: $request" }
      }

      onRequest(STATUS_UPDATE) { request ->
        logger.info { "Status update: STATUS_UPDATE" }
      }

      onRequest(STATUS_UPDATE) { request ->
        if (request.hasStatusUpdateError()) {
          logger.info { "Status update error: ${request.statusUpdateError}" }
        }
      }

      onAllResponses { requestType, response, elapsedTime ->
        logger.info { "All responses: $response" }
        logObject(response)
        logger.info { response.toJsonString() }
        if (isProduction)
          insertResponse(requestType, response, elapsedTime)
      }

      onResponse(ASSISTANT_REQUEST) { requestType, response, elapsed ->
//      logger.info { "Response: $response" }
      }
    }
  }
}
