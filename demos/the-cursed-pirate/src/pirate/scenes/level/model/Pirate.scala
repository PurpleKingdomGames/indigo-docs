package pirate.scenes.level.model

import indigo.*
import pirate.core.SpaceConvertors

final case class Pirate(
    state: PirateState,
    lastRespawn: Seconds
)

object Pirate:

  val respawnPoint = Vertex(9.5, -2)

  def initialBounds(spaceConvertors: SpaceConvertors): BoundingBox =
    val startPosition = Vertex(9.5, 6)
    val size          = spaceConvertors.ScreenToWorld.convert(Point(15, 28))
    BoundingBox(startPosition, size)

  val initial: Pirate =
    Pirate(
      PirateState.FallingRight,
      Seconds.zero
    )

  val inputMappings: Boolean => InputMapping[Vector2] = inMidAir => {
    val xSpeed: Double = if inMidAir then 3.5d else 4.0d
    val ySpeed: Double = if inMidAir then 0.0d else -10.0d

    InputMapping(
      Combo.withKeyInputs(Key.ARROW_LEFT, Key.ARROW_UP)  -> Vector2(-xSpeed, ySpeed),
      Combo.withKeyInputs(Key.ARROW_LEFT, Key.SPACE)     -> Vector2(-xSpeed, ySpeed),
      Combo.withKeyInputs(Key.ARROW_LEFT)                -> Vector2(-xSpeed, 0.0d),
      Combo.withKeyInputs(Key.ARROW_RIGHT, Key.ARROW_UP) -> Vector2(xSpeed, ySpeed),
      Combo.withKeyInputs(Key.ARROW_RIGHT, Key.SPACE)    -> Vector2(xSpeed, ySpeed),
      Combo.withKeyInputs(Key.ARROW_RIGHT)               -> Vector2(xSpeed, 0.0d),
      Combo.withKeyInputs(Key.ARROW_UP)                  -> Vector2(0.0d, ySpeed),
      Combo.withKeyInputs(Key.SPACE)                     -> Vector2(0.0d, ySpeed),
      Combo.withGamepadInputs(
        GamepadInput.LEFT_ANALOG(_ < -0.5, _ => true, false),
        GamepadInput.Cross
      )                                                                             -> Vector2(-xSpeed, ySpeed),
      Combo.withGamepadInputs(GamepadInput.LEFT_ANALOG(_ < -0.5, _ => true, false)) -> Vector2(-xSpeed, 0.0d),
      Combo.withGamepadInputs(
        GamepadInput.LEFT_ANALOG(_ > 0.5, _ => true, false),
        GamepadInput.Cross
      )                                                                            -> Vector2(xSpeed, ySpeed),
      Combo.withGamepadInputs(GamepadInput.LEFT_ANALOG(_ > 0.5, _ => true, false)) -> Vector2(xSpeed, 0.0d),
      Combo.withGamepadInputs(GamepadInput.Cross)                                  -> Vector2(0.0d, ySpeed)
    )
  }

  def decideNextState(state: PirateState, velocity: Vector2, appliedForce: Vector2, yDiff: Double): PirateState =
    val stateAcceptable = state.isFalling || state.isGrounded

    if (velocity.y > -0.01 && velocity.y < 0.01 && stateAcceptable && yDiff < 0.00001) || yDiff < 0.00001 then nextStanding(appliedForce.x)
    else if velocity.y > 0.001 then nextFalling(state)(velocity.x)
    else nextJumping(state)(velocity.x)

  private def nextStateFromDiffX(
      movingLeft: PirateState,
      movingRight: PirateState,
      otherwise: PirateState
  ): Double => PirateState =
    xDiff =>
      if xDiff < -0.01 then movingLeft
      else if xDiff > 0.01 then movingRight
      else otherwise

  lazy val nextStanding: Double => PirateState =
    nextStateFromDiffX(
      PirateState.MoveLeft,
      PirateState.MoveRight,
      PirateState.Idle
    )

  def nextFalling(state: PirateState): Double => PirateState =
    nextStateFromDiffX(
      PirateState.FallingLeft,
      PirateState.FallingRight,
      state match
        case PirateState.FallingLeft | PirateState.JumpingLeft =>
          PirateState.FallingLeft

        case PirateState.FallingRight | PirateState.JumpingRight =>
          PirateState.FallingRight

        case _ =>
          PirateState.FallingRight
    )

  def nextJumping(state: PirateState): Double => PirateState =
    nextStateFromDiffX(
      PirateState.JumpingLeft,
      PirateState.JumpingRight,
      state match
        case l @ PirateState.JumpingLeft  => l
        case r @ PirateState.JumpingRight => r
        case _                            => PirateState.JumpingRight
    )

final case class PirateRespawn(at: Vertex) extends GlobalEvent
