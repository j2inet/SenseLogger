package net.j2i.senselogger
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
//import kotlinx.serialization.json.JsonArray

@Serializable
data class SensorReading(val source:String="",val sessionID:Long = 0, val timestamp:Long=0L, var values:ArrayList<Float> = ArrayList<Float> ())  {

    fun getJobject():JsonObject {
        var jList: List<JsonElement> = listOf()
        for (i in 0..values.size - 1) {
            jList += JsonPrimitive(values[i])
        }

        val obj = JsonObject(
            mapOf(
                "source" to JsonPrimitive(source),
                "sessionID" to JsonPrimitive(sessionID),
                "timestamp" to JsonPrimitive(timestamp),
                "values" to JsonArray(jList)
            )
        )
        return obj;
    }
}