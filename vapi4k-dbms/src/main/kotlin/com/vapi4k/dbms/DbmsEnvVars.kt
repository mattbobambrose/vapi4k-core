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

import com.vapi4k.envvar.EnvVar
import com.vapi4k.envvar.EnvVar.Companion.getWithDefault
import com.vapi4k.envvar.EnvVar.Companion.obfuscate

object DbmsEnvVars {
  val DBMS_DRIVER_CLASSNAME =
    EnvVar("DBMS_DRIVER_CLASSNAME", getWithDefault("com.impossibl.postgres.jdbc.PGDriver"))
  val DBMS_URL = EnvVar("DBMS_URL", getWithDefault("jdbc:pgsql://localhost:5432/postgres"))
  val DBMS_USERNAME = EnvVar("DBMS_USERNAME", getWithDefault("postgres"))
  val DBMS_PASSWORD = EnvVar(
    name = "DBMS_PASSWORD",
    src = getWithDefault("docker"),
    maskFunc = obfuscate(1),
  )
  val DBMS_MAX_POOL_SIZE = EnvVar("DBMS_MAX_POOL_SIZE", getWithDefault(10))
  val DBMS_MAX_LIFETIME_MINS = EnvVar("DBMS_MAX_LIFETIME_MINS", getWithDefault(30))

  fun loadDbmsEnvVars() = Unit
}
