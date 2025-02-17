package indigoexamples

import indigo.*
import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*

@JSExportTopLevel("IndigoGame")
object FillTypeExample extends IndigoSandbox[Unit, Unit]:

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

  /** In this example, we set up four graphics with a bitmap material. The graphics are 128x128 in
    * size, but the texture is only 64x64. Each one then has a different fill type applied to its
    * material: normal (default), tile, stretch, and nineSlice.
    *
    * Just so that we can see the boundaries of each graphic, a rectangle with a green border has
    * been drawn around each one.
    */
  // ``` scala
  val material = Material.Bitmap(Assets.assets.nineslice)

  def present(context: Context[Unit], model: Unit): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        Graphic(0, 0, 128, 128, material.normal).moveTo(0, 0),
        Graphic(0, 0, 128, 128, material.tile).moveTo(128, 0),
        Graphic(0, 0, 128, 128, material.stretch).moveTo(0, 128),
        Graphic(0, 0, 128, 128, material.nineSlice(16, 16, 32, 32)).moveTo(128, 128),
        Shape.Box(Rectangle(128, 0, 128, 128), Fill.None, Stroke(1, RGBA.Green)),
        Shape.Box(Rectangle(0, 128, 128, 128), Fill.None, Stroke(1, RGBA.Green)),
        Shape.Box(Rectangle(0, 0, 128, 128), Fill.None, Stroke(1, RGBA.Green)),
        Shape.Box(Rectangle(128, 128, 128, 128), Fill.None, Stroke(1, RGBA.Green))
      )
    )
  // ```
