package hlz.klusterisavior

import android.app.Activity
import android.os.Bundle
import android.content.Context
import android.text.format.Time

class AtKlusteriActivity extends Activity with TypedActivity {
  private lazy val preferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

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
    setLastTimeAtKlusteriToNow()
  }
}
