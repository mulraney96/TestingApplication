package com.example.testingapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
