package com.example.userservice.controller

import com.example.userservice.dto.UserProfileRequest
import com.example.userservice.service.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = ["*"])
class UserController(
    private val userService: UserService
) {

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.username")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<Any> {
        val user = userService.getUserById(id)
        return if (user != null) {
            ResponseEntity.ok(mapOf(
                "userId" to user.userId,
                "username" to user.username,
                "email" to user.email,
                "roles" to user.roles,
                "createdAt" to user.createdAt,
                "updatedAt" to user.updatedAt
            ))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{userId}/profile")
    @PreAuthorize("#userId == principal.userId or hasRole('ADMIN')")
    fun createOrUpdateProfile(
        @PathVariable userId: UUID,
        @Valid @RequestBody profileRequest: UserProfileRequest
    ): ResponseEntity<Any> {
        try {
            val profile = userService.createOrUpdateUserProfile(userId, profileRequest)
            return ResponseEntity.ok(mapOf(
                "message" to "Profile updated successfully",
                "profileId" to profile.profileId,
                "firstName" to profile.firstName,
                "lastName" to profile.lastName,
                "address" to profile.address
            ))
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(mapOf("error" to e.message!!))
        }
    }

    @GetMapping("/{userId}/profile")
    @PreAuthorize("#userId == principal.userId or hasRole('ADMIN')")
    fun getProfile(@PathVariable userId: UUID): ResponseEntity<Any> {
        val profile = userService.getUserProfile(userId)
        return if (profile != null) {
            ResponseEntity.ok(mapOf(
                "profileId" to profile.profileId,
                "userId" to profile.user.userId,
                "firstName" to profile.firstName,
                "lastName" to profile.lastName,
                "address" to profile.address
            ))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}