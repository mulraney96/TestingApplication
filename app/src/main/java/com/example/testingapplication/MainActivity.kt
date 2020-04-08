package com.example.testingapplication

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var sensorManager: SensorManager

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accSensors = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val rotVec = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        Log.i("Acceleration Sensors", "${accSensors.vendor}, ${accSensors.name}, ${accSensors.version}, ${accSensors.power}, ${accSensors.resolution}, " +
                "${accSensors.minDelay} , ${accSensors.maximumRange}")
        Log.i("Gyro", "${gyro.vendor}, ${gyro.name}, ${gyro.reportingMode}, ${gyro.resolution} , ${gyro.power}" +
                ", ${gyro.maximumRange}, ${gyro.maxDelay}")
        Log.i("Magnet", "${rotVec.vendor}, ${rotVec.name}, ${rotVec.power}, ${rotVec.resolution} , ${rotVec.maxDelay}" +
                ", ${rotVec.maximumRange}, ${rotVec.minDelay}")
    }

    fun startRoll(view: View){
        val intent = Intent(this, getRollActivity::class.java)
        startActivity(intent)
    }

    fun saveFileName(view: View){
        val filename = editText.text.toString()
        AngleValues.setFileName(filename)
        Toast.makeText(this, "Set to $filename", Toast.LENGTH_SHORT).show()
    }

}
