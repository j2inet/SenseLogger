package net.j2i.senselogger
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log



const val DATABASE_NAME = "sensor_reading_database"
const val DATABASE_VERSION = 1

class SensorReadingDataHelper (context:Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    object SensorReadingContract: BaseColumns {
        const val TABLE_NAME = "sensor_reading"
        const val COLUMN_NAME_SESSIONID = "sessionID"
        const val COLUMN_NAME_SENSORNAME = "sensor_name"
        const val COLUMN_NAME_TIMESTAMP = "timestamp"
        const val COLUMN_NAME_ADJUSTEDTIMESTAMP = "adjustedTimestamp"
        const val COLUMN_NAME_VALUE_0 = "value0"
        const val COLUMN_NAME_VALUE_1 = "value1"
        const val COLUMN_NAME_VALUE_2 = "value2"
        const val COLUMN_NAME_VALUE_3 = "value3"
        const val COLUMN_NAME_VALUE_4 = "value4"
        const val COLUMN_NAME_VALUE_5 = "value5"


        const val COLUMN_NAME_CLIENTID = "clientID"

        const val CREATE_TABLE_QUERY = "CREATE TABLE ${SensorReadingContract.TABLE_NAME} ("+
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${SensorReadingContract.COLUMN_NAME_SESSIONID}  INTEGER, "+
                "${SensorReadingContract.COLUMN_NAME_SENSORNAME}  TEXT NOT NULL, "+
                "${SensorReadingContract.COLUMN_NAME_TIMESTAMP}  INT, "+
                "${SensorReadingContract.COLUMN_NAME_ADJUSTEDTIMESTAMP}  INT, "+
                "${SensorReadingContract.COLUMN_NAME_VALUE_0}  REAL ,"+
                "${SensorReadingContract.COLUMN_NAME_VALUE_1}  REAL ,"+
                "${SensorReadingContract.COLUMN_NAME_VALUE_2}  REAL ,"+
                "${SensorReadingContract.COLUMN_NAME_VALUE_3}  REAL ,"+
                "${SensorReadingContract.COLUMN_NAME_VALUE_4}  REAL ,"+
                "${SensorReadingContract.COLUMN_NAME_VALUE_5}  REAL "+
                ")"
        const val DELETE_TABLES_QUERY = "DROP TABLE IF EXISTS ${TABLE_NAME}";
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SensorReadingContract.CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SensorReadingContract.DELETE_TABLES_QUERY)
        onCreate(db)
    }

    fun insertReadingList(readings:List<SensorReading>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            readings.forEach { reading ->
                insertReading(reading)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun insertReading(reading:SensorReading) {
        if(reading.values.size > 6) {
            Log.e("SensorReadingDataHelper", "Too many values in reading")
            return
        }
        if(reading.values.size == 0) {
            Log.e("SensorReadingDataHelper", "No values in reading")
            return
        }

        val db = writableDatabase
        val values = ContentValues().apply {
            put(SensorReadingContract.COLUMN_NAME_SENSORNAME, reading.source)
            put(SensorReadingContract.COLUMN_NAME_TIMESTAMP, reading.timestamp)
            put(SensorReadingContract.COLUMN_NAME_ADJUSTEDTIMESTAMP, reading.adjustedTimestamp)
            put(SensorReadingContract.COLUMN_NAME_SESSIONID, reading.sessionID)
            if(reading.values.size > 0) {
                put(SensorReadingContract.COLUMN_NAME_VALUE_0, reading.values[0])
            }
            if(reading.values.size > 1) {
                put(SensorReadingContract.COLUMN_NAME_VALUE_1, reading.values[1])
            }
            if(reading.values.size > 2) {
                put(SensorReadingContract.COLUMN_NAME_VALUE_2, reading.values[2])
            }
            if(reading.values.size > 3) {
                put(SensorReadingContract.COLUMN_NAME_VALUE_3, reading.values[3])
            }
            if(reading.values.size > 4) {
                put(SensorReadingContract.COLUMN_NAME_VALUE_4, reading.values[4])
            }
            if(reading.values.size > 5) {
                put(SensorReadingContract.COLUMN_NAME_VALUE_5, reading.values[5])
            }

        }

        val newRowId = db?.insert(SensorReadingContract.TABLE_NAME, null, values)
    }



    fun clearDatabase() {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.execSQL(SensorReadingContract.DELETE_TABLES_QUERY)
            db.execSQL(SensorReadingContract.CREATE_TABLE_QUERY)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getAllReadings():List<SensorReading>  {
        val readings = mutableListOf<SensorReading>()
        val db = writableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            SensorReadingContract.COLUMN_NAME_SENSORNAME,
            SensorReadingContract.COLUMN_NAME_TIMESTAMP,
            SensorReadingContract.COLUMN_NAME_ADJUSTEDTIMESTAMP,
            SensorReadingContract.COLUMN_NAME_SESSIONID,
            SensorReadingContract.COLUMN_NAME_VALUE_0,
            SensorReadingContract.COLUMN_NAME_VALUE_1,
            SensorReadingContract.COLUMN_NAME_VALUE_2,
            SensorReadingContract.COLUMN_NAME_VALUE_3,
            SensorReadingContract.COLUMN_NAME_VALUE_4,
            SensorReadingContract.COLUMN_NAME_VALUE_5,
        )
        val sortOrder = "${SensorReadingContract.COLUMN_NAME_TIMESTAMP} ASC"
        val cursor = db.query(
            SensorReadingContract.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            sortOrder
        )
        with(cursor) {
            while (moveToNext()) {
                val reading = SensorReading(
                    source = getString(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_SENSORNAME)),
                    timestamp = getLong(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_TIMESTAMP)),
                    adjustedTimestamp = getLong(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_ADJUSTEDTIMESTAMP)),
                    sessionID = getLong(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_SESSIONID))
                );
                if (!cursor.isNull(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_0))) {
                    reading.values.add(getFloat(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_0)))
                }
                if (!cursor.isNull(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_1))) {
                    reading.values.add(getFloat(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_1)))
                }
                if (!cursor.isNull(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_2))) {
                    reading.values.add(getFloat(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_2)))
                }
                if (!cursor.isNull(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_3))) {
                    reading.values.add(getFloat(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_3)))
                }
                if (!cursor.isNull(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_4))) {
                    reading.values.add(getFloat(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_4)))
                }
                if (!cursor.isNull(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_5))) {
                    reading.values.add(getFloat(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_5)))
                }
                readings.add(reading)
            }
        }
        return readings
    }

    fun getReadings(sessionID:Long):List<SensorReading> {
        val readings = mutableListOf<SensorReading>()
        val db = writableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            SensorReadingContract.COLUMN_NAME_SENSORNAME,
            SensorReadingContract.COLUMN_NAME_TIMESTAMP,
            SensorReadingContract.COLUMN_NAME_ADJUSTEDTIMESTAMP,
            SensorReadingContract.COLUMN_NAME_SESSIONID,
            SensorReadingContract.COLUMN_NAME_VALUE_0,
            SensorReadingContract.COLUMN_NAME_VALUE_1,
            SensorReadingContract.COLUMN_NAME_VALUE_2,
            SensorReadingContract.COLUMN_NAME_VALUE_3,
            SensorReadingContract.COLUMN_NAME_VALUE_4,
            SensorReadingContract.COLUMN_NAME_VALUE_5,
        )
        val selection = "${SensorReadingContract.COLUMN_NAME_SESSIONID} = ?"
        val selectionArgs = arrayOf(sessionID.toString())
        val sortOrder = "${SensorReadingContract.COLUMN_NAME_TIMESTAMP} ASC"
        val cursor = db.query(
            SensorReadingContract.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )
        with(cursor) {
            while (moveToNext()) {
                val reading = SensorReading(
                    source = getString(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_SENSORNAME)),
                    sessionID = getLong(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_SESSIONID)),
                    timestamp = getLong(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_TIMESTAMP)),
                    adjustedTimestamp = getLong(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_ADJUSTEDTIMESTAMP))
                )
                if (!cursor.isNull(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_0))) {
                    reading.values.add(getFloat(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_0)))
                }
                if (!cursor.isNull(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_1))) {
                    reading.values.add(getFloat(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_1)))
                }
                if (!cursor.isNull(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_2))) {
                    reading.values.add(getFloat(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_2)))
                }
                if (!cursor.isNull(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_3))) {
                    reading.values.add(getFloat(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_3)))
                }
                if (!cursor.isNull(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_4))) {
                    reading.values.add(getFloat(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_4)))
                }
                if (!cursor.isNull(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_5))) {
                    reading.values.add(getFloat(getColumnIndexOrThrow(SensorReadingContract.COLUMN_NAME_VALUE_5)))
                }
                readings.add(reading)
            }
        }
        cursor.close()
        return readings
    }
}