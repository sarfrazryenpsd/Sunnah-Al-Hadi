package com.ryen.sunnah_alhadi.domain.useCase

import kotlinx.coroutines.flow.Flow

abstract class UseCase<in P, out R> {
    suspend operator fun invoke(parameters: P): R = execute(parameters)
    protected abstract suspend fun execute(parameters: P): R
}

abstract class NoParamUseCase<out R> {
    suspend operator fun invoke(): R = execute()
    protected abstract suspend fun execute(): R
}

abstract class FlowUseCase<in P, R> {
    operator fun invoke(params: P): Flow<R> = execute(params)
    protected abstract fun execute(params: P): Flow<R>
}

abstract class NoParamFlowUseCase<R> {
    operator fun invoke(): Flow<R> = execute()
    protected abstract fun execute(): Flow<R>
}