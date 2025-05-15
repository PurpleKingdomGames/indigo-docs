package snake.scenes

import indigo.*
import snake.init.{GameAssets, StaticAssets, ViewConfig}
import snake.model.GameMap
import snake.model.GameModel

object GameView:

  def update(
      viewConfig: ViewConfig,
      model: GameModel,
      walls: Group,
      staticAssets: StaticAssets
  ): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment.empty
        .addLayer(
          LayerKey("game") -> Layer(
            gameLayer(
              viewConfig,
              model,
              staticAssets,
              walls
            )
          )
        )
    )

  def gameLayer(
      viewConfig: ViewConfig,
      currentState: GameModel,
      staticAssets: StaticAssets,
      walls: Group
  ): Batch[SceneNode] =
    walls ::
      drawApple(viewConfig, currentState.gameMap, staticAssets) ++
      drawSnake(viewConfig, currentState, staticAssets.snake) ++
      drawScore(viewConfig, currentState.score)

  def drawApple(viewConfig: ViewConfig, gameMap: GameMap, staticAssets: StaticAssets): Batch[Graphic[?]] =
    gameMap.findApples.map { a =>
      staticAssets.apple
        .moveTo(gridPointToPoint(a.gridPoint, gameMap.grid.size, viewConfig.gridSquareSize))
    }

  def drawSnake(viewConfig: ViewConfig, currentState: GameModel, snakeAsset: Graphic[?]): Batch[Graphic[?]] =
    currentState.snake.givePath.map { pt =>
      snakeAsset.moveTo(gridPointToPoint(pt, currentState.gameMap.grid.size, viewConfig.gridSquareSize))
    }

  def drawScore(viewConfig: ViewConfig, score: Int): Batch[SceneNode] =
    Batch(
      Text(
        score.toString,
        (viewConfig.viewport.width / viewConfig.magnificationLevel) - 3,
        (viewConfig.viewport.height / viewConfig.magnificationLevel) - viewConfig.footerHeight + 21,
        GameAssets.fontKey,
        GameAssets.fontMaterial
      ).alignRight
    )

  def gridPointToPoint(gridPoint: Point, gridSize: Size, gridSquareSize: Int): Point =
    Point((gridPoint.x * gridSquareSize).toInt, (((gridSize.height - 1) - gridPoint.y) * gridSquareSize).toInt)
