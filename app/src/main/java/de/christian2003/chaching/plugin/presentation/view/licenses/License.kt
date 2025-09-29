package de.christian2003.chaching.plugin.presentation.view.licenses


/**
 * Class models license for a software used by the app.
 */
data class License (

    /**
     * Attribute stores the name of the software.
     */
    val softwareName: String,

    /**
     * Attribute stores the resource file of the license.
     */
    val licenseFile: String

)
