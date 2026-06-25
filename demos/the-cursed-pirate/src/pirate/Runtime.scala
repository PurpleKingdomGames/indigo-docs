package pirate

import indigo.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("IndigoGame")
object Runtime extends BasicGameRuntime:

  def game: Game[?, ?, ?] =
    CursedPirateDemo()

  def settings: Settings =
    Settings.default
