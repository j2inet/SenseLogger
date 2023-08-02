package net.j2i.senselogger

interface ISensorLoggingReceiverListener    {
    fun onBufferReady(buffer:List<SensorReading>);
}