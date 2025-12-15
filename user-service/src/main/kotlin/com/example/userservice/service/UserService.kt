package com.example.userservice.service

import com.example.userservice.dto.UserProfileRequest
import com.example.userservice.dto.UserRegistrationRequest
import com.example.userservice.model.User
import com.example.userservice.model.UserProfile
import com.example.userservice.repository.UserProfileRepository
import com.example.userservice.repository.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    @Transactional
    fun registerUser(request: UserRegistrationRequest): User {
        if (userRepository.existsByUsername(request.username)) {
            throw RuntimeException("Username ${request.username} already exists")
        }
        
        if (userRepository.existsByEmail(request.email)) {
            throw RuntimeException("Email ${request.email} already exists")
        }

        val user = User(
            username = request.username,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            roles = request.roles
        )

        return userRepository.save(user)
    }

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")

        return org.springframework.security.core.userdetails.User
            .withUsername(user.username)
            .password(user.passwordHash)
            .authorities(mapRolesToAuthorities(user.roles))
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build()
    }

    private fun mapRolesToAuthorities(roles: Set<String>): Collection<GrantedAuthority> {
        return roles.map { SimpleGrantedAuthority("ROLE_$it") }
    }

    @Transactional(readOnly = true)
    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    @Transactional(readOnly = true)
    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    @Transactional
    fun createOrUpdateUserProfile(userId: UUID, profileRequest: UserProfileRequest): UserProfile {
        val user = userRepository.findById(userId)
            .orElseThrow { throw RuntimeException("User not found with id: $userId") }

        val existingProfile = userProfileRepository.findByUserId(userId)
        
        val profile = if (existingProfile != null) {
            existingProfile.apply {
                firstName = profileRequest.firstName
                lastName = profileRequest.lastName
                address = profileRequest.address
            }
        } else {
            UserProfile(
                user = user,
                firstName = profileRequest.firstName,
                lastName = profileRequest.lastName,
                address = profileRequest.address
            )
        }

        return userProfileRepository.save(profile)
    }

    @Transactional(readOnly = true)
    fun getUserProfile(userId: UUID): UserProfile? {
        return userProfileRepository.findByUserId(userId)
    }

    @Transactional(readOnly = true)
    fun getUserById(userId: UUID): User? {
        return userRepository.findById(userId).orElse(null)
    }
}