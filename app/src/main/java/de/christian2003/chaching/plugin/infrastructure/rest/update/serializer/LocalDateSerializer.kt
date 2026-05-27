package de.christian2003.chaching.plugin.infrastructure.rest.update.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * Serializer for LocalDate instances.
 */
class LocalDateSerializer : KSerializer<LocalDate> {

    /**
     * Formatter.
     */
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")


    /**
     * Serial descriptor.
     */
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "LocalDate",
        kind = PrimitiveKind.STRING
    )


    /**
     * Serializes the specified value into the provided encoder.
     *
     * @param encoder   Encoder to which to serialize the specified value.
     * @param value     Value to serialize to the provided encoder.
     */
    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }


    /**
     * Deserializes a value from the provided decoder.
     *
     * @param decoder   Decoder from which to deserialize a value.
     * @return          Deserialized value.
     */
    override fun deserialize(decoder: Decoder): LocalDate {
        return try {
            LocalDate.parse(decoder.decodeString(), formatter)
        } catch (_: Exception) {
            return LocalDate.MIN
        }
    }

}