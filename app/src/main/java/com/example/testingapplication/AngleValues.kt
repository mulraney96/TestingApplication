package com.example.testingapplication


object AngleValues {
    //singleton class
    private var yawAngle: Float = 0.0f
    private var rollAngle: Float = 0.0f
    private var pitchAngle: Float = 0.0f
    private var FILENAME = "default.csv"


    fun setFileName(file: String){
        this.FILENAME = file
    }

    fun getFileName():String{
        return this.FILENAME
    }

    fun setYawValues(yaw: Float) {
        yawAngle = yaw
    }

    fun setPitchValues(pitch: Float) {
        pitchAngle = pitch
    }

    fun setRollValues(roll: Float) {
        rollAngle = roll
    }

    fun getAzimuth(): Float {
        return yawAngle
    }

    fun getPitch(): Float {
        return pitchAngle
    }

    fun getRollOffset(): Float {
        return rollAngle
    }

}