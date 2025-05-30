package de.christian2003.chaching.view.main

import androidx.lifecycle.ViewModel
import de.christian2003.chaching.database.ChaChingRepository
import de.christian2003.chaching.database.entities.Transfer
import kotlinx.coroutines.flow.Flow


class MainViewModel: ViewModel() {

	private lateinit var repository: ChaChingRepository


	fun init(repository: ChaChingRepository) {
		this.repository = repository
	}

}
