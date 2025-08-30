package preloader

import indigo.*
import indigo.scenes.*
import preloader.scenes.LoadingScene
import preloader.scenes.LevelScene
import preloader.core.{Model, ViewModel}
import preloader.core.BootInformation
import preloader.generated.Config
import preloader.core.{Assets, InitialLoad, StartupData}

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("IndigoGame")
object PreloaderExample extends IndigoGame[BootInformation, StartupData, Model, ViewModel]:

  def initialScene(bootInfo: BootInformation): Option[SceneName] =
    None

  def scenes(bootInfo: BootInformation): NonEmptyBatch[Scene[StartupData, Model, ViewModel]] =
    NonEmptyBatch(
      LoadingScene,
      LevelScene
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
        BootInformation(assetPath)
      ).withAssets(Assets.initialAssets(assetPath))
        .withFonts(Assets.Fonts.fontInfo)
    }

  def setup(
      bootInfo: BootInformation,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[StartupData]] =
    InitialLoad.setup(bootInfo.assetPath)

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
    )
