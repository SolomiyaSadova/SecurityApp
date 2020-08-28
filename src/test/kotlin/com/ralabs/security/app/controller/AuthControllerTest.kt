package com.ralabs.security.app.controller

import com.ralabs.security.app.DemoApplication
import com.ralabs.security.app.request.LoginRequest
import com.ralabs.security.app.request.SignUpRequest
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

const val URL = "/auth";

@SpringBootTest(
        classes = [DemoApplication::class],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@TestPropertySource(locations = ["classpath:application.properties"])
@Transactional
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var testService: TestService

    @Test
    @DisplayName("should sign up and return new created user")
    fun signUpUser() {
        val signUpRequest = SignUpRequest(email = "user1@gmail.com",
                firstName = "user1", lastName = "user1", password = "newUser1", confirmPassword = "newUser1")

        val body = testService.asJsonString(signUpRequest)
        val response = mockMvc
                .perform(MockMvcRequestBuilders.post("${URL}/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

        val savedUser = testService.getUserResponseFromJsonString(response)
        assertTrue(response.isNotEmpty())
        assertTrue(savedUser.email == signUpRequest.email)
    }

    @Test
    @DisplayName("should save user in database")
    fun saveUser() {
        val signUpRequest = SignUpRequest(email = "user2@gmail.com",
                lastName = "user2", firstName = "user2", password = "newUser2", confirmPassword = "newUser2")

        val body = testService.asJsonString(signUpRequest)
        val response = mockMvc
                .perform(MockMvcRequestBuilders.post("${URL}/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

        val savedUser = testService.userRepository.findByEmail(signUpRequest.email)
        assertTrue(response.isNotEmpty())
        assertTrue(savedUser.email == signUpRequest.email)
    }

    @Test
    @DisplayName("should not save user in database because of wrong email")
    fun trySaveUserWithWrongEmail() {
        val signUpRequest = SignUpRequest(email = "user3",
                lastName = "user3", firstName = "user3", password = "user3", confirmPassword = "user3")

        val body = testService.asJsonString(signUpRequest)
        mockMvc.perform(MockMvcRequestBuilders.post("${URL}/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString

    }

    @Test
    @DisplayName("should not save user in database because of using the same email by another user ")
    fun trySaveUserWithUsedEmail() {
        val signUpRequest = SignUpRequest(email = "admin@gmail.com",
                lastName = "user3", firstName = "user3", password = "user3", confirmPassword = "user3")

        val body = testService.asJsonString(signUpRequest)
        mockMvc.perform(MockMvcRequestBuilders.post("${URL}/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString

    }

    @Test
    @DisplayName("should sign in and return jwt token")
    fun userSignIn() {
        val loginRequest = LoginRequest(email = "admin@gmail.com", password = "admin")
        val body = testService.asJsonString(loginRequest)
        mockMvc.perform(MockMvcRequestBuilders.post("${URL}/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

    }

    @Test
    @DisplayName("should not sign in and return unauthorized")
    fun userSignInWithBadCredentials() {
        val loginRequest = LoginRequest(email = "admin", password = "admin")
        val body = testService.asJsonString(loginRequest)
        mockMvc.perform(MockMvcRequestBuilders.post("${URL}/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized)
                .andReturn().response.contentAsString
    }

    @Test
    @DisplayName("should not allow access to unauthenticated user")
    fun shouldNotAllowAccessToUnauthenticatedUser() {
        mockMvc.perform(MockMvcRequestBuilders.get("/hello")).andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("should not allow access to user with wrong jwt token")
    fun shouldNotAllowAccessToUserWithWrongJwtToken() {
        val accessToken = "wrong token"
        mockMvc.perform(MockMvcRequestBuilders.get("/hello")
                .header("Authorization", "Bearer $accessToken"))
                .andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("should allow access to user")
    fun shouldAllowAccessToUser() {
        val accessToken = testService.obtainAccessToken()
        mockMvc.perform(MockMvcRequestBuilders.get("/hello")
                .header("Authorization", "Bearer $accessToken"))
                .andExpect(status().isOk)
    }
}