package hlz.klusterisavior

import android.app.Activity
import android.os.{Handler, Bundle}
import android.content.{Intent, Context}
import android.text.format.Time
import android.location.{Location, LocationListener, LocationManager}

class AtKlusteriActivity extends Activity with TypedActivity with LocationListener {
  private lazy val preferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
  private lazy val chronometer = findView(TR.chronometer)
  private lazy val locationManager = getSystemService(Context.LOCATION_SERVICE).asInstanceOf[LocationManager]
  private val lastTimeAtKlusteri: Time = new Time()
  private lazy val handler = new Handler()

  val updateChronometer = new Runnable() {
    def run() {
      val now = new Time()
      now.setToNow()
      val millis = now.toMillis(false) - lastTimeAtKlusteri.toMillis(false)

      val hours = millis / 3600000
      val minutes = (millis % 3600000) / 60000
      val seconds = ((millis % 3600000) % 60000) / 1000

      chronometer.setText("%02d:%02d:%02d".format(hours, minutes, seconds))
      handler.postDelayed(this, 1000)
    }
  }

  def setLastTimeAtKlusteri(time: Time) {
    val prefEditor = preferences.edit()
    prefEditor.putString("LAST_TIME_AT_KLUSTERI", time.format3339(false))
    prefEditor.commit()
  }

  def setLastTimeAtKlusteriToNow() {
    val now = new Time()
    now.setToNow()
    setLastTimeAtKlusteri(now)
  }

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.at_klusteri)

    if (!preferences.getBoolean("AT_KLUSTERI", true)) setLastTimeAtKlusteriToNow()

    preferences.edit().putBoolean("AT_KLUSTERI", true)
    val lastTimeString = preferences.getString("LAST_TIME_AT_KLUSTERI", "")
    lastTimeAtKlusteri.parse3339(lastTimeString)
  }

  override def onStart() {
    super.onStart()

    handler.post(updateChronometer)
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this)
  }


  override def onPause() {
    super.onPause()
    handler.removeCallbacks(updateChronometer)
    locationManager.removeUpdates(this)
  }

  def onLocationChanged(location: Location) {
    if (!MyLocation.atKlusteri(location)) {
      preferences.edit().putBoolean("AT_KLUSTERI", false)
      val intent = new Intent(this, classOf[MainActivity])
      startActivity(intent)
      finish()
    }
  }

  def onProviderDisabled(provider: String) {}

  def onProviderEnabled(provider: String) {}

  def onStatusChanged(provider: String, status: Int, extras: Bundle) {}
}

object AtKlusteriActivity {
}
