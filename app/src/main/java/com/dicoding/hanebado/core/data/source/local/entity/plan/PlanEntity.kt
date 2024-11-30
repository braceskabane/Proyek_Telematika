package com.dicoding.hanebado.core.data.source.local.entity.plan

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dicoding.hanebado.core.data.source.local.entity.results.DateConverters

/**
 * Entity class representing a plan item stored in the database
 */
@Entity(tableName = "plan_items")
data class Plan(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val exercise: String,
    val repeatCount: Int,
    var completed: Boolean = false,
    @TypeConverters(DateConverters::class)
    val timeCompleted: Long? = null
)




