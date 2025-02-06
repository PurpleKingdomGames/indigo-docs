package indigoexamples

import indigo.*
import generated.Config
import generated.Assets
import generated.KiwiSodaFont

import scala.scalajs.js.annotation.*

@JSExportTopLevel("IndigoGame")
object TextExample extends IndigoSandbox[Unit, Unit]:

  val config: GameConfig =
    Config.config.noResize
      .withMagnification(3)

  val assets: Set[AssetType] =
    Assets.assets.assetSet ++ Assets.assets.generated.assetSet

  val fonts: Set[FontInfo]        = Set(KiwiSodaFont.fontInfo)
  val animations: Set[Animation]  = Set()
  val shaders: Set[ShaderProgram] = Set()

  def setup(assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Unit] =
    Outcome(())

  def updateModel(context: Context[Unit], model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  def present(context: Context[Unit], model: Unit): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        Text(
          "Hello, Indigo!",
          KiwiSodaFont.fontKey,
          Assets.assets.generated.KiwiSodaFontMaterial.toImageEffects
            .withOverlay(Fill.Color(RGBA.Magenta))
        ).moveTo(10, 10)
      )
    )
