package de.christian2003.chaching.view.settings

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel


class SettingsViewModel(application: Application): AndroidViewModel(application) {

    fun init() {

    }


    private fun writeToFile(uri: Uri, content: String): Boolean {
        val context: Context = getApplication<Application>().baseContext
        try {
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(content.toByteArray())
                stream.flush()
            }
        }
        catch (e: Exception) {
            Log.e("WriteToFile", e.stackTraceToString())
            return false
        }
        return true
    }


    private fun readFromFile(uri: Uri): String? {
        val context: Context = getApplication<Application>().baseContext
        var content: String? = null
        try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val buffer = ByteArray(1024)
                val stringBuilder = StringBuilder()
                var bytesRead: Int
                while (stream.read(buffer).also { bytesRead = it } != -1) {
                    stringBuilder.append(String(buffer, 0, bytesRead))
                }
                content = stringBuilder.toString()
            }
        }
        catch (e: Exception) {
            Log.e("WriteToFile", e.stackTraceToString())
            content = null
        }
        return content
    }

}
