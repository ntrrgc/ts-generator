package me.ntrrgc.tsGenerator.tests

import com.winterbe.expekt.should
import me.ntrrgc.tsGenerator.camelCaseToSnakeCase
import me.ntrrgc.tsGenerator.snakeCaseToCamelCase
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class CapitalizationTests: Spek({
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
                camelCaseToSnakeCase(camel).should.equal(snake)
            }
        }
    }

    describe("snakeCaseToCamelCase()") {
        val tests: List<Pair<String, String>> = listOf(
            "camel_case" to "camelCase",
            "camel_camel_case" to "camelCamelCase",
            "camel2_camel2_case" to "camel2Camel2Case",
            "get_http_response_code" to "getHttpResponseCode",
            "get2_http_response_code" to "get2HttpResponseCode",
            "http_response_code" to "httpResponseCode",
            "http_response_code_xyz" to "httpResponseCodeXyz"
        )

        tests.forEach { (snake, camel) ->
            it("handles $snake -> $camel") {
                snakeCaseToCamelCase(snake).should.equal(camel)
            }
        }
    }
})