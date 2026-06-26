package indigoexamples

import indigo.*
import tyrian.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("IndigoGame")
object Runtime extends BasicGameRuntime[Unit]:

  def game: Game[?, ?, ?] =
    SnakeIn5Minutes()

  def settings: Settings =
    Settings.default
      .targetFrameRate(FPS(30))

  def init(flags: Map[String, String]): Result[Unit] =
    Result(())

  def update(model: Unit): GlobalMsg => Result[Unit] =
    case _ => Result(model)

  def eventMapping: PartialIso[GlobalMsg, GlobalEvent] =
    PartialIso.none
