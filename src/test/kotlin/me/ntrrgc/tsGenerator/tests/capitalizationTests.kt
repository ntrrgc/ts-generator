/*
 * Copyright 2017 Alicia Boya García
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.ntrrgc.tsGenerator.tests

import me.ntrrgc.tsGenerator.camelCaseToSnakeCase
import me.ntrrgc.tsGenerator.snakeCaseToCamelCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


class CapitalizationTests : Spek({

    describe("camelCaseToSnakeCase()") {
        val tests: List<Pair<String, String>> = listOf(
            "camelCase" to "camel_case",
            "camelCase" to "camel_case",
            "CamelCase" to "camel_case",
            "CamelCamelCase" to "camel_camel_case",
            "Camel2Camel2Case" to "camel2_camel2_case",
            "getHTTPResponseCode" to "get_http_response_code",
            "get2HTTPResponseCode" to "get2_http_response_code",
            "HTTPResponseCode" to "http_response_code",
            "HTTPResponseCodeXYZ" to "http_response_code_xyz"
        )

        tests.forEach { (camel, snake) ->
            it("handles $camel -> $snake") {
                val actual = camelCaseToSnakeCase(snake)
                assertEquals(snake, actual)
            }
        }
    }

    describe("snakeCaseToCamelCase()") {
        listOf(
            "camel_case" to "camelCase",
            "camel_camel_case" to "camelCamelCase",
            "camel2_camel2_case" to "camel2Camel2Case",
            "get_http_response_code" to "getHttpResponseCode",
            "get2_http_response_code" to "get2HttpResponseCode",
            "http_response_code" to "httpResponseCode",
            "http_response_code_xyz" to "httpResponseCodeXyz"
        )
            .forEach { (snake, camel) ->
                it("handles $snake -> $camel") {
                    val actual = snakeCaseToCamelCase(snake)
                    assertEquals(camel, actual)
                }
            }
    }
})
