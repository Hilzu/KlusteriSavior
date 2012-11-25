package hlz.klusterisavior

import _root_.android.app.Activity
import _root_.android.os.Bundle
import android.content.Context
import android.hardware.{SensorEventListener, SensorEvent, Sensor, SensorManager}

class MainActivity extends Activity with TypedActivity with SensorEventListener {
  private lazy val sensorManager = getSystemService(Context.SENSOR_SERVICE).asInstanceOf[SensorManager]
  private lazy val orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
  private lazy val compassView = findView(TR.compass_view)

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
  }

  override def onResume() {
    super.onResume()
    sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL)
  }

  override def onPause() {
    super.onPause()
    sensorManager.unregisterListener(this)
  }

  def onSensorChanged(event: SensorEvent) {
    val azimuth = event.values(0)
    compassView.setDirection(azimuth)
  }

  def onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}
