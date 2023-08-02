package net.j2i.senselogger

import android.hardware.SensorEventListener

interface ISensorLoggingReceiverListener    {
    fun onBufferReady(buffer:List<SensorReading>);
    fun onReadingReceived(reading:SensorReading);
}