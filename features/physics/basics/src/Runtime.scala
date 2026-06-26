package indigoexamples

import indigo.*
import tyrian.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("IndigoGame")
object Runtime extends BasicGameRuntime[Unit]:

  def game: Game[?, ?, ?] =
    BasicPhysicsExample()

  def settings: Settings =
    Settings.default

  def init(flags: Map[String, String]): Result[Unit] =
    Result(())

  def update(model: Unit): GlobalMsg => Result[Unit] =
    case _ => Result(model)

  def eventMapping: PartialIso[GlobalMsg, GlobalEvent] =
    PartialIso.none
