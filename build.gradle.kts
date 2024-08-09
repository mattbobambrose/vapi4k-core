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

plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.versions)
}

allprojects {
    extra["versionStr"] = "1.3.1"
    extra["releaseDate"] = "08/08/2024"
    group = "com.github.mattbobambrose.vapi4k"
}
