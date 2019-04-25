package com.mobiquityinc.oauth2example.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.nio.charset.Charset
import java.security.MessageDigest
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Controller
class AccountInfo {

    val getAccountsUrl = "https://api-sandbox.rabobank.nl/openapi/sandbox/payments/account-information/ais/v3/accounts"

    @Autowired
    lateinit var restTemplate: RestTemplate

    @GetMapping("/accounts")
    fun getUserAccounts(authentication: Authentication) {
        val auth = authentication as OAuth2Authentication
        val headers = getHeaders(auth)

        val request = RequestEntity<Unit>(headers, HttpMethod.GET, URI(getAccountsUrl))
        System.out.println(request)
        restTemplate.exchange(request, String::class.java)
    }

    private fun getHeaders(auth: OAuth2Authentication): HttpHeaders {
        val headers = HttpHeaders()
        headers.set("x-ibm-client-id", auth.oAuth2Request.clientId)
        headers.set("authorization", "Bearer " + (auth.details as OAuth2AuthenticationDetails).tokenValue)
        headers.set("accept", "application/json")
        headers.set("date", DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC)))
        headers.set("digest", createBase64Digest(""))
        //headers.set("psu-ip-address","")
        headers.set("x-request-id", UUID.randomUUID().toString())
        headers.set("signature", createSignatureHeader(headers))
        headers.set("tpp-signature-certificate", getCertificate())

        return headers
    }

    private fun getCertificate(): String {
        return RSA.getCertificate("src/main/resources/certs/cert.pem")
    }

    private fun createSignatureHeader(headers: HttpHeaders): String? {
        val certSerialNumber = "1523433508"

        val signingString = "date: ${headers["date"]?.first()}\ndigest: ${headers["digest"]?.first()}\nx-request-id: ${headers["x-request-id"]?.first()}"
        val signature = RSA.sign(RSA.getPrivateKey("src/main/resources/certs/key.pem"), signingString.toByteArray(Charset.forName("UTF-8")))

        return "keyId=\"$certSerialNumber\",algorithm=\"rsa-sha512\",headers=\"date digest x-request-id\",signature=\"$signature\""
    }

    private fun createBase64Digest(body: String): String {
        val digest = MessageDigest.getInstance("SHA-512")
        val bytes = digest.digest(body.toByteArray(Charsets.UTF_8))
        return "sha-512=" + Base64.getEncoder().encodeToString(bytes)
    }
}