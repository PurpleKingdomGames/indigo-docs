package preloader

import indigo.*
import preloader.scenes.LoadingScene
import preloader.scenes.LevelScene
import preloader.core.Model
import preloader.core.BootInformation
import preloader.generated.Config
import preloader.core.{Assets, InitialLoad, StartupData}

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("IndigoGame")
object PreloaderExample extends Game[BootInformation, StartupData, Model]:

  def gameId: GameId = GameId("PreloaderExample")

  def initialScene(bootInfo: BootInformation): Option[SceneName] =
    None

  def scenes(bootInfo: BootInformation): NonEmptyBatch[Scene[StartupData, Model]] =
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
    Outcome(Model.initial(startupData))

  def updateModel(context: Context, model: Model): GlobalEvent => Outcome[Model] =
    _ => Outcome(model)

  def present(
      context: Context,
      model: Model
  ): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment.empty
    )
