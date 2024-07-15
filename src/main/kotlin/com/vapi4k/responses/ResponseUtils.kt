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

package com.vapi4k.responses

import com.vapi4k.Vapi4k.logger
import com.vapi4k.common.Constants
import com.vapi4k.utils.JsonUtils.get
import com.vapi4k.utils.JsonUtils.stringValue
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredFunctions

object ResponseUtils {

  fun invokeMethod(
    service: Any,
    methodName: String,
    args: JsonElement,
  ): String {
    logger.info { "Invoking method $methodName with methods: ${service::class.java.declaredMethods.map { it.name }}" }
    val method = service::class.java.declaredMethods.single { it.name == methodName }
    val argNames = args.jsonObject.keys
    val result = method.invoke(service, *argNames.map { args[it].stringValue }.toTypedArray<String>())

    val kFunction = service::class.declaredFunctions.single { it.name == methodName }
    val isVoid = kFunction.returnType.classifier as KClass<*> == Unit::class
    return if (isVoid) "" else result.toString()
  }

  fun deriveNames(funcName: String): Pair<String, String> {
    val classMethod = funcName.split(Constants.NAME_SEPARATOR)
    require(classMethod.size == 2) {
      "Function name must be in the format 'ClassName${Constants.NAME_SEPARATOR}methodName'"
    }
    val className = classMethod[0]
    val methodName = classMethod[1]
    return Pair(className, methodName)
  }
}
