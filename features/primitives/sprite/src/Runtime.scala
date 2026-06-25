package indigoexamples

import indigo.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("IndigoGame")
object Runtime extends BasicGameRuntime:

  def game: Game[?, ?, ?] =
    SpriteExample()

  def settings: Settings =
    Settings.default
