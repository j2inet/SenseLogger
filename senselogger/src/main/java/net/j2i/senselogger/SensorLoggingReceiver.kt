package net.j2i.senselogger

import android.hardware.SensorEvent
import android.hardware.SensorEventListener

class SensorLoggingReceiver: SensorEventListener {
    val sessionID:Int
    var buffer = ArrayList<SensorReading>()
    var notifyAtSizeCount= 0
    var hasRaisedNotification = false
    var listener:ISensorLoggingReceiverListener

    constructor(sessionID:Int, listener:ISensorLoggingReceiverListener, notifyAtSizeCount:Int = 128) {
        this.sessionID = sessionID
        this.notifyAtSizeCount = notifyAtSizeCount
        this.listener = listener
    }

    override fun onAccuracyChanged(sensor: android.hardware.Sensor?, accuracy: Int) {
        TODO()
    }

    fun getSourceName(sensorEvent: SensorEvent):String? {
        var sensorName: String? = null
        val sensorType = sensorEvent?.sensor?.type
        when (sensorType) {
            android.hardware.Sensor.TYPE_ACCELEROMETER -> {
                sensorName = "accelerometer"
            }

            android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                sensorName = "ambient_temperature"
            }

            android.hardware.Sensor.TYPE_GAME_ROTATION_VECTOR -> {
                sensorName = "game_rotation_vector"
            }

            android.hardware.Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> {
                sensorName = "geomagnetic_rotation_vector"
            }

            android.hardware.Sensor.TYPE_GRAVITY -> {
                sensorName = "gravity"
            }

            android.hardware.Sensor.TYPE_GYROSCOPE -> {
                sensorName = "gyroscope"
            }

            android.hardware.Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> {
                sensorName = "gyroscope_uncalibrated"
            }

            android.hardware.Sensor.TYPE_HEART_BEAT -> {
                sensorName = "heart_beat"
            }

            android.hardware.Sensor.TYPE_HEART_RATE -> {
                sensorName = "heart_rate"
            }

            android.hardware.Sensor.TYPE_LIGHT -> {
                sensorName = "light"
            }

            android.hardware.Sensor.TYPE_LINEAR_ACCELERATION -> {
                sensorName = "linear_acceleration"
            }

            android.hardware.Sensor.TYPE_MAGNETIC_FIELD -> {
                sensorName = "magnetic_field"
            }

            android.hardware.Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> {
                sensorName = "magnetic_field_uncalibrated"
            }

            android.hardware.Sensor.TYPE_MOTION_DETECT -> {
                sensorName = "motion_detect"
            }

            android.hardware.Sensor.TYPE_POSE_6DOF -> {
                sensorName = "pose_6dof"
            }

            android.hardware.Sensor.TYPE_PRESSURE -> {
                sensorName = "pressure"
            }

            android.hardware.Sensor.TYPE_PROXIMITY -> {
                sensorName = "proximity"
            }

            android.hardware.Sensor.TYPE_RELATIVE_HUMIDITY -> {
                sensorName = "relative_humidity"
            }

            android.hardware.Sensor.TYPE_ROTATION_VECTOR -> {
                sensorName = "rotation_vector"
            }

            android.hardware.Sensor.TYPE_SIGNIFICANT_MOTION -> {
                sensorName = "significant_motion"
            }

            android.hardware.Sensor.TYPE_STATIONARY_DETECT -> {
                sensorName = "stationary_detect"
            }

            android.hardware.Sensor.TYPE_STEP_COUNTER -> {
                sensorName = "step_counter"
            }

            android.hardware.Sensor.TYPE_STEP_DETECTOR -> {
                sensorName = "step_detector"
            }

            android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                sensorName = "ambient_temperature"
            }
        }
        return sensorName
    }

    override fun onSensorChanged(event: android.hardware.SensorEvent?) {

        var reading: SensorReading? = null
        val sensorName = getSourceName(event!!)

        if(sensorName == null) {
            return
        }
        val valueList = ArrayList<Float>(event.values.toList())
        reading = SensorReading(source = sensorName, sessionID=this.sessionID, timestamp = event.timestamp, values = valueList)
        buffer += reading
        if(!hasRaisedNotification and (buffer.size >= notifyAtSizeCount)) {
            hasRaisedNotification = true
        }
        if(buffer.size > notifyAtSizeCount*4) {
            buffer.removeAt(0)
        }
    }

    fun Flush() {
        this.listener.onBufferReady(this.buffer)
        buffer = ArrayList<SensorReading>()
        hasRaisedNotification = false
    }
}