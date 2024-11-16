package com.dicoding.hanebado.core.utils.datamapper

import com.dicoding.hanebado.core.data.source.remote.response.exercise.ExerciseResponse
import com.dicoding.hanebado.core.domain.exercise.model.Exercise

object ExerciseMapper {
    fun mapResponseToDomain(response: ExerciseResponse): List<Exercise> {
        return response.data?.mapNotNull { dataItem ->
            dataItem?.let {
                Exercise(
                    id = it.id ?: 0,
                    name = it.name.orEmpty(),
                    description = it.description.orEmpty(),
                    difficultyXP = it.difficultyXP ?: 0,
                    createdAt = it.createdAt.orEmpty(),
                    updatedAt = it.updatedAt.orEmpty()
                )
            }
        } ?: emptyList()
    }
}