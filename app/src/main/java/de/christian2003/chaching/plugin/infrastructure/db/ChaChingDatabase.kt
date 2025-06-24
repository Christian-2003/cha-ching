package de.christian2003.chaching.plugin.infrastructure.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.christian2003.chaching.plugin.infrastructure.db.converter.LocalDateConverter
import de.christian2003.chaching.plugin.infrastructure.db.converter.LocalDateTimeConverter
import de.christian2003.chaching.plugin.infrastructure.db.converter.TypeIconConverter
import de.christian2003.chaching.plugin.infrastructure.db.entities.TransferEntity
import de.christian2003.chaching.plugin.infrastructure.db.entities.TypeEntity


/**
 * Database for the Cha-Ching app.
 */
@Database(
	entities = [
		TransferEntity::class,
		TypeEntity::class
	],
	version = 1,
	exportSchema = false
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
						.fallbackToDestructiveMigration(false)
						.build()
				}
				return instance
			}
		}

	}

}
