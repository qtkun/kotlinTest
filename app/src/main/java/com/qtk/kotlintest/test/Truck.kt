package com.qtk.kotlintest.test

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject
import javax.inject.Qualifier

class Truck @Inject constructor(private val driver: Driver) {
    @BindGasEngine
    @Inject
    lateinit var gasEngine: GasEngine

    @BindElectricEngine
    @Inject
    lateinit var electricEngine: ElectricEngine

    fun deliver() {
        gasEngine.start()
        electricEngine.start()
        println("Truck is delivering cargo. Driven by $driver")
        gasEngine.shutdown()
        electricEngine.shutdown()
    }
}

class Driver @Inject constructor() {
}

interface Engine{
    fun start()
    fun shutdown()
}

class GasEngine @Inject constructor() : Engine {
    override fun start() {
        println("Gas engine start.")
    }

    override fun shutdown() {
        println("Gas engine shutdown.")
    }

}

class ElectricEngine @Inject constructor() : Engine {
    override fun start() {
        println("Electric engine start.")
    }

    override fun shutdown() {
        println("Electric engine shutdown.")
    }
}

@Module
@InstallIn(ActivityComponent::class)
abstract class EngineModule {
    @BindGasEngine
    @Binds
    abstract fun bindGasEngine(gasEngine: GasEngine) : GasEngine

    @BindElectricEngine
    @Binds
    abstract fun bindElectricEngine(electricEngine: ElectricEngine) : ElectricEngine
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BindGasEngine

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BindElectricEngine