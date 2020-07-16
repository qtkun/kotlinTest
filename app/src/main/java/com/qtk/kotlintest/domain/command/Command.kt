package com.qtk.kotlintest.domain.command

/**
 * Created by qtkun
on 2020-06-15.
 */
interface Command<T> {
    suspend fun execute() : T
}