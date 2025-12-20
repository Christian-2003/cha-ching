package de.christian2003.chaching.plugin.infrastructure.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import de.christian2003.chaching.plugin.infrastructure.db.converter.LocalDateConverter
import de.christian2003.chaching.plugin.infrastructure.db.converter.LocalDateTimeConverter
import de.christian2003.chaching.plugin.infrastructure.db.converter.TypeIconConverter
import de.christian2003.chaching.plugin.infrastructure.db.entities.DeletedTypeEntity
import de.christian2003.chaching.plugin.infrastructure.db.entities.TransferEntity
import de.christian2003.chaching.plugin.infrastructure.db.entities.TypeEntity


/**
 * Database for the Cha-Ching app.
 */
@Database(
	entities = [
		TransferEntity::class,
		TypeEntity::class,
		DeletedTypeEntity::class
	],
	version = 3,
	exportSchema = true
)
@TypeConverters(
	LocalDateTimeConverter::class,
	LocalDateConverter::class,
	TypeIconConverter::class
)
abstract class ChaChingDatabase: RoomDatabase() {

	/**
	 * DAO through which to access the transfers.
	 */
	abstract val transferDao: TransferDao

	/**
	 * DAO through which to access the transfer types.
	 */
	abstract val typeDao: TypeDao

	/**
	 * DAO through which to access the deleted types.
	 */
	abstract val deletedTypeDao: DeletedTypeDao


	companion object {

		/**
		 * Singleton instance of the database.
		 */
		@Volatile
		private var INSTANCE: ChaChingDatabase? = null


		private val MIGRATION_1_2 = object: Migration(1, 2) {
			override fun migrate(db: SupportSQLiteDatabase) {
				db.execSQL("ALTER TABLE types ADD COLUMN isEnabledInQuickAccess INTEGER NOT NULL DEFAULT 1")
			}
		}

		private val MIGRATION_2_3 = object: Migration(2, 3) {
			override fun migrate(db: SupportSQLiteDatabase) {
				//Create table for deleted types
				db.execSQL("CREATE TABLE deletedTypes" +
						"(typeId BLOB NOT NULL, deletedAt BLOB NOT NULL)," +
						"FOREIGN KEY(typeId) REFERENCES types(typeId) ON UPDATE NO ACTION ON DELETE CASCADE")

				//Update existing tables:
				db.execSQL("ALTER TABLE types ADD COLUMN isSalaryByDefault INTEGER NOT NULL DEFAULT 1")
			}
		}


		/**
		 * Returns the singleton instance of the database.
		 *
		 * @param context	Context from which to create the database if none exists.
		 * @return			Database singleton instance.
		 */
		fun getInstance(context: Context): ChaChingDatabase {
			synchronized(this) {
				var instance: ChaChingDatabase? = INSTANCE
				if (instance == null) {
					instance = Room.databaseBuilder(
						context = context.applicationContext,
						klass = ChaChingDatabase::class.java,
						name = "cha_ching_database"
					)
						.addMigrations(MIGRATION_1_2)
						.addMigrations(MIGRATION_2_3)
						.build()
				}
				return instance
			}
		}

	}

}
