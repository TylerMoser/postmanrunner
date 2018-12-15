package me.tylermoser.postmanrunner.style

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * The main TornadoFX [Stylesheet] for this application
 */
class AppStylesheet: Stylesheet() {

    private val passColor: Paint = Color.LIGHTGREEN
    private val failColor: Paint = Color.RED

    companion object {
        val innerBackgroundColor by cssproperty<MultiValue<Paint>>("-fx-control-inner-background")
        val textBackgroundColor by cssproperty<MultiValue<Paint>>("-fx-text-background-color")
        val postmanTestPass by cssclass()
        val postmanTestFail by cssclass()
    }

    init {
        postmanTestPass {
            innerBackgroundColor.value += passColor
            and(selected) {
                borderColor += CssBox(passColor, passColor, passColor, passColor)
                textBackgroundColor.value += passColor
                fontWeight = FontWeight.EXTRA_BOLD
            }
        }
        postmanTestFail {
            innerBackgroundColor.value += failColor
            and(selected) {
                borderColor += CssBox(failColor, failColor, failColor, failColor)
                textBackgroundColor.value += failColor
                fontWeight = FontWeight.EXTRA_BOLD
            }
        }
    }

}
