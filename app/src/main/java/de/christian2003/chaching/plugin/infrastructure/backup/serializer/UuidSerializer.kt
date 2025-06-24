package de.christian2003.chaching.plugin.infrastructure.backup.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID


/**
 * Serializer to serialize a UUID instance with the Kotlin serialization API.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = UUID::class)
class UuidSerializer: KSerializer<UUID> {

    /**
     * Serializes the UUID instance to the encoder passed.
     *
     * @param encoder   Encoder into which to encode the serialized UUID.
     * @param uuid      UUID to serialize.
     */
    override fun serialize(encoder: Encoder, uuid: UUID) {
        encoder.encodeString(uuid.toString())
    }


    /**
     * Deserializes a UUID instance from the decoder passed.
     *
     * @param decoder   Decoder to decode a value.
     * @return          Deserialized UUID.
     */
    override fun deserialize(decoder: Decoder): UUID {
        return try {
            UUID.fromString(decoder.decodeString())
        } catch (_: Exception) {
            UUID(0, 0)
        }
    }

}
