package com.mobiquityinc.oauth2example.controller

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class UserInfoController {

    @GetMapping("/protected")
    fun greeting(authentication: Authentication, model: ModelMap): String {
        model["name"] = authentication.principal
        return "greeting"
    }

    @GetMapping("/me")
    @ResponseBody
    fun getUserInfo(): Map<String, String> {
        return mapOf()
    }
}