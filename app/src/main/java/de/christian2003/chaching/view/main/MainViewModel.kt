package de.christian2003.chaching.view.main

import androidx.lifecycle.ViewModel
import de.christian2003.chaching.database.ChaChingRepository
import de.christian2003.chaching.database.entities.Transfer
import de.christian2003.chaching.database.entities.Type
import kotlinx.coroutines.flow.Flow


class MainViewModel: ViewModel() {

	private lateinit var repository: ChaChingRepository

	lateinit var allTypes: Flow<List<Type>>


	fun init(repository: ChaChingRepository) {
		this.repository = repository
		allTypes = repository.allTypes
	}

}
