package com.ralabs.security.app.controller

import com.nhaarman.mockito_kotlin.*
import com.ralabs.security.app.DemoApplication
import com.ralabs.security.app.event.OnRegistrationCompleteEvent
import com.ralabs.security.app.event.RegistrationListener
import com.ralabs.security.app.models.User
import com.ralabs.security.app.request.password.PasswordChangeRequest
import com.ralabs.security.app.request.password.PasswordResetRequest
import com.ralabs.security.app.service.TestService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
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

    @MockBean
    lateinit var eventPublisher: RegistrationListener

    @Test
    @DisplayName("should change password")
    fun changePassword() {
        val passwordRequest = PasswordChangeRequest("admin", "newAdmin1",
                "newAdmin1")
        val body = testService.asJsonString(passwordRequest);
        val accessToken = testService.obtainAccessToken()
        val response = mockMvc
                .perform(MockMvcRequestBuilders.post("/password/change")
                        .header("Authorization", "Bearer $accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn().response.contentAsString
        assertTrue(response.isNotEmpty())
    }

    @Test
    @DisplayName("should return 404 (bad request) providing password with invalid format")
    fun changePasswordBadRequest() {
        val passwordRequest = PasswordChangeRequest("admin", "newAdmin",
                "newAdmin")
        val body = testService.asJsonString(passwordRequest);
        val accessToken = testService.obtainAccessToken()
        mockMvc.perform(MockMvcRequestBuilders.post("/password/change")
                .header("Authorization", "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andReturn().response.contentAsString
    }

    @Test
    @DisplayName("should send generated code to user email")
    fun resetPasswordSentCode() {
        val response = mockMvc.perform(MockMvcRequestBuilders.get("/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", "admin@gmail.com")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn().response.contentAsString

        val apiResponse = testService.getApiResponseFromJsonString(response)
        assertTrue(apiResponse.success)
        assertTrue(apiResponse.message == "Success. Check your email.")
    }

    @Test
    @DisplayName("should check if reset password email was sent")
    fun checkConfirmationEmailSending() {
        val email = "admin@gmail.com"
        val response = mockMvc.perform(MockMvcRequestBuilders.get("/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .param("email", email)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn().response.contentAsString

        val apiResponse = testService.getApiResponseFromJsonString(response)
        val savedUser = testService.userRepository.findByEmail(email) as User

        val registrationCompleteEvent = OnRegistrationCompleteEvent(savedUser, "reset password")

        whenever(eventPublisher.onApplicationEvent(any())).thenAnswer {}

        verify(eventPublisher, times(1))
                .onApplicationEvent(argWhere(testService.appEventArgPredicateFunc(registrationCompleteEvent)))

        assertTrue(response.isNotEmpty())
        assertTrue(apiResponse.success)
        assertTrue(apiResponse.message == "Success. Check your email.")
    }

    @Test
    @DisplayName("should return 404 (bad request) bad token to reset password")
    fun resetPasswordBadToken() {
        val passwordResetRequest = PasswordResetRequest("badToken", "newPassword1")
        val body = testService.asJsonString(passwordResetRequest)
        val response = mockMvc.perform(MockMvcRequestBuilders.get("/password/reset/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andReturn().response.contentAsString

        val apiResponse = testService.getApiResponseFromJsonString(response)
        assertTrue(!apiResponse.success)
        assertTrue(apiResponse.message == "Token is invalid")
    }

    @Test
    @DisplayName("should send email")
    fun resetPassword() {
        val passwordResetRequest = PasswordResetRequest("4824403807", "newPassword1")
        val body = testService.asJsonString(passwordResetRequest)
        val response = mockMvc.perform(MockMvcRequestBuilders.get("/password/reset/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn().response.contentAsString

        val apiResponse = testService.getApiResponseFromJsonString(response)
        assertTrue(apiResponse.success)
        assertTrue(apiResponse.message == "Success. Password was changed.")
    }


    @Test
    @DisplayName("should return 404 (bad request) because of using expired token to reset password")
    fun resetPasswordUsingExpiredToken() {
        val passwordResetRequest = PasswordResetRequest("5456709816", "newPassword1")
        val body = testService.asJsonString(passwordResetRequest)
        val response = mockMvc.perform(MockMvcRequestBuilders.get("/password/reset/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andReturn().response.contentAsString

        val apiResponse = testService.getApiResponseFromJsonString(response)
        assertTrue(!apiResponse.success)
        assertTrue(apiResponse.message == "Token is expired")
    }

    @Test
    @DisplayName("should refresh token")
    fun refreshToken() {
        val accessToken = testService.obtainAccessToken()
        val response = mockMvc.perform(MockMvcRequestBuilders.get("/token/refresh")
                .header("Authorization", "Bearer $accessToken")
                .header("isRefreshToken", true)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn().response.contentAsString

        val apiResponse = testService.getApiResponseFromJsonString(response)
        assertTrue(apiResponse.success)
    }


}