package com.qtk.kotlintest.domain.command

import com.qtk.kotlintest.domain.model.ForecastList
import kotlinx.coroutines.flow.Flow

/**
 * Created by qtkun
on 2020-06-15.
 */
interface Command<T> {
    suspend fun execute() : T

    fun execute2() : Flow<T>
}