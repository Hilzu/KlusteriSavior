package hlz.klusterisavior

import android.app.{Dialog, DialogFragment}
import android.os.Bundle
import android.app.AlertDialog.Builder
import android.content.DialogInterface.OnClickListener
import android.content.{Intent, DialogInterface}
import GpsAlertDialog._

class GpsAlertDialog extends DialogFragment {
  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    val builder = new Builder(getActivity)
    builder.setMessage(R.string.gps_alert_message)

    builder.setPositiveButton(android.R.string.ok, () => {
      val intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
      startActivity(intent)
    })

    builder.setNegativeButton(android.R.string.cancel, (dialog: DialogInterface, id: Int) => {
      dialog.cancel()
    })

    builder.create()
  }
}

object GpsAlertDialog {
  implicit def func2OnClickListener(f: () => Unit): OnClickListener = {
    new OnClickListener {
      def onClick(dialog: DialogInterface, which: Int) {
        f.apply()
      }
    }
  }

  implicit def func2OnClickListener(f: (DialogInterface, Int) => Unit): OnClickListener = {
    new OnClickListener {
      def onClick(dialog: DialogInterface, which: Int) {
        f.apply(dialog, which)
      }
    }
  }
}