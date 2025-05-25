package indigoexamples

import indigo.*
import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*

@JSExportTopLevel("IndigoGame")
object ShapePolygonExample extends IndigoSandbox[Unit, Unit]:

  val config: GameConfig =
    Config.config.noResize

  val assets: Set[AssetType] =
    Assets.assets.assetSet

  val fonts: Set[FontInfo]        = Set()
  val animations: Set[Animation]  = Set()
  val shaders: Set[ShaderProgram] = Set()

  def setup(assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Unit] =
    Outcome(())

  def updateModel(context: Context[Unit], model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  def present(context: Context[Unit], model: Unit): Outcome[SceneUpdateFragment] =
    val viewportCenter =
      context.frame.viewport.giveDimensions(context.frame.globalMagnification).center

    Outcome(
      SceneUpdateFragment(
        Shape
          .Polygon(
            Batch(Point(0, 0), Point(64, 32), Point(64, 64), Point(0, 32)),
            Fill.LinearGradient(Point.zero, RGBA.Cyan, Point(64), RGBA.Magenta),
            Stroke(2, RGBA.White)
          )
          .moveTo(viewportCenter - Point(32))
      )
    )
