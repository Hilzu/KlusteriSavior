package hlz.klusterisavior

import android.location.Location
import android.hardware.GeomagneticField

object MyLocation {
  val klusteri = {
    val l = new Location("static")
    l.setLatitude(60.169439353910285)
    l.setLongitude(24.921391010284424)
    l
  }

  private val approximateCurrentTime = System.currentTimeMillis()

  def directionToKlusteri(azimuth: Double, currentLocation: Location) = {
    lazy val declination = {
      val geoField = new GeomagneticField(
        currentLocation.getLatitude.toFloat,
        currentLocation.getLongitude.toFloat,
        currentLocation.getAltitude.toFloat,
        approximateCurrentTime
      )
      geoField.getDeclination
    }
    (azimuth + declination) - (currentLocation bearingTo klusteri)
  }
}