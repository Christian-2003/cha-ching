package de.christian2003.chaching.application.backup


/**
 * Backup service allows for serialization and deserialization (+ import) of app data.
 */
interface BackupService {

    /**
     * Serializes the app data into a string. If the data cannot be serialized, null is returned.
     * The serialized data can then be (for example) written into a file.
     *
     * @return  Serialized app data.
     */
    suspend fun serialize(): String?


    /**
     * Deserializes the data passed and stores the data within the app repository according to the
     * import strategy passed as argument.
     *
     * @param serializedData    Data that should be deserialized and stored within the app.
     * @param importStrategy    Indicates what should happen with the data currently stored within
     *                          the app.
     * @return                  Whether deserialization and import are successful.
     */
    suspend fun deserialize(serializedData: String, importStrategy: ImportStrategy): Boolean

}
