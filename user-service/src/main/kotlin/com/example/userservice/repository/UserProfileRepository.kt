package com.example.userservice.repository

import com.example.userservice.model.User
import com.example.userservice.model.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface UserProfileRepository : JpaRepository<UserProfile, UUID> {
    fun findByUser(user: User): UserProfile?
    fun findByUser_UserId(userId: UUID): UserProfile?
}
