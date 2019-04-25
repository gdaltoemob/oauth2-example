package com.mobiquityinc.oauth2example.service

import org.apache.http.client.fluent.Form
import org.apache.http.client.fluent.Request
import org.springframework.stereotype.Component
import java.nio.charset.Charset
import java.util.*

@Component
class OAuthService {

    val clientId: String = "5027999a-3cae-49ba-a66e-78fcbd417b46"
    val psd2AppSecret: String = "G1vW3hJ0gR1aU2cH8aP7jS4tE0wT8pI7eP4qT5kH8uX8fK0nD3"

    fun getToken(code: String): String {
        return Request.Post("https://api-sandbox.rabobank.nl/openapi/sandbox/oauth2/token")
                .addHeader("accept", "application/json")
                .addHeader("authorization", "Basic ${authorizationBase64()}")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .bodyForm(Form.form()
                        .add("grant_type", "authorization_code")
                        .add("code", code)
                        .add("redirect_uri", "https://www.google.com").build())
                .execute().returnContent().asString()
    }

    private fun authorizationBase64(): String {
        return Base64.getEncoder().encodeToString("$clientId:$psd2AppSecret".toByteArray(Charset.forName("UTF-8")))
    }
}