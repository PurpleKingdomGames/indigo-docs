package snake.scenes

import indigo.*
import indigo.scenes.*

import snake.model.GameModel
import snake.Score
import snake.init.{GameAssets, StartupData}

object GameScene extends Scene[StartupData, GameModel]:
  type SceneModel     = GameModel

  val name: SceneName =
    SceneName("game scene")

  val modelLens: Lens[GameModel, GameModel] =
    Lens.keepLatest

  val eventFilters: EventFilters =
    EventFilters.Restricted

  val subSystems: Set[SubSystem[GameModel]] =
    Set(Score.automataSubSystem(GameModel.ScoreIncrement.toString(), GameAssets.fontKey))

  def updateModel(context: SceneContext, gameModel: GameModel): GlobalEvent => Outcome[GameModel] =
    gameModel.update(context.frame.time, context.frame.dice, gameModel.startupData.viewConfig.gridSquareSize)

  def present(
      context: SceneContext,
      gameModel: GameModel,
  ): Outcome[SceneUpdateFragment] =
    GameView.update(
      gameModel.startupData.viewConfig,
      gameModel, 
      gameModel.viewModel.walls, 
      gameModel.startupData.staticAssets
    )
