package com.dicoding.hanebado.core.utils.datamapper

import com.dicoding.hanebado.core.data.source.remote.response.session.ResponseSession
import com.dicoding.hanebado.core.domain.exercise.model.SessionDomain

object SessionMapper {
    fun mapResponseToDomain(response: ResponseSession): SessionDomain {
        return SessionDomain(
            startTime = response.data?.startTime ?: "",
            setNumber = response.data?.setNumber ?: 0
        )
    }
}