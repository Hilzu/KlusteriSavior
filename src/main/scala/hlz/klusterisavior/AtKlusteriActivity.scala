package hlz.klusterisavior

import android.app.Activity
import android.os.Bundle

class AtKlusteriActivity extends Activity with TypedActivity {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.at_klusteri)
  }
}
