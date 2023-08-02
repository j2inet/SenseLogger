package net.j2i.motionsense

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import net.j2i.motionsense.ui.theme.MotionSenseTheme
import net.j2i.senselogger.LoggingManager
import java.io.OutputStream
import java.io.OutputStreamWriter
import kotlinx.serialization.json.JsonObject

class MainActivity : ComponentActivity() {
    lateinit var loggingManager:LoggingManager

    val PICKER_EXPORT_READING = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MotionSenseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UILayout(this)
                }
            }
        }
        loggingManager = LoggingManager(this)
    }

    fun playSound(isMajor:Boolean = true) {
        val asset = Uri.parse("android.resource://${packageName}/raw/${if (isMajor) net.j2i.senselogger.R.raw.major else net.j2i.senselogger.R.raw.minor }")
        val player = MediaPlayer()
        player.setDataSource(this, asset)
        player.prepare()
        player.start()
        player.setOnCompletionListener { player.release() }
        player.isLooping = false

    }

    fun startLogging() {
        val sensorList = arrayListOf<Int>(android.hardware.Sensor.TYPE_ACCELEROMETER, android.hardware.Sensor.TYPE_GYROSCOPE)
        loggingManager.startLogging(sensorList)
        playSound(true)
    }

    fun stopLogging() {
        loggingManager.stopLogging()
        playSound(false)
    }

    fun exportData() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "sensor_readings.json")
        }
        this.startActivityForResult(intent, PICKER_EXPORT_READING)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            PICKER_EXPORT_READING -> {
                if (resultCode == RESULT_OK) {
                    val loggedData = loggingManager.exportData()

                    if(data?.data == null) return
                    var uri: Uri = data?.data as Uri;

                    var jsonItemList = ArrayList<JsonObject>()
                    for(reading in loggedData) {
                        jsonItemList.add(reading.getJobject())
                    }
                    var dataString = jsonItemList.toString()
                    val os: OutputStream? = contentResolver.openOutputStream(uri)
                    if(os!=null) {
                        val pw = OutputStreamWriter(os)
                        try {
                            pw.write(dataString)
                        } finally {
                            pw.close()
                        }
                    }
                }
            }
        }
    }

}



@Composable
fun UILayout(a:MainActivity,modifier:Modifier = Modifier) {
    Column(modifier = modifier, content = {
        Text(text = "Sensor Logger")
        Button(onClick = { a?.startLogging() }, modifier = modifier, content = { Text("Start") });
        Button(onClick = { a?.stopLogging() }, modifier = modifier, content = { Text("Stop") });
        Text(text = "Export Data")
        Button(onClick = { a?.exportData() }, modifier = modifier, content = { Text("Export") });

    })
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MotionSenseTheme {
        Greeting("Android")
    }
}