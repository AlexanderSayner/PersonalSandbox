package com.example.userservice.controller

import com.example.userservice.config.JwtService
import com.example.userservice.dto.*
import com.example.userservice.service.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["*"])
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val userService: UserService
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: UserRegistrationRequest): ResponseEntity<Map<String, String>> {
        try {
            val user = userService.registerUser(request)
            return ResponseEntity.ok(mapOf("message" to "User registered successfully", "userId" to user.userId.toString()))
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(mapOf("error" to e.message!!))
        }
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<JwtResponse> {
        try {
            val authentication: Authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    request.username,
                    request.password
                )
            )

            SecurityContextHolder.getContext().authentication = authentication

            val user = userService.findByUsername(request.username)
                ?: throw RuntimeException("User not found after authentication")

            val roles = user.roles.toList()

            val jwt = jwtService.generateToken(authentication.principal as org.springframework.security.core.userdetails.User)

            return ResponseEntity.ok(
                JwtResponse(
                    token = jwt,
                    userId = user.userId.toString(),
                    username = user.username,
                    roles = roles
                )
            )
        } catch (e: Exception) {
            return ResponseEntity.badRequest().build()
        }
    }
}