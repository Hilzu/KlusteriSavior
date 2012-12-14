package hlz.klusterisavior

import _root_.android.app.Activity
import _root_.android.os.Bundle
import android.content.Context
import android.hardware.{SensorEventListener, SensorEvent, Sensor, SensorManager}
import KlusteriLocation._
import android.location.{Location, LocationManager, LocationListener}

class MainActivity extends Activity with TypedActivity with SensorEventListener with LocationListener {
  private lazy val sensorManager = getSystemService(Context.SENSOR_SERVICE).asInstanceOf[SensorManager]
  private lazy val locationManager = getSystemService(Context.LOCATION_SERVICE).asInstanceOf[LocationManager]
  private lazy val orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
  private lazy val compassView = findView(TR.compass_view)
  private lazy val locationText = findView(TR.location_text)
  private var currentLocation: Location = null

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
    val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    currentLocation =
      if (lastKnownLocation == null) new Location("None")
      else lastKnownLocation
    setLocationText(currentLocation)
  }

  override def onStart() {
    super.onStart()
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this)
  }

  override def onResume() {
    super.onResume()
    sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL)
  }

  override def onPause() {
    super.onPause()
    sensorManager.unregisterListener(this)
  }

  override def onStop() {
    super.onStop()
    locationManager.removeUpdates(this)
  }

  def onSensorChanged(event: SensorEvent) {
    val azimuth = event.values(0)
    compassView.setDirection(bearingToKlusteri(azimuth, currentLocation))
  }

  def onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

  def onLocationChanged(location: Location) {
    currentLocation = location
    setLocationText(location)
  }

  def setLocationText(loc: Location) {
    locationText.setText("Location Lat: %f Long: %f".format(loc.getLatitude, loc.getLongitude))
  }

  def onProviderDisabled(provider: String) {}

  def onStatusChanged(provider: String, status: Int, extras: Bundle) {}

  def onProviderEnabled(provider: String) {}
}
