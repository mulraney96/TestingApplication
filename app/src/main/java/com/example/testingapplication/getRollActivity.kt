package com.example.testingapplication

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.activity_get_roll.*
import java.io.FileOutputStream

@TargetApi(Build.VERSION_CODES.CUPCAKE)
class getRollActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager

    private var MagAccOrientationValues = arrayListOf<Float>()
    private var dtListMagAcc = arrayListOf<Float>()
    var angleValuesMagAcc: ArrayList<Float> = arrayListOf()
    var counterMagAcc: Int = 0
    var xAxisMagAcc: Float = 0.0f
    var eventTime: Long = 0L
    var timestampMagAcc: Long = 0L
    var Mag_Readings: FloatArray? = floatArrayOf(0.0f, 0.0f, 0.0f)
    var Acc_Readings: FloatArray? = floatArrayOf(0.0f, 0.0f,0.0f ,0.0f)

    var counterGameVec: Int = 0
    var xAxisGameVec: Float = 0.0f
    var timestampGameVec: Long = 0L
    var angleValues: ArrayList<Float> = arrayListOf()
    private var gameVecOrientationValues = arrayListOf<Float>()
    private var dtListGameVec = arrayListOf<Float>()

    var counterVec: Int = 0
    var xAxisVec: Float = 0.0f
    var timestampVec: Long = 0L
    var angleValuesVec: ArrayList<Float> = arrayListOf()
    private var VecOrientationValues = arrayListOf<Float>()
    private var dtListVec = arrayListOf<Float>()

    private var averageCounter = -1

    fun reset(){
        MagAccOrientationValues.clear()
        dtListMagAcc.clear()
        angleValuesMagAcc.clear()
        eventTime=0L
        counterMagAcc=0
        xAxisMagAcc = 0.0f
        timestampMagAcc=0L
        Mag_Readings = floatArrayOf(0.0f, 0.0f, 0.0f)
        Acc_Readings = floatArrayOf(0.0f, 0.0f, 0.0f)

        counterGameVec=0
        xAxisGameVec=0.0f
        timestampGameVec=0L
        angleValues.clear()
        gameVecOrientationValues.clear()
        dtListGameVec.clear()

        counterVec=0
        xAxisVec=0.0f
        timestampVec=0L
        angleValuesVec.clear()
        VecOrientationValues.clear()
        dtListVec.clear()
    }


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) //locks in portrait mode
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_roll)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

    }


    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        reset()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }


    override fun onSensorChanged(event: SensorEvent) {

        if (event.sensor.type == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            var rotationMatrix: FloatArray =
                floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
            var orientation: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)

            if (timestampGameVec != 0L) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event!!.values)
                SensorManager.getOrientation(rotationMatrix, orientation)

                xAxisGameVec += ((event.timestamp - timestampGameVec) * (1.0f / 1000000000.0f))
                var roll = (orientation[2]*57.2958f) // convert to degrees
                var pitch = (orientation[1]*57.2958f)
                var yaw = (orientation[0]*57.2958f)


                gameVecOrientationValues.add(roll)
                gameVecOrientationValues.add(pitch)
                gameVecOrientationValues.add(yaw)
                dtListGameVec.add(xAxisGameVec)


                if (counterGameVec > 150) {
                    angleValues.add(yaw)
                }
                if (counterGameVec == 250) {
                    calculateAverage(angleValues, 1)
                    averageCounter++
                    saveList(gameVecOrientationValues, dtListGameVec, "GameVec${AngleValues.getFileName()}")
                    sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR))

                }
            }
            counterGameVec++
            timestampGameVec = event.timestamp
        }



        if(event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            var rotationMatrix: FloatArray =
                floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
            var orientation: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)

            if(timestampVec!=0L){
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event!!.values)
                SensorManager.getOrientation(rotationMatrix, orientation)

                xAxisVec += ((event.timestamp - timestampVec)*(1.0f/1000000000.0f))
                var roll = (orientation[2]*57.2958f) // convert to degrees
                var pitch = (orientation[1]*57.2958f)
                var yaw = (orientation[0]*57.2958f)

                VecOrientationValues.add(roll)
                VecOrientationValues.add(pitch)
                VecOrientationValues.add(yaw)
                dtListVec.add(xAxisVec)

                if(counterVec>150){
                    angleValuesVec.add(yaw)
                }
                if(counterVec==250){
                    calculateAverage(angleValuesVec, 2)
                    averageCounter++
                    saveList(VecOrientationValues, dtListVec, "RotationVec${AngleValues.getFileName()}")
                    sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR))

                }
            }
            counterVec++
            timestampVec = event.timestamp
        }



        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER){
            Acc_Readings = event.values.clone()
            eventTime = event.timestamp
            calculateMagAccOrientation(Mag_Readings, Acc_Readings, eventTime)
            timestampMagAcc = eventTime
        }
        if(event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD){
            Mag_Readings = event.values.clone()
            eventTime = event.timestamp
            calculateMagAccOrientation(Mag_Readings, Acc_Readings, eventTime)
            timestampMagAcc = eventTime
        }


    }

    fun calculateMagAccOrientation(MagR: FloatArray?, AccR: FloatArray?, eventTime: Long){
        if(MagR != null && AccR != null){
            if(timestampMagAcc!=0L) {
                var rotationMatrix: FloatArray =
                    floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
                var orientation: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)

                SensorManager.getRotationMatrix(rotationMatrix, null, AccR, MagR)
                SensorManager.getOrientation(rotationMatrix, orientation)
                xAxisMagAcc += ((eventTime - timestampMagAcc) * (1.0f / 1000000000.0f))

                var roll = (orientation[2] * 57.2958f) // convert to degrees
                var pitch = (orientation[1] * 57.2958f)
                var yaw = (orientation[0] * 57.2958f)

                MagAccOrientationValues.add(roll)
                MagAccOrientationValues.add(pitch)
                MagAccOrientationValues.add(yaw)
                dtListMagAcc.add(xAxisMagAcc)

                if(counterMagAcc>400){
                    angleValuesMagAcc.add(yaw)
                }
                if(counterMagAcc==500){
                    calculateAverage(angleValuesMagAcc, 3)
                    this.averageCounter++
                    saveList(MagAccOrientationValues, dtListMagAcc, "MagAcc${AngleValues.getFileName()}")
                    sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
                    sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD))
                }
            }
        }
        counterMagAcc++
    }

    fun calculateAverage(arr: ArrayList<Float>, type: Int){
        val size = arr.size-1
        Log.i("Size", "$size")
        var sum = 0.0f
        for(i in 0..size){
            sum += arr[i]
        }
        Log.i("Sum", "$sum")
        AngleValues.setRollValues(sum/(size+1))

        if(type==1) {
            text_roll_value.text = "GameVec Average= ${AngleValues.getRollOffset()}"
        }
        if(type==2){
            text_roll_value2.text = "Vec Average= ${AngleValues.getRollOffset()}"
        }
        if(type==3){
            text_roll_value3.text = "MagAcc Average = ${AngleValues.getRollOffset()}"
        }
    }

    fun saveList(list: ArrayList<Float>, dtList: ArrayList<Float>, fileName: String){

        var entry = ""
        var size = (list.size/3)-1
        for(i in 0..size){
            entry = entry.plus("${dtList[i]},${list[i*3]},${list[i*3+1]},${list[i*3+2]}\n")
        }
        try{
            var out: FileOutputStream = openFileOutput("$fileName", Context.MODE_APPEND)
            out.write(entry.toByteArray())

            Toast.makeText(this, "Saved to $filesDir/$fileName", Toast.LENGTH_SHORT).show()
            out.close()
            Log.i("Counter", "$averageCounter")
            Log.i("File", "Written")
        }
        catch(e: Exception){
            e.printStackTrace()
        }
        finally{
            if(averageCounter>=3){
                sensorManager.unregisterListener(this)
            }
        }
    }

