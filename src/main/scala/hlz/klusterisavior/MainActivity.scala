package hlz.klusterisavior

import android.app.Activity
import android.os.Bundle
import android.content.Context
import android.hardware.{SensorEventListener, SensorEvent, Sensor, SensorManager}
import MyLocation._
import android.location.{Location, LocationManager, LocationListener}
import scala.math.toDegrees

class MainActivity extends Activity with TypedActivity with SensorEventListener with LocationListener {
  private lazy val sensorManager = getSystemService(Context.SENSOR_SERVICE).asInstanceOf[SensorManager]
  private lazy val locationManager = getSystemService(Context.LOCATION_SERVICE).asInstanceOf[LocationManager]
  private lazy val compassView = findView(TR.compass_view)
  private lazy val locationText = findView(TR.location_text)
  private lazy val distanceText = findView(TR.distance_text)
  private lazy val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
  private lazy val magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
  private var currentLocation: Location = null
  private var accelerometerValues: Array[Float] = null
  private var magneticValues: Array[Float] = null

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)

    val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    if (!gpsEnabled) {
      val alertDialog = new GpsAlertDialog()
      alertDialog.show(getFragmentManager, "GpsAlertDialog")
    }
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
    sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
    sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL)
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
    event.sensor.getType match {
      case Sensor.TYPE_ACCELEROMETER => accelerometerValues = event.values.clone()
      case Sensor.TYPE_MAGNETIC_FIELD => magneticValues = event.values.clone()
    }
    if (accelerometerValues == null || magneticValues == null) return

    val rotationMatrix = new Array[Float](16)
    if (!SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magneticValues)) return

    val orientation = new Array[Float](3)
    SensorManager.getOrientation(rotationMatrix, orientation)

    compassView.setDirection(directionToKlusteri(toDegrees(orientation(0)), currentLocation))
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
