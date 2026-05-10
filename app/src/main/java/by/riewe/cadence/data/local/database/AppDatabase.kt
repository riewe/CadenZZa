package by.riewe.cadence.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import by.riewe.cadence.data.local.dao.*
import by.riewe.cadence.data.local.entities.*

@Database(
    entities = [
        CadenceEntity::class,
        ExpenseEntity::class,
        TrailerChangeEntity::class,
        RouteEntity::class,
        RefuelEntity::class,
        TruckSettingsEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cadenceDao(): CadenceDao
    abstract fun trailerChangeDao(): TrailerChangeDao
    abstract fun routeDao(): RouteDao
    abstract fun refuelDao(): RefuelDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun truckSettingsDao(): TruckSettingsDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `truck_settings` (`truckNumber` TEXT NOT NULL, `baseConsumption` REAL NOT NULL, `weightCoefficient` REAL NOT NULL, PRIMARY KEY(`truckNumber`))")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE refuels ADD COLUMN refuelNumber INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
