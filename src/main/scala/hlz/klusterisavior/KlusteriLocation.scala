package hlz.klusterisavior

import android.location.Location
import android.hardware.GeomagneticField
import compat.Platform

object KlusteriLocation {
  lazy val klusteriLocation = {
    val l = new Location("static")
    l.setLatitude(60.16944)
    l.setLongitude(24.92162)
    l
  }

  def bearingToKlusteri(azimuth: Double, currentLocation: Location) = {
    def magneticNorthToTrueNorth(azimuth: Double) = {
      val geoField = new GeomagneticField(
        currentLocation.getLatitude.toFloat,
        currentLocation.getLongitude.toFloat,
        currentLocation.getAltitude.toFloat,
        Platform.currentTime
      )
      azimuth + geoField.getDeclination
    }
    val trueAzimuth = magneticNorthToTrueNorth(azimuth)
    trueAzimuth - currentLocation.bearingTo(klusteriLocation)
  }

}
