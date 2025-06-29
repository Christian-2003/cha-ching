package de.christian2003.chaching.plugin.infrastructure.backup

import de.christian2003.chaching.plugin.infrastructure.backup.serializer.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


/**
 * Contains metadata for serialized backups.
 */
@Serializable
data class BackupMetadata(

    /**
     * Version of the serialized format.
     */
    @SerialName("version")
    val version: Int = 1,

    /**
     * Time at which the backup was created.
     */
    @SerialName("time")
    @Serializable(with = LocalDateTimeSerializer::class)
    val time: LocalDateTime = LocalDateTime.now()

)
