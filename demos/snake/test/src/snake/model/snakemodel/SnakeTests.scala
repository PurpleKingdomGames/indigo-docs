package snake.model.snakemodel

import indigo.*

class SnakeTests extends munit.FunSuite {

  val gridSize: Size = Size(10)

  def collisionF: Point => CollisionCheckOutcome = pt => CollisionCheckOutcome.NoCollision(pt)

  def tick(snake: Snake, count: Int): Snake =
    if (count == 0) snake
    else tick(snake.update(gridSize, collisionF)._1, count - 1)

  implicit class Tickable(snake: Snake) {
    def doTick(): Snake = tick(snake, 1)
  }

  test("Moving and turning.should advance forward on each tick") {

    val s = tick(Snake(Point.zero), 1)

    assertEquals(s.length, 1)
    assertEquals(s.start, Point(0, 1))

  }

  test("Moving and turning.Turning.should be able to turn left") {

    val s = Snake(Point(1, 1)).turnLeft

    assertEquals(s.direction, SnakeDirection.Left)

    val s2 = tick(s, 1)

    assertEquals(s2.start, Point(0, 1))

  }

  test("Moving and turning.Turning.should be able to turn right") {
    val s = Snake(Point.zero).turnRight

    assertEquals(s.direction, SnakeDirection.Right)

    val s2 = tick(s, 1)

    assertEquals(s2.start, Point(1, 0))
  }

  test("Moving and turning.Going (instead of turning).should be able to go up") {
    val s = Snake(Point(1, 1)).turnLeft.goUp

    assertEquals(s.direction, SnakeDirection.Up)

    val s2 = tick(s, 1)

    assertEquals(s2.start, Point(1, 2))
  }

  test("Moving and turning.Going (instead of turning).should be able to go down") {
    val s = Snake(Point(1, 1)).turnLeft.goDown

    assertEquals(s.direction, SnakeDirection.Down)

    val s2 = tick(s, 1)

    assertEquals(s2.start, Point(1, 0))
  }

  test("Moving and turning.Going (instead of turning).should be able to go left") {
    val s = Snake(Point(1, 1)).goLeft

    assertEquals(s.direction, SnakeDirection.Left)

    val s2 = tick(s, 1)

    assertEquals(s2.start, Point(0, 1))
  }

  test("Moving and turning.Going (instead of turning).should be able to go right") {
    val s = Snake(Point(1, 1)).goRight

    assertEquals(s.direction, SnakeDirection.Right)

    val s2 = tick(s, 1)

    assertEquals(s2.start, Point(2, 1))
  }

  test("Moving and turning.should wrap the world.up and over") {
    assertEquals(tick(Snake(Point(0, 5)), 5).start, Point(0, 0))
  }

  test("Moving and turning.should wrap the world.down and out") {
    assertEquals(tick(Snake(Point(5, 0)).turnLeft.turnLeft, 1).start, Point(5, 10))
  }

  test("Moving and turning.should be able to move") {

    val path: Batch[(Int, Int)] =
      Snake(Point.zero).grow
        .doTick()
        .grow
        .turnRight
        .doTick()
        .grow
        .doTick()
        .grow
        .turnLeft
        .doTick()
        .grow
        .doTick()
        .grow
        .doTick()
        .grow
        .turnLeft
        .doTick()
        .grow
        .turnLeft
        .doTick()
        .givePathList

    val expected: Batch[(Int, Int)] = Batch(
      (0, 0),
      (0, 1),
      (1, 1),
      (2, 1),
      (2, 2),
      (2, 3),
      (2, 4),
      (1, 4),
      (1, 3) 
    ).reverse

    assertEquals(path, expected)

  }

  test("Growing.should be able to grow") {
    val s = Snake(Point.zero).grow
    assertEquals(s.length, 2)
    assertEquals(s.body.length, 1)
    assertEquals(s.start, s.body.headOption.get)

    val s2 = Snake(Point(0, 3), Batch(Point(0, 2), Point(0, 1)), SnakeDirection.Up, SnakeStatus.Alive).grow
    assertEquals(s2.length, 4)
    assertEquals(s2.body.length, 3)
    assertEquals(s2.start, Point(0, 3))
    assertEquals(s2.end, Point(0, 1))
    assertEquals(s2.body, Batch(Point(0, 2), Point(0, 1), Point(0, 1)))
  }

  test("Shrinking.should be able to shrink") {
    val s =
      Snake(Point(0, 3), Batch(Point(0, 2), Point(0, 1), Point(0, 0)), SnakeDirection.Up, SnakeStatus.Alive)

    assertEquals(s.length, 4)

    assertEquals(s.shrink.length, 3)
    assertEquals(s.shrink.start, Point(0, 3))
    assertEquals(s.shrink.end, Point(0, 1))
    assertEquals(s.shrink.body, Batch(Point(0, 2), Point(0, 1)))

    assertEquals(s.shrink.shrink.shrink.start, Point(0, 3))
    assertEquals(s.shrink.shrink.shrink.end, Point(0, 3))
    assertEquals(s.shrink.shrink.shrink.shrink.shrink.shrink.shrink.length, 1)
  }

  test("Colliding.should die when it crashes into something") {
    val f: Point => CollisionCheckOutcome = pt => CollisionCheckOutcome.Crashed(pt)

    val s = Snake(Point.zero)
    assertEquals(s.status, SnakeStatus.Alive)

    val s2 = s.update(gridSize, f)._1
    assertEquals(s2.status, SnakeStatus.Dead)
  }

  test("Collecting.should grow on item pick up") {
    val f: Point => CollisionCheckOutcome = pt => CollisionCheckOutcome.PickUp(pt)

    val s = Snake(Point.zero)
    assertEquals(s.length, 1)

    val s2 = s.update(gridSize, f)._1
    assertEquals(s2.length, 2)
  }

}
