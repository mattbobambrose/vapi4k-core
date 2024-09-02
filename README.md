# Vapi4k: A Kotlin DSL for creating [vapi.ai](https://vapi.ai) applications

## RequestContexts

RequestContexts are created:

* All the entry points in Vapi4kServer -- populated by query params
* validateToolInvokeRequest -- populated by queryParams

## SessionIds and AssistantIds

* Every assistant (AssistantOverrides and AssistantOverrides) is assigned a unique assistantId via
  `assistantIdSource.nextAssistantId()`

## serverUrl assignments

ServerUrls are assigned in:

* AbstractAssistantResponseImpl.assistant{} -- values assigned from requestContext
* MemberImpl.assistant{} -- values assigned from requestContext


* Phone.outboundCall{} -- sessionId assigned a random value
* VapiHtmlImpl.talkButton -- sessionId assigned a random value
* validateToolInvokeRequest
