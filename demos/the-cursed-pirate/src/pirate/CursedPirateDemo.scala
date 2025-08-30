package pirate

import indigo.*
import indigo.scenes.*
import pirate.scenes.loading.LoadingScene
import pirate.scenes.level.LevelScene
import pirate.core.{Model, ViewModel}
import pirate.core.BootInformation
import pirate.core.LayerKeys
import pirate.generated.Config

import pirate.core.{Assets, InitialLoad, StartupData}

import scala.scalajs.js.annotation.JSExportTopLevel
import indigoextras.subsystems.FPSCounter

@JSExportTopLevel("IndigoGame")
object CursedPirateDemo extends IndigoGame[BootInformation, StartupData, Model, ViewModel]:

  def initialScene(bootInfo: BootInformation): Option[SceneName] =
    None

  def scenes(bootInfo: BootInformation): NonEmptyBatch[Scene[StartupData, Model, ViewModel]] =
    NonEmptyBatch(
      LoadingScene(bootInfo.assetPath, bootInfo.screenDimensions),
      LevelScene(bootInfo.screenDimensions.width)
    )

  val eventFilters: EventFilters =
    EventFilters.BlockAll

  def boot(flags: Map[String, String]): Outcome[BootResult[BootInformation, Model]] =
    Outcome {
      val assetPath: String =
        flags.getOrElse("baseUrl", "")

      val config =
        Config.config
          .withMagnification(2)
          .noResize

      BootResult(
        config,
        BootInformation(assetPath, config.screenDimensions)
      ).withAssets(Assets.initialAssets(assetPath))
        .withFonts(Assets.Fonts.fontInfo)
        .withSubSystems(
          FPSCounter[Model](
            Assets.Fonts.fontKey,
            pirate.generated.Assets.assets.fonts.boxyFontSmall
          ).withLayerKey(LayerKeys.fps)
        )
    }

  def setup(
      bootInfo: BootInformation,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[StartupData]] =
    InitialLoad.setup(bootInfo.screenDimensions, assetCollection)

  def initialModel(startupData: StartupData): Outcome[Model] =
    Outcome(Model.initial)

  def initialViewModel(startupData: StartupData, model: Model): Outcome[ViewModel] =
    Outcome(ViewModel.initial)

  def updateModel(context: Context[StartupData], model: Model): GlobalEvent => Outcome[Model] =
    _ => Outcome(model)

  def updateViewModel(
      context: Context[StartupData],
      model: Model,
      viewModel: ViewModel
  ): GlobalEvent => Outcome[ViewModel] =
    _ => Outcome(viewModel)

  def present(
      context: Context[StartupData],
      model: Model,
      viewModel: ViewModel
  ): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment.empty
        .withLayers(
          LayerKeys.background  -> Layer.empty,
          LayerKeys.bigClouds   -> Layer.empty,
          LayerKeys.smallClouds -> Layer.empty,
          LayerKeys.game        -> Layer.empty,
          LayerKeys.fps         -> Layer.empty
        )
    )
