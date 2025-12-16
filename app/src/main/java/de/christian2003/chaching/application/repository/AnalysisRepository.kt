package de.christian2003.chaching.application.repository

import de.christian2003.chaching.domain.transfer.Transfer
import de.christian2003.chaching.domain.type.Type
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate


interface AnalysisRepository {

    fun getAllTransfersInDateRange(start: LocalDate, end: LocalDate): Flow<List<Transfer>>


    fun getAllTypes(): Flow<List<Type>>

}
