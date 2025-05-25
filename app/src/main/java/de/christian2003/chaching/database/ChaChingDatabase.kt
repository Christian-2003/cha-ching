package de.christian2003.chaching.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.christian2003.chaching.database.converter.LocalDateTimeConverter
import de.christian2003.chaching.database.entities.Transfer


/**
 * Database for the Cha-Ching app.
 */
@Database(entities = [Transfer::class], version = 1, exportSchema = false)
@TypeConverters(LocalDateTimeConverter::class)
abstract class ChaChingDatabase: RoomDatabase() {

	/**
	 * DAO through which to access the transfers.
	 */
	abstract val transferDao: TransferDao


	companion object {

		/**
		 * Singleton instance of the database.
		 */
		@Volatile
		private var INSTANCE: ChaChingDatabase? = null


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
						.fallbackToDestructiveMigration()
						.build()
				}
				return instance
			}
		}

	}

}
