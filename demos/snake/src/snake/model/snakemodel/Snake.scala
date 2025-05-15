package snake.model.snakemodel

import indigo.*

final case class Snake(start: Point, body: Batch[Point], direction: SnakeDirection, status: SnakeStatus):

  def goUp: Snake =
    Snake.goUp(this)

  def goDown: Snake =
    Snake.goDown(this)

  def goLeft: Snake =
    Snake.goLeft(this)

  def goRight: Snake =
    Snake.goRight(this)

  def turnLeft: Snake =
    Snake.turnLeft(this)

  def turnRight: Snake =
    Snake.turnRight(this)

  def update(gridSize: Size, collisionCheck: Point => CollisionCheckOutcome): (Snake, CollisionCheckOutcome) =
    Snake.update(this, gridSize, collisionCheck)

  def end: Point =
    Snake.end(this)

  def grow: Snake =
    Snake.grow(this)

  def shrink: Snake =
    Snake.shrink(this)

  def crash: Snake =
    Snake.crash(this)

  def length: Int =
    1 + body.length

  def givePath: Batch[Point] =
    start :: body

  def givePathList: Batch[(Int, Int)] =
    (start :: body).map(p => (p.x.toInt, p.y.toInt))

object Snake:

  def apply(start: Point): Snake =
    Snake(start, Batch.empty, SnakeDirection.Up, SnakeStatus.Alive)

  def apply(x: Int, y: Int): Snake =
    Snake(Point(x, y), Batch.empty, SnakeDirection.Up, SnakeStatus.Alive)

  def turnLeft(snake: Snake): Snake =
    snake.copy(direction = snake.direction.turnLeft)

  def turnRight(snake: Snake): Snake =
    snake.copy(direction = snake.direction.turnRight)

  def goUp(snake: Snake): Snake =
    snake.copy(direction = snake.direction.goUp)

  def goDown(snake: Snake): Snake =
    snake.copy(direction = snake.direction.goDown)

  def goLeft(snake: Snake): Snake =
    snake.copy(direction = snake.direction.goLeft)

  def goRight(snake: Snake): Snake =
    snake.copy(direction = snake.direction.goRight)

  def end(snake: Snake): Point =
    snake.body.reverse.headOption.getOrElse(snake.start)

  def grow(snake: Snake): Snake =
    snake.copy(body = snake.body :+ end(snake))

  def shrink(snake: Snake): Snake =
    snake.copy(body = snake.body.dropRight(1))

  def crash(snake: Snake): Snake =
    snake.copy(status = SnakeStatus.Dead)

  def update(
      snake: Snake,
      gridSize: Size,
      collisionCheck: Point => CollisionCheckOutcome
  ): (Snake, CollisionCheckOutcome) =
    (nextPosition(gridSize) andThen collisionCheck andThen snakeUpdate(snake))(snake)

  def nextPosition(gridSize: Size): Snake => Point =
    snake =>
      wrap(
        snake.direction
          .oneSquareForward(snake.start),
        gridSize
      )

  def wrap(gridPoint: Point, gridSize: Size): Point =
    gridPoint.copy(
      x = if (gridPoint.x < 0) gridSize.width else gridPoint.x % gridSize.width,
      y = if (gridPoint.y < 0) gridSize.height else gridPoint.y % gridSize.height
    )

  def snakeUpdate(snake: Snake): CollisionCheckOutcome => (Snake, CollisionCheckOutcome) =
    case oc @ CollisionCheckOutcome.NoCollision(pt) =>
      (moveToPosition(snake, pt), oc)

    case oc @ CollisionCheckOutcome.PickUp(pt) =>
      (moveToPosition(snake.grow, pt), oc)

    case oc @ CollisionCheckOutcome.Crashed(_) =>
      (snake.crash, oc)

  def moveToPosition(snake: Snake, snakePoint: Point): Snake =
    snake match
      case Snake(_, Batch(), d, s) =>
        Snake(snakePoint, Batch.empty, d, s)

      case Snake(h, l, d, s) =>
        Snake(snakePoint, h :: l.reverse.drop(1).reverse, d, s)
