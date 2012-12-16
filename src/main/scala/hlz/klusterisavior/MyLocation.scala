package hlz.klusterisavior

import android.location.Location

object MyLocation {
  val klusteri = {
    val l = new Location("static")
    l.setLatitude(60.169439353910285)
    l.setLongitude(24.921391010284424)
    l
  }

  def directionToKlusteri(azimuth: Double, currentLocation: Location) = {
    (azimuth) - (currentLocation bearingTo klusteri)
  }
}
