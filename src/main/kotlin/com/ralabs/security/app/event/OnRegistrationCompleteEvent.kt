package com.ralabs.security.app.event

import com.ralabs.security.app.models.User
import org.springframework.context.ApplicationEvent
import java.util.*

class OnRegistrationCompleteEvent(
        val user: User,
        val actionName: String
) : ApplicationEvent(user)