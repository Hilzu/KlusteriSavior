package hlz.klusterisavior

import android.app.Activity
import android.os.Bundle
import android.content.Context
import android.hardware.{SensorEventListener, SensorEvent, Sensor, SensorManager}
import MyLocation._
import android.location.{Location, LocationManager, LocationListener}

class MainActivity extends Activity with TypedActivity with SensorEventListener with LocationListener {
  private lazy val sensorManager = getSystemService(Context.SENSOR_SERVICE).asInstanceOf[SensorManager]
  private lazy val locationManager = getSystemService(Context.LOCATION_SERVICE).asInstanceOf[LocationManager]
  private lazy val orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
  private lazy val compassView = findView(TR.compass_view)
  private lazy val locationText = findView(TR.location_text)
  private lazy val distanceText = findView(TR.distance_text)
  private var currentLocation: Location = null

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
    val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    currentLocation =
      if (lastKnownLocation == null) new Location("None")
      else lastKnownLocation
    setLocationText(currentLocation)
    setDistanceText(currentLocation)
  }

  override def onStart() {
    super.onStart()
  }

  override def onResume() {
    super.onResume()
    sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL)
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this)
  }

  override def onPause() {
    super.onPause()
    sensorManager.unregisterListener(this)
    locationManager.removeUpdates(this)
  }

  override def onStop() {
    super.onStop()
  }

  def onSensorChanged(event: SensorEvent) {
    val azimuth = event.values(0)
    compassView.setDirection(directionToKlusteri(azimuth, currentLocation))
  }

  def onLocationChanged(location: Location) {
    currentLocation = location
    setLocationText(location)
    setDistanceText(location)
  }

  def setLocationText(loc: Location) {
    locationText.setText("Location Lat: %f Long: %f".format(loc.getLatitude, loc.getLongitude))
  }

  def setDistanceText(loc: Location) {
    distanceText.setText("Distance: " + (loc distanceTo klusteri) + " meters")
  }

  def onProviderDisabled(provider: String) {}

  def onStatusChanged(provider: String, status: Int, extras: Bundle) {}

  def onProviderEnabled(provider: String) {}

  def onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}
