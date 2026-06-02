package snake

import indigo.*
import indigoextras.subsystems.FPSCounter

import snake.generated.Assets

import snake.model.{ControlScheme, GameModel}
import snake.init.{GameAssets, StartupData, ViewConfig}
import snake.scenes.{ControlsScene, GameOverScene, GameScene, StartScene}

final class SnakeGame() extends Game[ViewConfig, StartupData, GameModel]:

  val gameId: GameId = GameId("snake")

  def initialScene(bootData: ViewConfig): Option[SceneName] =
    Option(StartScene.name)

  def scenes(bootData: ViewConfig): NonEmptyBatch[Scene[StartupData, GameModel]] =
    NonEmptyBatch(StartScene, ControlsScene, GameScene, GameOverScene)

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
    Outcome(GameModel.initialModel(startupData, ControlScheme.directedKeys))

  def setup(
      viewConfig: ViewConfig,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[StartupData]] =
    StartupData.initialise(viewConfig)

  def updateModel(
      context: Context,
      model: GameModel
  ): GlobalEvent => Outcome[GameModel] = {
    case GameReset =>
      Outcome(model.reset())

    case _ =>
      Outcome(model)
  }

  def present(
      context: Context,
      model: GameModel
  ): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        LayerKey("game")  -> Layer.empty.withMagnificationForAll(2),
        LayerKey("score") -> Layer.empty.withMagnificationForAll(2),
        LayerKey("ui")    -> Layer.empty.withMagnificationForAll(2),
        LayerKey("fps")   -> Layer.empty.withMagnificationForAll(2)
      )
    )

case object GameReset extends GlobalEvent
