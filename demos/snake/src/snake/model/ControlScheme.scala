package snake.model

import indigo.*

import snake.model.snakemodel.{Snake, SnakeDirection}

enum ControlScheme:
  case Turning(left: Key, right: Key)                      extends ControlScheme
  case Directed(up: Key, down: Key, left: Key, right: Key) extends ControlScheme

object ControlScheme:
  val turningKeys: Turning   = Turning(Key.ARROW_LEFT, Key.ARROW_RIGHT)
  val directedKeys: Directed = Directed(Key.ARROW_UP, Key.ARROW_DOWN, Key.ARROW_LEFT, Key.ARROW_RIGHT)

  extension (cs: ControlScheme)
    def instructSnake(keyboardEvent: KeyboardEvent, snake: Snake, currentDirection: SnakeDirection): Snake =
      (cs, keyboardEvent) match
        case (ControlScheme.Turning(left, _), KeyboardEvent.KeyDown(code)) if code === left =>
          currentDirection.makeLegalTurn(snake.turnLeft).getOrElse(snake)

        case (ControlScheme.Turning(_, right), KeyboardEvent.KeyDown(code)) if code === right =>
          currentDirection.makeLegalTurn(snake.turnRight).getOrElse(snake)

        case (ControlScheme.Directed(up, _, _, _), KeyboardEvent.KeyDown(code)) if code === up =>
          currentDirection.makeLegalTurn(snake.goUp).getOrElse(snake)

        case (ControlScheme.Directed(_, down, _, _), KeyboardEvent.KeyDown(code)) if code === down =>
          currentDirection.makeLegalTurn(snake.goDown).getOrElse(snake)

        case (ControlScheme.Directed(_, _, left, _), KeyboardEvent.KeyDown(code)) if code === left =>
          currentDirection.makeLegalTurn(snake.goLeft).getOrElse(snake)

        case (ControlScheme.Directed(_, _, _, right), KeyboardEvent.KeyDown(code)) if code === right =>
          currentDirection.makeLegalTurn(snake.goRight).getOrElse(snake)

        case _ =>
          snake

    def swap: ControlScheme =
      cs match
        case ControlScheme.Turning(_, _) =>
          ControlScheme.directedKeys

        case ControlScheme.Directed(_, _, _, _) =>
          ControlScheme.turningKeys
