package indigoexamples

import indigo.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("IndigoGame")
object Runtime extends BasicGameRuntime:

  def game: Game[?, ?, ?] =
    SnakeIn5Minutes()

  def settings: Settings =
    Settings.default
      .targetFrameRate(FPS(30))
