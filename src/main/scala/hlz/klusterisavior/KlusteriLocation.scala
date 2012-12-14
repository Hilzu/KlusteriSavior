package hlz.klusterisavior

import android.location.Location
import android.hardware.GeomagneticField

object KlusteriLocation {
  val klusteriLocation = {
    val l = new Location("static")
    l.setLatitude(60.169439353910285)
    l.setLongitude(24.921391010284424)
    l
  }

  private val approximateCurrentTime = System.currentTimeMillis()

  def magneticNorthToTrueNorth(location: Location, azimuth: Double) = {
    val geoField = new GeomagneticField(
      location.getLatitude.toFloat,
      location.getLongitude.toFloat,
      location.getAltitude.toFloat,
      approximateCurrentTime
    )
    azimuth + geoField.getDeclination
  }

  def bearingToKlusteri(azimuth: Double, currentLocation: Location) = {
    val trueAzimuth = magneticNorthToTrueNorth(currentLocation, azimuth)
    trueAzimuth - currentLocation.bearingTo(klusteriLocation)
  }
}
