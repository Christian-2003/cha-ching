package de.christian2003.chaching.view.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.christian2003.chaching.database.ChaChingRepository

class OnboardingViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var repository: ChaChingRepository

    private var isInitialized: Boolean = false


    fun init(repository: ChaChingRepository) {
        if (!isInitialized) {
            this.repository = repository
            isInitialized = true
        }
    }

}
