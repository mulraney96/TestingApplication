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

    private var rollValues = arrayListOf<Entry>()
    private var rollValuesVec = arrayListOf<Entry>()

    var graphRoll: LineChart? = null
    var graphRollVec: LineChart? = null

    var counterGameVec: Int = 0
    var counterVec: Int = 0
    var counterMagAcc: Int = 0
    var xAxis: Float = 0.0f
    var xAxisVec: Float = 0.0f
    var xAxisMagAcc: Float = 0.0f
    var timestampGameVec: Long = 0L
    var timestampVec: Long = 0L
    var timestampMagAcc: Long = 0L
    var angleValues: ArrayList<Float> = arrayListOf()
    var angleValuesVec: ArrayList<Float> = arrayListOf()

    var Mag_Readings: FloatArray? = floatArrayOf(0.0f, 0.0f, 0.0f)
    var Acc_Readings: FloatArray? = floatArrayOf(0.0f, 0.0f,0.0f ,0.0f)

    private var gameVecOrientationValues = arrayListOf<Float>()
    private var dtListGameVec = arrayListOf<Float>()
    private var VecOrientationValues = arrayListOf<Float>()
    private var dtListVec = arrayListOf<Float>()
    private var MagAccOrientationValues = arrayListOf<Float>()
    private var dtListMagAcc = arrayListOf<Float>()

    private var averageCounter = -1
    private var sensorType = 0



    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) //locks in portrait mode
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_roll)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        graphRoll = findViewById(R.id.graphRollGameVec)
        graphRollVec = findViewById(R.id.graphRollVec)

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

                xAxis += ((event.timestamp - timestampGameVec) * (1.0f / 1000000000.0f))
                var roll = (orientation[0]*57.2958f) // convert to degrees
                val graphEntry = Entry(xAxis, roll)
                rollValues.add(graphEntry)
                gameVecOrientationValues.add(roll)
                dtListGameVec.add(xAxis)

                val RollDataSet = LineDataSet(rollValues, "Game Rotation Vector Roll")
                RollDataSet.setDrawCircles(false)
                RollDataSet.setColor(Color.BLUE)
                val accLineData = LineData(RollDataSet)
                graphRoll!!.setData(accLineData)
                if (counterGameVec > 150) {
                    angleValues.add(roll)
                }
                if (counterGameVec >= 250) {
                    calculateAverage(angleValues, 1)
                    averageCounter++
                    saveList(gameVecOrientationValues, dtListGameVec, "GamVec_yawwroll302.csv")
                }
            }

            graphRoll!!.invalidate()
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
                var roll = (orientation[0]*57.2958f) // convert to degrees
                val graphEntry = Entry(xAxisVec, roll)
                rollValuesVec.add(graphEntry)
                VecOrientationValues.add(roll)
                dtListVec.add(xAxisVec)

                val RollDataSet = LineDataSet(rollValuesVec, "Rotation Vector - Roll")
                RollDataSet.setDrawCircles(false)
                RollDataSet.setColor(Color.RED)
                val accLineData = LineData(RollDataSet)
                graphRollVec!!.setData(accLineData)
                if(counterVec>150){
                    angleValuesVec.add(roll)
                }
                if(counterVec>=250){
                    calculateAverage(angleValuesVec, 2)
                    averageCounter++
                    saveList(VecOrientationValues, dtListVec, "VecA_yaww_roll302.csv")
                }
            }

            graphRollVec!!.invalidate()
            counterVec++
            timestampVec = event.timestamp
        }


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
    }

    fun saveList(list: ArrayList<Float>, dtList: ArrayList<Float>, fileName: String){

        var entry = ""
        var size = list.size-1
        for(i in 0..size){
            entry = entry.plus("${dtList[i]},${list[i]}\n")
        }
        try{
            var out: FileOutputStream = openFileOutput("$fileName", Context.MODE_APPEND)
            out.write(entry.toByteArray())

            Toast.makeText(this, "Saved to $filesDir/$fileName", Toast.LENGTH_LONG).show()
            out.close()
            Log.i("Counter", "$averageCounter")
            Log.i("File", "Written")
        }
        catch(e: Exception){
            e.printStackTrace()
        }
        finally{
            if(averageCounter>=2){
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
