package de.christian2003.chaching.view.main

import androidx.lifecycle.ViewModel
import de.christian2003.chaching.database.ChaChingRepository
import de.christian2003.chaching.database.entities.Type
import kotlinx.coroutines.flow.Flow


/**
 * View model for the MainScreen.
 */
class MainViewModel: ViewModel() {

	/**
	 * Repository from which to source data.
	 */
	private lateinit var repository: ChaChingRepository


	/**
	 * List of all types.
	 */
	lateinit var allTypes: Flow<List<Type>>


	/**
	 * Instantiates the view model.
	 *
	 * @param repository	Repository from which to source data.
	 */
	fun init(repository: ChaChingRepository) {
		this.repository = repository
		allTypes = repository.allTypes
	}

}
