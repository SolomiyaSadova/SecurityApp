package com.ralabs.security.app.controller

import com.nhaarman.mockito_kotlin.*
import com.ralabs.security.app.DemoApplication
import com.ralabs.security.app.event.OnRegistrationCompleteEvent
import com.ralabs.security.app.event.RegistrationListener
import com.ralabs.security.app.models.User
import com.ralabs.security.app.request.LoginRequest
import com.ralabs.security.app.request.SignUpRequest
import com.ralabs.security.app.service.TestService
import com.ralabs.security.app.service.UserTestService
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional


const val URL = "/auth";

@SpringBootTest(
        classes = [DemoApplication::class],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
//@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
@TestPropertySource(locations = ["classpath:application.properties"])
@Transactional
class AuthControllerTest {


    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var testService: TestService

    @Autowired
    lateinit var userTestService: UserTestService

    @MockBean
    lateinit var eventPublisher: RegistrationListener

    @Test
    @DisplayName("should sign up and return new created user")
    fun signUpUser() {
        val signUpRequest = SignUpRequest(email = "email@gmail.com",
                firstName = "first_name", lastName = "last_name", password = "Password1", confirmPassword = "Password1")

        val body = testService.asJsonString(signUpRequest)
        val response = mockMvc
                .perform(MockMvcRequestBuilders.post("${URL}/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

        val userResponse = testService.getUserResponseFromJsonString(response)
        val savedUser = testService.userRepository.findByEmail(userResponse.email)

        assertTrue(response.isNotEmpty())
        assertTrue(savedUser!!.email == signUpRequest.email)
        assertTrue(!savedUser.verified)
    }

    @Test
    @DisplayName("should check if confirm email was sent")
    fun checkConfirmationEmailSending() {
        val signUpRequest = SignUpRequest(email = "email@gmail.com",
                firstName = "first_name", lastName = "last_name", password = "Password1", confirmPassword = "Password1")

        val body = testService.asJsonString(signUpRequest)
        val response = mockMvc
                .perform(MockMvcRequestBuilders.post("${URL}/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

        val userResponse = testService.getUserResponseFromJsonString(response)
        val savedUser = testService.userRepository.findByEmail(userResponse.email) as User

        val registrationCompleteEvent = OnRegistrationCompleteEvent(savedUser, "confirm registration")

        println("Registration Complite Event - $registrationCompleteEvent")
        whenever(eventPublisher.onApplicationEvent(any())).thenAnswer {}


        //  Mockito.doThrow(Exception()).`when`(emailService).sendConfirmationEmail(mail)
        verify(eventPublisher, times(1))
                .onApplicationEvent(argWhere(testService.appEventArgPredicateFunc(registrationCompleteEvent)))

        assertTrue(response.isNotEmpty())
        assertTrue(savedUser.email == signUpRequest.email)
        assertTrue(!savedUser.verified)
    }


    @Test
    @DisplayName("should save user in database")
    fun saveUser() {
        val signUpRequest = SignUpRequest(email = "email@gmail.com",
                firstName = "first_name", lastName = "last_name", password = "Password1", confirmPassword = "Password1")

        val body = testService.asJsonString(signUpRequest)
        mockMvc.perform(MockMvcRequestBuilders.post("${URL}/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

        val savedUser = testService.userRepository.findByEmail(signUpRequest.email)
        assertTrue(savedUser?.email == signUpRequest.email)
        assertTrue(savedUser?.verified == false)
    }

    @Test
    @DisplayName("should return 404 (bad request) because of invalid format of email")
    fun trySaveUserWithInvalidEmail() {
        val signUpRequest = SignUpRequest(email = "invalid_email",
                firstName = "first_name", lastName = "last_name", password = "Password1", confirmPassword = "Password1")

        val body = testService.asJsonString(signUpRequest)
        mockMvc.perform(MockMvcRequestBuilders.post("${URL}/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
    }


    @Test
    @DisplayName("should return 404 (bad request) if user with email already exists")
    fun trySignUpUserWithExistingEmail() {
        val signUpRequest = SignUpRequest(email = "admin@gmail.com",
                firstName = "first_name", lastName = "last_name", password = "Password1", confirmPassword = "Password1")

        val body = testService.asJsonString(signUpRequest)
        mockMvc.perform(MockMvcRequestBuilders.post("${URL}/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
    }

    @Test
    @DisplayName("should return 404 (bad request) when confirmation fail because of bad token")
    fun confirmRegistrationUsingBadToken() {
        val badToken = "bad_token"
        val response = mockMvc.perform(MockMvcRequestBuilders.get("${URL}/signup/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", badToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
        val apiResponse = testService.getApiResponseFromJsonString(response)

        val user = userTestService.getUserByVerificationToken(badToken)

        assertTrue(!apiResponse.success)
        assertTrue(apiResponse.message == "Token is bad")
        assertTrue(user == null)
    }

    @Test
    @DisplayName("should return 404 (bad request) when confirmation fail because of expired token")
    fun confirmRegistrationUsingExpiredToken() {
        val badToken = "8976452343"
        val response = mockMvc.perform(MockMvcRequestBuilders.get("${URL}/signup/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", badToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString

        val apiResponse = testService.getApiResponseFromJsonString(response)

        val user = userTestService.getUserByVerificationToken(badToken)

        assertTrue(!apiResponse.success)
        assertTrue(apiResponse.message == "Token is expired")
        assertTrue(!user!!.verified)
    }

    @Test
    @DisplayName("should confirm user registration")
    fun confirmRegistration() {
        val token = "5456709816"
        val response = mockMvc.perform(MockMvcRequestBuilders.get("${URL}/signup/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

        val apiResponse = testService.getApiResponseFromJsonString(response)

        val user = userTestService.getUserByVerificationToken(token)

        assertTrue(apiResponse.success)
        assertTrue(apiResponse.message == "You was successfully registered")
        assertTrue(user?.verified!!)
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
        val loginRequest = LoginRequest(email = "bad_email", password = "bad_password")
        val body = testService.asJsonString(loginRequest)
        mockMvc.perform(MockMvcRequestBuilders.post("${URL}/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized)
                .andReturn().response.contentAsString
    }

}