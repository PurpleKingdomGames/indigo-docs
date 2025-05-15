package snake.model.snakemodel

import indigo.Point

enum CollisionCheckOutcome(val gridPoint: Point):
  case NoCollision(gridPosition: Point) extends CollisionCheckOutcome(gridPosition)
  case PickUp(gridPosition: Point)      extends CollisionCheckOutcome(gridPosition)
  case Crashed(gridPosition: Point)     extends CollisionCheckOutcome(gridPosition)
