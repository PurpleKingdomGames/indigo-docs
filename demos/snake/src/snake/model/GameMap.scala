package snake.model

import indigo.*
import scala.annotation.tailrec
import indigoextras.datatypes.SparseGrid

final case class GameMap(grid: SparseGrid[MapElement]):

  def fetchElementAt(gridPoint: Point): Option[MapElement] =
    grid.get(gridPoint).headOption

  def insertApple(element: MapElement.Apple): GameMap =
    this.copy(grid = grid.put(element.gridPoint, element))

  def removeApple(gridPoint: Point): GameMap =
    this.copy(grid = grid.remove(gridPoint))

  def insertElements(elements: Batch[MapElement]): GameMap =
    this.copy(
      grid = grid.put(elements.map(me => (me.giveGridPoint, me)))
    )

  def findEmptySpace(dice: Dice, not: Batch[Point]): Point =
    GameMap.findEmptySpace(grid, dice, not)

  def asElementBatch: Batch[MapElement] =
    grid.toBatch.collect { case Some(s) => s }

  lazy val findWalls: Batch[MapElement.Wall] =
    asElementBatch.flatMap {
      case w: MapElement.Wall =>
        Batch(w)

      case _ =>
        Batch.empty
    }

  def findApples: Batch[MapElement.Apple] =
    asElementBatch.flatMap {
      case a: MapElement.Apple =>
        Batch(a)

      case _ =>
        Batch.empty
    }

object GameMap:

  def findEmptySpace(grid: SparseGrid[MapElement], dice: Dice, not: Batch[Point]): Point =
    val gridSize: Size =
      grid.size

    def randomPosition(dice: Dice, maxX: Int, maxY: Int): Point =
      Point(
        x = dice.rollFromZero(maxX),
        y = dice.rollFromZero(maxY)
      )

    def makeRandom: () => Point =
      () =>
        randomPosition(dice, (gridSize.width - 2).toInt, (gridSize.height - 2).toInt) + Point(1, 1)

    @tailrec
    def rec(next: Point): Point =
      grid.get(next) match {
        case None if !not.contains(next) =>
          next

        case None =>
          rec(makeRandom())

        case Some(_) =>
          rec(makeRandom())
      }

    rec(makeRandom())

  def genLevel(gridSize: Size): GameMap =
    val adjustedGridSize = gridSize - Size(1)

    GameMap(SparseGrid(gridSize))
      .insertApple(MapElement.Apple((adjustedGridSize.toPoint / 2) + Point(3, 2)))
      .insertElements(topEdgeWall(adjustedGridSize))
      .insertElements(rightEdgeWall(adjustedGridSize))
      .insertElements(bottomEdgeWall(adjustedGridSize))
      .insertElements(leftEdgeWall(adjustedGridSize))

  private def topEdgeWall(gridSize: Size): Batch[MapElement.Wall] =
    fillIncrementally(Point.zero, Point(gridSize.width, 0)).map(MapElement.Wall.apply)

  private def rightEdgeWall(gridSize: Size): Batch[MapElement.Wall] =
    fillIncrementally(Point(gridSize.width, 0), gridSize.toPoint).map(MapElement.Wall.apply)

  private def bottomEdgeWall(gridSize: Size): Batch[MapElement.Wall] =
    fillIncrementally(Point(0, gridSize.height), gridSize.toPoint).map(MapElement.Wall.apply)

  private def leftEdgeWall(gridSize: Size): Batch[MapElement.Wall] =
    fillIncrementally(Point.zero, Point(0, gridSize.height)).map(MapElement.Wall.apply)

  private def fillIncrementally(start: Point, end: Point): Batch[Point] =
    @tailrec
    def rec(last: Point, dest: Point, p: Point => Boolean, acc: Batch[Point]): Batch[Point] =
      if (p(last)) acc
      else {
        val nextX: Int  = if (last.x + 1 <= end.x) last.x + 1 else last.x
        val nextY: Int  = if (last.y + 1 <= end.y) last.y + 1 else last.y
        val next: Point = Point(nextX, nextY)
        rec(next, dest, p, acc :+ next)
      }

    if (lessThanOrEqual(start, end)) rec(start, end, (gp: Point) => gp == end, Batch(start))
    else rec(end, start, (gp: Point) => gp == start, Batch(end))

  private def lessThanOrEqual(a: Point, b: Point): Boolean =
    a.x <= b.x && a.y <= b.y

enum MapElement derives CanEqual:
  case Wall(gridPoint: Point)  extends MapElement
  case Apple(gridPoint: Point) extends MapElement
object MapElement:
  extension (me: MapElement)
    def giveGridPoint: Point =
      me match
        case Wall(gp)  => gp
        case Apple(gp) => gp
