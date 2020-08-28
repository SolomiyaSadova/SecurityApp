package com.ralabs.security.app.service

import com.ralabs.security.app.controller.URL
import com.ralabs.security.app.repository.UserRepository
import com.ralabs.security.app.request.ApiResponse
import com.ralabs.security.app.request.LoginRequest
import com.ralabs.security.app.request.UserResponse
import org.apache.tomcat.util.json.JSONParser
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.JavaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.json.JacksonJsonParser
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import javax.tools.JavaFileObject

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
            objectMapper.readValue(jsonString, UserResponse::class.java)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun getApiResponseFromJsonString(jsonString: String): ApiResponse {
        return try {
            objectMapper.readValue(jsonString, ApiResponse::class.java)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}