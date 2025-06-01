package de.christian2003.chaching.model.backup.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate


/**
 * Serializer to serialize a LocalDate instance with the Kotlin serialization API.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDate::class)
class LocalDateSerializer: KSerializer<LocalDate> {

    /**
     * Serializes the local date instance to the encoder passed.
     *
     * @param encoder   Encoder into which to encode the serialized local date.
     * @param localDate Local date to serialize.
     */
    override fun serialize(encoder: Encoder, localDate: LocalDate) {
        encoder.encodeLong(localDate.toEpochDay())
    }


    /**
     * Deserializes a local date instance from the decoder passed.
     *
     * @param decoder   Decoder to decode a value.
     * @return          Deserialized local date.
     */
    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.ofEpochDay(decoder.decodeLong())
    }

}
