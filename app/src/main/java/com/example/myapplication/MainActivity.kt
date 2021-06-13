package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import cn.iwgang.countdownview.CountdownView
import io.paperdb.Paper

class MainActivity : AppCompatActivity() {
    //private val btn_start: Any



    //private val IS_START_KEY: String?
    private val LIMIT_TIME: Long = 15 * 1000    //15second
    var isStart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        Paper.init(this)

        isStart = Paper.book().read(IS_START_KEY, false)
        if(isStart){
            btn_start.isEnabled = false
            cv_countdownView.start(LIMIT_TIME)
           checkTime()
        } else btn_start.isEnabled=true

        //event
        btn_start.setOnClickListener{
            if(!isStart){
                cv_countdownView.start(LIMIT_TIME)
                Paper.book().write(IS_START_KEY, true)
            }
        }
        cv_countdownView.setOnCountDownEndListener {
            Toast.makeText(this, "Finish!!", Toast.LENGTH_SHORT).show()
            reset()
        }
        cv_countdownView.setOnCountDownIntervalListener(1000, object : CountdownView.OnCountdownIntervalListener {
            override fun onInterval(cv: CountdownView?, remainTime: Long) {
                Log.d("TIMER", ""+remainTime)
            }
        })
    }

    override fun onStop() {
        Paper.book().write(TIME_REMAIN, cv_countdownView.remainTime)
        Paper.book().write(LAST_TIME_SAVED_KEY, System.currentTimeMillis())
        super.onStop()
    }

    private fun checkTime() {
        val currentTime = System.currentTimeMillis()
        val lastTimeSaved:Long=Paper.book().read(LAST_TIME_SAVED_KEY, 0).toLong()
        val timeRemain:Long=Paper.book().read(TIME_REMAIN, 0).toLong()
        val result = timeRemain + (lastTimeSaved - currentTime)
        if(result > 0){
            cv_countdownView!!.start(result)
        } else {
            cv_countdownView.stop()
            reset()
        }
        }

    private fun reset() {
        btn_start.isEnabled=true
        Paper.book().delete(IS_START_KEY)
        Paper.book().delete(LAST_TIME_SAVED_KEY)
        Paper.book().delete(TIME_REMAIN)
        isStart = false
    }
}

    companion object {
        private const val IS_START_KEY = "IS_START"
        private const val LAST_TIME_SAVED_KEY = "LAST TIME SAVED"
        private const val TIME_REMAIN = "TIME_REMAIN"

    }
}