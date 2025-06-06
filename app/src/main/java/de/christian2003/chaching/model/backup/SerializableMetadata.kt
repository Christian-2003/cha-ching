package de.christian2003.chaching.model.backup

import de.christian2003.chaching.model.backup.serializer.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


/**
 * Contains metadata for serialized backups.
 */
@Serializable
data class SerializableMetadata(

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
