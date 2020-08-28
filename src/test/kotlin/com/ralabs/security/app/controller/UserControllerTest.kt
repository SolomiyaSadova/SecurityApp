package com.ralabs.security.app.controller

import com.ralabs.security.app.DemoApplication
import com.ralabs.security.app.request.password.PasswordChangeRequest
import com.ralabs.security.app.request.password.PasswordResetRequest
import com.ralabs.security.app.service.TestService
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(
        classes = [DemoApplication::class],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@TestPropertySource(locations = ["classpath:application.properties"])
@Transactional
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var testService: TestService

    @Test
    @DisplayName("should change password")
    fun changePassword() {
        val passwordRequest = PasswordChangeRequest("admin", "newAdmin1", "newAdmin1")
        val body = testService.asJsonString(passwordRequest);
        val accessToken = testService.obtainAccessToken()
        val response = mockMvc
                .perform(MockMvcRequestBuilders.post("/changePassword")
                        .header("Authorization", "Bearer $accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn().response.contentAsString
        assertTrue(response.isNotEmpty())
    }

    @Test
    @DisplayName("should not change password because of bad password")
    fun changePasswordBadRequest() {
        val passwordRequest = PasswordChangeRequest("admin", "newAdmin", "newAdmin")
        val body = testService.asJsonString(passwordRequest);
        val accessToken = testService.obtainAccessToken()
        mockMvc.perform(MockMvcRequestBuilders.post("/changePassword")
                .header("Authorization", "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andReturn().response.contentAsString
    }

    @Test
    @DisplayName("should not change password because newPasswordConfirm field doesnt matches newPassword field")
    fun changePasswordBadRequestNewPasswordConfirmField() {
        val passwordRequest = PasswordChangeRequest("admin", "newAdmin1", "newAdin1")
        val body = testService.asJsonString(passwordRequest);
        val accessToken = testService.obtainAccessToken()
        val response = mockMvc.perform(MockMvcRequestBuilders.post("/changePassword")
                .header("Authorization", "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andReturn().response.contentAsString
//
//        val apiRequest = testService.getApiResponseFromJsonString(response);
//        assertTrue(!apiRequest.success)
//        assertTrue(apiRequest.message == "Confirm password field doesn't match the password")
    }
}