package snake.model

import indigo.*
import snake.model.snakemodel.{CollisionCheckOutcome, Snake}
import snake.scenes.GameView
import snake.Score
import indigo.scenes.SceneEvent
import snake.scenes.GameOverScene
import snake.generated.Assets

final case class GameModel(
    snake: Snake,
    gameState: GameState,
    gameMap: GameMap,
    score: Int,
    tickDelay: Seconds,
    controlScheme: ControlScheme,
    lastUpdated: Seconds
):

  def update(gameTime: GameTime, dice: Dice, gridSquareSize: Int): GlobalEvent => Outcome[GameModel] =
    case FrameTick if gameTime.running < lastUpdated + tickDelay =>
      Outcome(this)

    case FrameTick =>
      gameState match {
        case s @ GameState.Running(_, _) =>
          GameModel.updateRunning(
            gameTime,
            dice,
            this.copy(lastUpdated = gameTime.running),
            s,
            gridSquareSize
          )(FrameTick)

        case s @ GameState.Crashed(_, _, _, _) =>
          GameModel.updateCrashed(
            gameTime,
            this.copy(lastUpdated = gameTime.running),
            s
          )(FrameTick)
      }

    case e =>
      gameState match {
        case s @ GameState.Running(_, _) =>
          GameModel.updateRunning(gameTime, dice, this, s, gridSquareSize)(e)

        case s @ GameState.Crashed(_, _, _, _) =>
          GameModel.updateCrashed(gameTime, this, s)(e)
      }

object GameModel:

  val ScoreIncrement: Int = 100

  def initialModel(gridSize: Size, controlScheme: ControlScheme): GameModel =
    val center = gridSize / 2

    GameModel(
      snake = Snake(
        center.width,
        center.height - (center.height / 2).toInt
      ).grow.grow,
      gameState = GameState.start,
      gameMap = GameMap.genLevel(gridSize),
      score = 0,
      tickDelay = Seconds(0.1),
      controlScheme = controlScheme,
      lastUpdated = Seconds.zero
    )

  def updateRunning(
      gameTime: GameTime,
      dice: Dice,
      state: GameModel,
      runningDetails: GameState.Running,
      gridSquareSize: Int
  ): GlobalEvent => Outcome[GameModel] =
    case FrameTick =>
      val (updatedModel, collisionResult) =
        state.snake.update(state.gameMap.grid.size, hitTest(state.gameMap, state.snake.givePath)) match {
          case (s, outcome) =>
            (state.copy(snake = s, gameState = state.gameState.updateNow(gameTime.running, state.snake.direction)), outcome)
        }

      updateBasedOnCollision(gameTime, dice, gridSquareSize, updatedModel, collisionResult)

    case e: KeyboardEvent =>
      Outcome(
        state.copy(
          snake = state.controlScheme.instructSnake(e, state.snake, runningDetails.lastSnakeDirection)
        )
      )

    case _ =>
      Outcome(state)

  def hitTest(gameMap: GameMap, body: Batch[Point]): Point => CollisionCheckOutcome =
    given CanEqual[Option[MapElement], Option[MapElement]] = CanEqual.derived
    pt =>
      if (body.contains(pt)) CollisionCheckOutcome.Crashed(pt)
      else
        gameMap.fetchElementAt(pt) match {
          case Some(MapElement.Apple(_)) =>
            CollisionCheckOutcome.PickUp(pt)

          case Some(MapElement.Wall(_)) =>
            CollisionCheckOutcome.Crashed(pt)

          case None =>
            CollisionCheckOutcome.NoCollision(pt)
        }

  def updateBasedOnCollision(
      gameTime: GameTime,
      dice: Dice,
      gridSquareSize: Int,
      gameModel: GameModel,
      collisionResult: CollisionCheckOutcome
  ): Outcome[GameModel] =
    collisionResult match
      case CollisionCheckOutcome.Crashed(_) =>
        Outcome(
          gameModel.copy(
            gameState = gameModel.gameState match {
              case c @ GameState.Crashed(_, _, _, _) =>
                c

              case r @ GameState.Running(_, _) =>
                r.crash(gameTime.running, gameModel.snake.length)
            },
            tickDelay = gameModel.snake.length match {
              case l if l < 5  => Seconds(0.1)
              case l if l < 10 => Seconds(0.05)
              case l if l < 25 => Seconds(0.025)
              case _           => Seconds(0.015)
            }
          )
        ).addGlobalEvents(Assets.assets.losePlay)

      case CollisionCheckOutcome.PickUp(pt) =>
        Outcome(
          gameModel.copy(
            snake = gameModel.snake.grow,
            gameMap = gameModel.gameMap
              .removeApple(pt)
              .insertApple(
                MapElement.Apple(
                  gameModel.gameMap
                    .findEmptySpace(dice, pt :: gameModel.snake.givePath)
                )
              ),
            score = gameModel.score + ScoreIncrement
          )
        ).addGlobalEvents(
          Assets.assets.pointPlay,
          Score.spawnEvent(GameView.gridPointToPoint(pt, gameModel.gameMap.grid.size, gridSquareSize))
        )

      case CollisionCheckOutcome.NoCollision(_) =>
        Outcome(gameModel)

  def updateCrashed(
      gameTime: GameTime,
      state: GameModel,
      crashDetails: GameState.Crashed
  ): GlobalEvent => Outcome[GameModel] =
    case FrameTick if gameTime.running <= crashDetails.crashedAt + Seconds(0.75) =>
      Outcome(state)

    case FrameTick if state.snake.length > 1 =>
      Outcome(
        state.copy(
          snake = state.snake.shrink,
          gameState = state.gameState.updateNow(gameTime.running, state.gameState.lastSnakeDirection)
        )
      )

    case FrameTick if state.snake.length == 1 =>
      Outcome(state)
        .addGlobalEvents(SceneEvent.JumpTo(GameOverScene.name))

    case _ =>
      Outcome(state)
