package hlz.klusterisavior

import android.app.Activity
import android.os.Bundle
import android.content.{Intent, Context}
import android.hardware.{SensorEventListener, SensorEvent, Sensor, SensorManager}
import MyLocation._
import android.location.{Location, LocationManager, LocationListener}
import android.text.format
import android.util.TimeFormatException

class MainActivity extends Activity with TypedActivity with SensorEventListener with LocationListener {
  private lazy val sensorManager = getSystemService(Context.SENSOR_SERVICE).asInstanceOf[SensorManager]
  private lazy val locationManager = getSystemService(Context.LOCATION_SERVICE).asInstanceOf[LocationManager]
  private lazy val compassView = findView(TR.compass_view)
  private lazy val locationText = findView(TR.location_text)
  private lazy val distanceText = findView(TR.distance_text)
  private lazy val lastTimeAtKlusteriText = findView(TR.last_time_text)
  private lazy val orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
  private lazy val preferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
  private var currentLocation: Location = null

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)

    val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    if (!gpsEnabled) {
      val alertDialog = new GpsAlertDialog()
      alertDialog.show(getFragmentManager, "GpsAlertDialog")
    }
    val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    onLocationChanged(if (lastKnownLocation == null) new Location("None") else lastKnownLocation)
    if (!atKlusteri(currentLocation)) preferences.edit().putBoolean("AT_KLUSTERI", false)
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
    setLastTimeAtKlusteriText()
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
    if (event.sensor.getType != Sensor.TYPE_ORIENTATION) return

    compassView.setDirection(directionToKlusteri(event.values(0), currentLocation))
  }

  def onLocationChanged(location: Location) {
    currentLocation = location
    if (atKlusteri(location)) {
      val intent = new Intent(this, classOf[AtKlusteriActivity])
      startActivity(intent)
      finish()
    } else {
      setLocationText(location)
      setDistanceText(location)
    }
  }

  def setLocationText(loc: Location) {
    locationText.setText("(%.5f, %.5f)".format(loc.getLongitude, loc.getLatitude))
  }

  def setDistanceText(loc: Location) {
    distanceText.setText("%.0f meters".format(loc distanceTo klusteri))
  }

  def setLastTimeAtKlusteriText() {
    val lastTimeString = preferences.getString("LAST_TIME_AT_KLUSTERI", "")
    val lastTime = new format.Time()
    try {
      lastTime.parse3339(lastTimeString)
      lastTimeAtKlusteriText.setText(lastTime.format("%d.%m.%Y %H:%M"))
    } catch {
      case ex: TimeFormatException =>
        lastTimeAtKlusteriText.setText("Never")
    }
  }

  def onProviderDisabled(provider: String) {}

  def onStatusChanged(provider: String, status: Int, extras: Bundle) {}

  def onProviderEnabled(provider: String) {}

  def onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}
