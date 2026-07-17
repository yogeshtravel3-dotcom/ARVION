package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val prompt: String,
    val code: String,
    val framework: String,
    val createdAt: Long = System.currentTimeMillis(),
    val deployedSubdomain: String = "",
    val isTemplate: Boolean = false,
    val category: String = "Custom"
)
