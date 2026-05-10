package by.riewe.cadence.di

import by.riewe.cadence.data.repository.*
import by.riewe.cadence.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCadenceRepository(
        cadenceRepositoryImpl: CadenceRepositoryImpl
    ): CadenceRepository

    @Binds
    @Singleton
    abstract fun bindRefuelRepository(
        refuelRepositoryImpl: RefuelRepositoryImpl
    ): RefuelRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(impl: ExpenseRepositoryImpl): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindRouteRepository(
        routeRepositoryImpl: RouteRepositoryImpl
    ): RouteRepository

    @Binds
    @Singleton
    abstract fun bindTrailerChangeRepository(
        impl: TrailerChangeRepositoryImpl
    ): TrailerChangeRepository

    @Binds
    @Singleton
    abstract fun bindTruckSettingsRepository(
        impl: TruckSettingsRepositoryImpl
    ): TruckSettingsRepository

}