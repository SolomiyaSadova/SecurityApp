package com.ralabs.security.app.controller

import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


@RestController
class AppController() {
//    @Value("\${security.oauth2.resource.id}")
//    private val resourceId: String? = null
//
//    @Value("\${auth0.domain}")
//    private val domain: String? = null
//
//    @Value("\${auth0.clientId}")
//    private val clientId: String? = null

    @RequestMapping(value = ["/api/public"], method = [RequestMethod.GET], produces = ["application/json"])
    @ResponseBody
    fun publicEndpoint(): String {
        return JSONObject()
                .put("message", "Hello from a public endpoint! You don\'t need to be authenticated to see this.")
                .toString()
    }

    @RequestMapping(value = ["/api/private"], method = [RequestMethod.GET], produces = ["application/json"])
    @ResponseBody
    fun privateEndpoint(): String {
        return JSONObject()
                .put("message", "Hello from a private endpoint! You need to be authenticated to see this.")
                .toString()
    }

    @RequestMapping(value = ["/api/private-scoped"], method = [RequestMethod.GET], produces = ["application/json"])
    @ResponseBody
    fun privateScopedEndpoint(): String {
        return JSONObject()
                .put("message", "Hello from a private endpoint! You need to be authenticated and have a scope of read:messages to see this.")
                .toString()
    }

//    @get:ResponseBody
//    @get:RequestMapping(value = ["/config"], method = [RequestMethod.GET], produces = ["application/json"])
//    val appConfigs: String
//        get() = JSONObject()
//                .put("domain", domain)
//                .put("clientID", clientId)
//                .put("audience", resourceId)
//                .toString()
}