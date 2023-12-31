package net.j2i.senselogger
import android.content.Context

class DefaultSensorLoggingReceiverListener:ISensorLoggingReceiverListener {
    val dataHelper:SensorReadingDataHelper

    constructor(uiContext:android.content.Context) {
        this.dataHelper = SensorReadingDataHelper(uiContext)
    }

    override fun onBufferReady(buffer: List<SensorReading>) {
        dataHelper.insertReadingList(buffer)
    }

    override fun onReadingReceived(reading:SensorReading) {
    }

    fun clearData() {
        dataHelper.clearDatabase()
    }
    fun exportData():List<SensorReading> {
        return dataHelper.getAllReadings()
    }

}