package de.christian2003.chaching.plugin.infrastructure.serializer

import android.net.Uri
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import androidx.core.net.toUri

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Uri::class)
class UriSerializer: KSerializer<Uri> {

    /**
     * Serializes the URI instance to the encoder passed.
     *
     * @param encoder   Encoder into which to encode the serialized URI.
     * @param value     URI to serialize.
     */
    override fun serialize(encoder: Encoder, value: Uri) {
        encoder.encodeString(value.toString())
    }


    /**
     * Deserializes a URI instance from the decoder passed.
     *
     * @param decoder   Decoder to decode a value.
     * @return          Deserialized URI.
     */
    override fun deserialize(decoder: Decoder): Uri {
        return try {
            decoder.decodeString().toUri()
        } catch (_: Exception) {
            Uri.fromParts("about", "blank", null) //about:blank
        }
    }

}
