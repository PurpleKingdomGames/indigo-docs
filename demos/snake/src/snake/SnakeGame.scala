package snake

import indigo.*
import indigo.scenes.*
import indigoextras.subsystems.FPSCounter

import snake.generated.Assets

import snake.model.{ControlScheme, GameModel, ViewModel}
import snake.init.{GameAssets, StartupData, ViewConfig}
import snake.scenes.{ControlsScene, GameOverScene, GameScene, StartScene}
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("IndigoGame")
object SnakeGame extends IndigoGame[ViewConfig, StartupData, GameModel, ViewModel]:

  def initialScene(bootData: ViewConfig): Option[SceneName] =
    Option(StartScene.name)

  def scenes(bootData: ViewConfig): NonEmptyList[Scene[StartupData, GameModel, ViewModel]] =
    NonEmptyList(StartScene, ControlsScene, GameScene, GameOverScene)

  val eventFilters: EventFilters =
    EventFilters.Restricted

  def boot(flags: Map[String, String]): Outcome[BootResult[ViewConfig, GameModel]] =
    Outcome {
      val viewConfig: ViewConfig =
        ViewConfig.default

      val assetPath: String =
        flags.getOrElse("baseUrl", "")

      val config =
        snake.generated.SnakeConfig.config
          .withMagnification(viewConfig.magnificationLevel)
          .withViewport(viewConfig.viewport)
          .noResize

      BootResult(config, viewConfig)
        .withAssets(GameAssets.assets(assetPath))
        .withFonts(GameAssets.fontInfo)
        .withSubSystems(
          FPSCounter(GameAssets.fontKey, Assets.assets.boxyFontSmall)
            .withLayerKey(LayerKey("fps"))
            .moveTo(Point(5, 5))
        )
    }

  def initialModel(startupData: StartupData): Outcome[GameModel] =
    Outcome(GameModel.initialModel(startupData.viewConfig.gridSize, ControlScheme.directedKeys))

  def initialViewModel(startupData: StartupData, model: GameModel): Outcome[ViewModel] =
    Outcome(ViewModel.initialViewModel(startupData, model))

  def setup(
      viewConfig: ViewConfig,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[StartupData]] =
    StartupData.initialise(viewConfig)

  def updateModel(
      context: Context[StartupData],
      model: GameModel
  ): GlobalEvent => Outcome[GameModel] = {
    case GameReset =>
      Outcome(GameModel.initialModel(context.startUpData.viewConfig.gridSize, model.controlScheme))

    case _ =>
      Outcome(model)
  }

  def updateViewModel(
      context: Context[StartupData],
      model: GameModel,
      viewModel: ViewModel
  ): GlobalEvent => Outcome[ViewModel] =
    _ => Outcome(viewModel)

  def present(
      context: Context[StartupData],
      model: GameModel,
      viewModel: ViewModel
  ): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        LayerKey("game")  -> Layer.empty,
        LayerKey("score") -> Layer.empty,
        LayerKey("ui")    -> Layer.empty,
        LayerKey("fps")   -> Layer.empty
      )
    )

case object GameReset extends GlobalEvent
