package com.github.gameinventory.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.ContextCompat.getSystemService
import kotlin.math.sqrt

class SensorDetector(
    context: Context,
    private val action: ()->Unit
): SensorEventListener {
    private val shakeThreshold=2.1f
    private val shakeCoolDown = 1000L
    private var lastShakeTime = 0L


    private  var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)


    fun start(){
        if(accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    fun close(){
        sensorManager.unregisterListener(this)
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if(p0 != null){
            val x= p0.values[0]
            val y=p0.values[1]
            val z= p0.values[2]

            val xG= x/ SensorManager.GRAVITY_EARTH
            val yG=y/SensorManager.GRAVITY_EARTH
            val zG= z/SensorManager.GRAVITY_EARTH

            var gForce= sqrt(xG*xG + yG*yG + zG*zG)

            if(gForce >= shakeThreshold){
                val now = System.currentTimeMillis()
                if(  now -lastShakeTime >=shakeCoolDown){
                    lastShakeTime=now
                    action()
                }
            }
        }


    }
}