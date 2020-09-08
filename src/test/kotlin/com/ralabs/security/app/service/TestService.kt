package com.ralabs.security.app.service

import com.google.gson.Gson
import com.ralabs.security.app.controller.URL
import com.ralabs.security.app.event.OnRegistrationCompleteEvent
import com.ralabs.security.app.repository.UserRepository
import com.ralabs.security.app.request.ApiResponse
import com.ralabs.security.app.request.LoginRequest
import com.ralabs.security.app.request.UserResponse
import org.codehaus.jackson.map.ObjectMapper
import org.h2.value.Value.JSON
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.json.JacksonJsonParser
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@Service
class TestService {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    val objectMapper: ObjectMapper = ObjectMapper();
    fun obtainAccessToken(): String {
        val loginRequest = LoginRequest(email = "admin@gmail.com", password = "admin")
        val body = asJsonString(loginRequest)
        val result = mockMvc.perform(MockMvcRequestBuilders.post("$URL/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn().response.contentAsString

        val jsonParser = JacksonJsonParser()
        return jsonParser.parseMap(result)["accessToken"].toString()
    }

    fun asJsonString(obj: Any?): String {
        return try {
            objectMapper.writeValueAsString(obj)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun getUserResponseFromJsonString(jsonString: String): UserResponse {
        return try {
            val g = Gson()
            g.fromJson(jsonString, UserResponse::class.java)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun getApiResponseFromJsonString(jsonString: String): ApiResponse {
        return try {
            val g = Gson()
            g.fromJson(jsonString, ApiResponse::class.java)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun appEventArgPredicateFunc(expected: OnRegistrationCompleteEvent)
            : (arg: OnRegistrationCompleteEvent) -> Boolean = { arg ->
        arg.actionName == expected.actionName &&
                arg.user.id == expected.user.id &&
                arg.user.firstName == expected.user.firstName &&
                arg.user.lastName == expected.user.lastName &&
                arg.user.email == expected.user.email &&
                arg.user.verified == expected.user.verified &&
                arg.user.password == expected.user.password &&
                arg.user.roles.containsAll(expected.user.roles)
    }
}