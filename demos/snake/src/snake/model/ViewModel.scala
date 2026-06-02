package snake.model

import indigo.*
import snake.scenes.GameView
import snake.init.StaticAssets

final case class ViewModel(walls: Group)
object ViewModel:
  def initial(gridSize: Size, gridSquareSize: Int, staticAssets: StaticAssets, gameMap: GameMap): ViewModel =
    ViewModel(
      walls = Group(
        gameMap.findWalls.map { wall =>
          staticAssets.wall
            .moveTo(
              GameView.gridPointToPoint(wall.gridPoint, gridSize, gridSquareSize)
            )
        }
      )
    )
