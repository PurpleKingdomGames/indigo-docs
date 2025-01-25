package indigoexamples

import indigo.*
import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*

@JSExportTopLevel("IndigoGame")
object GraphicExample extends IndigoSandbox[Unit, Unit]:

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

  /** Below, we present a simple graphic using the bitmap material.
    */
  // ``` scala
  def present(context: Context[Unit], model: Unit): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        Graphic(0, 0, 64, 64, 1, Material.Bitmap(Assets.assets.nineslice))
          .moveTo(10, 10)
      )
    )
  // ```
