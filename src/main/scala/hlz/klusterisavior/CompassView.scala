package hlz.klusterisavior

import android.view.View
import android.content.Context
import android.graphics._
import android.util.AttributeSet

class CompassView(c: Context, attrs: AttributeSet, style: Int) extends View(c, attrs, style) {
  def this(c: Context, attrs: AttributeSet) = this(c, attrs, 0)

  private var direction = 0.0

  private lazy val paint: Paint = {
    val p = new Paint(Paint.ANTI_ALIAS_FLAG)
    p.setStyle(Paint.Style.STROKE)
    p.setStrokeWidth(3)
    p.setColor(Color.WHITE)
    p.setTextSize(30)
    p
  }

  protected override def onDraw(canvas: Canvas) {
    val cxCompass = getWidth / 2
    val cyCompass = getHeight / 2
    val radiusCompass =
      if (cxCompass > cyCompass) cyCompass * 0.9f
      else cxCompass * 0.9f

    canvas.drawCircle(cxCompass, cyCompass, radiusCompass, paint)
    canvas.drawRect(0, 0, getWidth, getHeight, paint)
    canvas.drawLine(
      cxCompass,
      cyCompass,
      (cxCompass + radiusCompass * math.sin(-direction * math.Pi / 180)).toFloat,
      (cyCompass - radiusCompass * math.cos(-direction * math.Pi / 180)).toFloat,
      paint)
    canvas.drawText(String.valueOf(direction), cxCompass, cyCompass, paint)
  }

  def setDirection(dir: Double) {
    direction = dir
    invalidate()
  }
}
