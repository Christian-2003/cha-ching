package de.christian2003.chaching.plugin.infrastructure.backup

import de.christian2003.chaching.plugin.infrastructure.backup.dto.TransferDto
import de.christian2003.chaching.plugin.infrastructure.backup.dto.TypeDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BackupData(

    @SerialName("metadata")
    val metadata: BackupMetadata,

    @SerialName("types")
    val types: List<TypeDto>,

    @SerialName("transfers")
    val transfers: List<TransferDto>

)