//    fun saveList(view: View){
//        sensorManager.unregisterListener(this)
//
//        var FileName: String  = OffsetValues.getFileName()
//        var xFileName: String  = "x".plus(FileName)
//        var yFileName: String  = "y".plus(FileName)
//        var zFileName: String  = "z".plus(FileName)
//
//
//        var Xentry = "0,0,0,0,0\n"
//        var Yentry = "0,0,0,0,0\n"
//        var Zentry = "0,0,0,0,0\n"
//
//        var size = zAcc.size-1
//        for(i in 0..size){
//            Xentry = Xentry.plus("${dtList[i]},${xAcc[i]},${xVel[i]},${xDist[i]},${xPos[i]}\n")
//            Yentry = Yentry.plus("${dtList[i]},${yAcc[i]},${yVel[i]},${yDist[i]},${yPos[i]}\n")
//            Zentry = Zentry.plus("${dtList[i]},${zAcc[i]},${zVel[i]},${zDist[i]},${zPos[i]}\n")
//        }
//        try{
//            var out: FileOutputStream = openFileOutput(zFileName, Context.MODE_APPEND)
//            out.write(Zentry.toByteArray())
//            Toast.makeText(this, "Saved to $filesDir/$zFileName", Toast.LENGTH_SHORT).show()
//            out.close()
//
//            var outX: FileOutputStream = openFileOutput(xFileName, Context.MODE_APPEND)
//            outX.write(Xentry.toByteArray())
//            Toast.makeText(this, "Saved to $filesDir/$xFileName", Toast.LENGTH_SHORT).show()
//            outX.close()
//
//            var outY: FileOutputStream = openFileOutput(yFileName, Context.MODE_APPEND)
//            outY.write(Yentry.toByteArray())
//            Toast.makeText(this, "Saved to $filesDir/$yFileName", Toast.LENGTH_SHORT).show()
//            outY.close()
//        }
//        catch(e: Exception){
//            e.printStackTrace()
//        }
//
//    }

}
