package indigoexamples

import indigo.*
import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*

@JSExportTopLevel("IndigoGame")
object GroupExample extends IndigoSandbox[Unit, Unit]:

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

  /** # How to use a group
    *
    * In this example we arrange three circle relative to each other, and then move the group the
    * middle of the screen.
    */
  // ```scala
  def present(context: Context[Unit], model: Unit): Outcome[SceneUpdateFragment] =
    val viewportCenter =
      context.frame.viewport.giveDimensions(context.frame.globalMagnification).center

    val radius = 50

    Outcome(
      SceneUpdateFragment(
        Group(
          Shape
            .Circle(
              Circle(Point.zero, radius),
              Fill.Color(RGBA.Red.withAlpha(0.75)),
              Stroke(2, RGBA.White)
            )
            .moveTo(Point(0, -25)),
          Shape
            .Circle(
              Circle(Point.zero, radius),
              Fill.Color(RGBA.Green.withAlpha(0.75)),
              Stroke(2, RGBA.White)
            )
            .moveTo(Point(25, 25)),
          Shape
            .Circle(
              Circle(Point.zero, radius),
              Fill.Color(RGBA.Blue.withAlpha(0.75)),
              Stroke(2, RGBA.White)
            )
            .moveTo(Point(-25, 25))
        ).moveTo(viewportCenter)
          .withRef(25, 25)
      )
    )
  // ```
