package de.christian2003.chaching.view.transfer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.christian2003.chaching.database.ChaChingRepository
import java.util.UUID


class TransferViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var repository: ChaChingRepository


    fun init(repository: ChaChingRepository, transferId: UUID?) {
        this.repository = repository
    }

}
