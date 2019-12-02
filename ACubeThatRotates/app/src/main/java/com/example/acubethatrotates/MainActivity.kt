package com.example.acubethatrotates

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MainActivity : Activity() {

    var view: OpenGLView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = OpenGLView(this)
        setContentView(view)
    }

    override fun onPause() {
        super.onPause()
        view!!.onPause()
    }

    override fun onResume() {
        super.onResume()
        view!!.onResume()
    }
}
