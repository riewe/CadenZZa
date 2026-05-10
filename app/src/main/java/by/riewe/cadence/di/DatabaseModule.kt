package by.riewe.cadence.di

import android.content.Context
import androidx.room.Room
import by.riewe.cadence.data.local.database.AppDatabase
import by.riewe.cadence.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "cadence_database"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
            .build()
    }

    @Provides
    fun provideCadenceDao(database: AppDatabase): CadenceDao = database.cadenceDao()

    @Provides
    fun provideRefuelDao(database: AppDatabase): RefuelDao = database.refuelDao()

    @Provides
    fun provideTrailerChangeDao(database: AppDatabase): TrailerChangeDao = database.trailerChangeDao()

    @Provides
    fun provideRouteDao(database: AppDatabase): RouteDao = database.routeDao()

    @Provides
    fun provideExpenseDao(database: AppDatabase): ExpenseDao = database.expenseDao()

    @Provides
    fun provideTruckSettingsDao(database: AppDatabase): TruckSettingsDao = database.truckSettingsDao()
}