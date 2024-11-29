package com.dicoding.hanebado.core.utils.datamapper

import com.dicoding.hanebado.core.data.source.remote.response.dailyplan.AddDailyPlanResponse
import com.dicoding.hanebado.core.data.source.remote.response.dailyplan.GetAllDailyResponse
import com.dicoding.hanebado.core.data.source.remote.response.dailyplan.GetTodayResponse
import com.dicoding.hanebado.core.domain.dailyplan.model.DailyPlanDomain
import com.dicoding.hanebado.core.domain.dailyplan.model.DailyPlanExerciseDomain
import com.dicoding.hanebado.core.domain.dailyplan.model.DailyPlanItemDomain
import com.dicoding.hanebado.core.domain.dailyplan.model.DailyPlanListDomain
import com.dicoding.hanebado.core.domain.dailyplan.model.ExerciseDomain
import com.dicoding.hanebado.core.domain.dailyplan.model.MetaDomain
import com.dicoding.hanebado.core.domain.dailyplan.model.TodayDailyPlanDomain
import com.dicoding.hanebado.core.domain.dailyplan.model.TodayExerciseDetailDomain
import com.dicoding.hanebado.core.domain.dailyplan.model.TodayExerciseDomain

object DailyPlanMapper {
    // Mengubah fungsi agar menerima AddDailyPlanResponse, bukan hanya Data
    fun mapAddDailyPlanResponseToDomain(response: AddDailyPlanResponse): DailyPlanDomain {
        // Mengonversi response.data ke DailyPlanDomain
        return DailyPlanDomain(
            id = response.data?.id ?: 0,
            label = response.data?.label.orEmpty(),
            userId = response.data?.userId.orEmpty(),
            isActive = response.data?.isActive ?: false,
            createdAt = response.data?.createdAt.orEmpty(),
            notificationTime = response.data?.notificationTime.orEmpty(),
            repeatDays = response.data?.repeatDays?.filterNotNull().orEmpty(),
            exercises = response.data?.exercises?.filterNotNull()?.map { exercisesItem ->
                DailyPlanDomain.ExerciseItem(
                    id = exercisesItem.id ?: 0,
                    dailyPlanId = exercisesItem.dailyPlanId ?: 0,
                    reps = exercisesItem.reps ?: 0,
                    sets = exercisesItem.sets ?: 0,
                    order = exercisesItem.order ?: 0,
                    isCompleted = exercisesItem.isCompleted ?: false,
                    exercise = exercisesItem.exercise?.let { exercise ->
                        DailyPlanDomain.Exercise(
                            id = exercise.id ?: 0,
                            name = exercise.name.orEmpty(),
                            description = exercise.description.orEmpty(),
                            createdAt = exercise.createdAt.orEmpty(),
                            updatedAt = exercise.updatedAt.orEmpty(),
                            difficultyXP = exercise.difficultyXP ?: 0
                        )
                    } ?: DailyPlanDomain.Exercise(
                        id = 0,
                        name = "",
                        description = "",
                        createdAt = "",
                        updatedAt = "",
                        difficultyXP = 0
                    )
                )
            } ?: emptyList()
        )
    }

    fun mapResponseToDomain(response: GetAllDailyResponse): DailyPlanListDomain {
        return DailyPlanListDomain(
            data = response.data?.data?.map { dataItem ->
                DailyPlanItemDomain(
                    id = dataItem?.id ?: 0,
                    label = dataItem?.label.orEmpty(),
                    notificationTime = dataItem?.notificationTime.orEmpty(),
                    repeatDays = dataItem?.repeatDays?.filterNotNull() ?: emptyList(),
                    exercises = dataItem?.exercises?.mapNotNull { exercise ->
                        exercise?.let {
                            DailyPlanExerciseDomain(
                                id = it.id ?: 0,
                                dailyPlanId = it.dailyPlanId ?: 0,
                                exerciseId = it.exerciseId ?: 0,
                                sets = it.sets ?: 0,
                                reps = it.reps ?: 0,
                                order = it.order ?: 0,
                                isCompleted = it.isCompleted ?: false,
                                exercise = ExerciseDomain(
                                    id = it.exercise?.id ?: 0,
                                    name = it.exercise?.name.orEmpty(),
                                    description = it.exercise?.description.orEmpty(),
                                    difficultyXP = it.exercise?.difficultyXP ?: 0,
                                    createdAt = it.exercise?.createdAt.orEmpty(),
                                    updatedAt = it.exercise?.updatedAt.orEmpty()
                                )
                            )
                        }
                    } ?: emptyList(),
                    isActive = dataItem?.isActive ?: false,
                    userId = dataItem?.userId.orEmpty(),
                    createdAt = dataItem?.createdAt.orEmpty()
                )
            } ?: emptyList(),
            meta = MetaDomain(
                total = response.data?.meta?.total ?: 0,
                page = response.data?.meta?.page ?: 1,
                limit = response.data?.meta?.limit ?: 10,
                totalPages = response.data?.meta?.totalPages ?: 1,
                hasNextPage = response.data?.meta?.hasNextPage ?: false,
                hasPreviousPage = response.data?.meta?.hasPreviousPage ?: false
            )
        )
    }

    fun mapTodayResponseToDomain(response: GetTodayResponse): List<TodayDailyPlanDomain> {
        return response.data?.mapNotNull { item ->
            item?.let {
                TodayDailyPlanDomain(
                    id = it.id ?: 0,
                    label = it.label.orEmpty(),
                    notificationTime = it.notificationTime.orEmpty(),
                    repeatDays = it.repeatDays?.filterNotNull() ?: emptyList(),
                    exercises = it.exercises?.mapNotNull { exerciseItem ->
                        exerciseItem?.let { exercise ->
                            TodayExerciseDomain(
                                id = exercise.id ?: 0,
                                dailyPlanId = exercise.dailyPlanId ?: 0,
                                exerciseId = exercise.exerciseId ?: 0,
                                sets = exercise.sets ?: 0,
                                reps = exercise.reps ?: 0,
                                order = exercise.order ?: 0,
                                isCompleted = exercise.isCompleted ?: false,
                                exercise = TodayExerciseDetailDomain(
                                    id = exercise.exercise?.id ?: 0,
                                    name = exercise.exercise?.name.orEmpty(),
                                    description = exercise.exercise?.description.orEmpty(),
                                    difficultyXP = exercise.exercise?.difficultyXP ?: 0,
                                    createdAt = exercise.exercise?.createdAt.orEmpty(),
                                    updatedAt = exercise.exercise?.updatedAt.orEmpty()
                                )
                            )
                        }
                    } ?: emptyList(),
                    isActive = it.isActive ?: false,
                    userId = it.userId.orEmpty(),
                    createdAt = it.createdAt.orEmpty()
                )
            }
        } ?: emptyList()
    }

}