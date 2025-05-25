package indigoexamples

import indigo.*
import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*

@JSExportTopLevel("IndigoGame")
object ShapeCircleExample extends IndigoSandbox[Unit, Unit]:

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

    val radius = 50

    Outcome(
      SceneUpdateFragment(
        Shape
          .Circle(
            Circle(Point.zero, radius),
            Fill.LinearGradient(Point.zero, RGBA.Cyan, Point(radius * 2), RGBA.Magenta),
            Stroke(2, RGBA.White)
          )
          .moveTo(viewportCenter)
      )
    )
