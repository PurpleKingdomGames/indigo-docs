package indigoexamples

import indigo.*
import generated.Config
import generated.Assets
import generated.KiwiSodaFont

import scala.scalajs.js.annotation.*

@JSExportTopLevel("IndigoGame")
object TextExample extends Game[Unit, Unit, Unit]:

  def gameId: GameId = GameId("TextExample")

  def boot(flags: Map[String, String]): Outcome[BootResult[Unit, Unit]] =
    Outcome(
      BootResult(Config.config, ())
        .withAssets(Assets.assets.assetSetRelative ++ Assets.assets.generated.assetSetRelative)
        .withFonts(KiwiSodaFont.fontInfo)
    )

  def initialScene(bootData: Unit): Option[SceneName] = None

  def scenes(bootData: Unit): NonEmptyBatch[Scene[Unit, Unit]] =
    NonEmptyBatch(Scene.empty)

  def eventFilters: EventFilters = EventFilters.Permissive

  def setup(bootData: Unit, assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Unit] =
    Outcome(())

  def updateModel(context: Context, model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  def present(context: Context, model: Unit): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        Text(
          "Hello, Indigo!",
          KiwiSodaFont.fontKey,
          Assets.assets.generated.KiwiSodaFontMaterial.toImageEffects
            .withOverlay(Fill.Color(RGBA.Magenta))
        ).moveTo(10, 10)
      ).withMagnification(3)
    )
