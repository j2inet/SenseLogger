package net.j2i.senselogger

import android.hardware.Sensor
import android.hardware.SensorManager
import android.content.Context

class LoggingManager {
    val sensorManager: SensorManager
    val context:Context
    val sensorListener:SensorLoggingReceiver
    val loggingReceiver:ISensorLoggingReceiverListener
    var sensorTypeList:List<Int> = listOf()

    constructor(context:Context) {
        this.sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        this.context = context
        this.loggingReceiver = DefaultSensorLoggingReceiverListener(context)
        this.sensorListener = SensorLoggingReceiver(SessionID,loggingReceiver)
    }

    var  SessionID:Long = System.currentTimeMillis();

    fun newSession() {
        SessionID = System.currentTimeMillis();
    }

    fun startLogging(sensorTypes:List<Int> ) {
        sensorTypeList = sensorTypes
        resumeLogging()
    }

    fun stopLogging() {
        pauseLogging()
        sensorTypeList = listOf()
    }

    fun pauseLogging() {
        this.sensorListener.Flush()
        sensorManager.unregisterListener(sensorListener)
    }

    fun resumeLogging() {
        for(sensorType in sensorTypeList) {
            val sensor = sensorManager.getDefaultSensor(sensorType)
            sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun exportData(): List<SensorReading> {
        return (loggingReceiver as DefaultSensorLoggingReceiverListener).exportData()
    }

    fun clearData() {
        (loggingReceiver as DefaultSensorLoggingReceiverListener).clearData()
    }

    fun dump() {
        sensorListener.Flush()
    }
}