package indigoexamples

import indigo.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("IndigoGame")
object Runtime extends BasicGameRuntime:

  def game: Game[?, ?, ?] =
    BasicShaderExample()

  def settings: Settings =
    Settings.default
