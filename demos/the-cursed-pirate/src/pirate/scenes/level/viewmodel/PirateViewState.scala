package pirate.scenes.level.viewmodel

import indigo.*
import pirate.scenes.level.model.Pirate
import pirate.scenes.level.model.PirateState
import pirate.generated.Assets.*

final case class PirateViewState(
    facingRight: Boolean,
    soundLastPlayed: Seconds
):

  def update(gameTime: GameTime, pirate: Pirate): Outcome[PirateViewState] =
    pirate.state match
      case PirateState.Idle =>
        Outcome(this)

      case PirateState.MoveLeft =>
        val (walkingSound, lastPlayed) = updateWalkSound(gameTime, soundLastPlayed)

        Outcome(
          this.copy(
            facingRight = false,
            soundLastPlayed = lastPlayed
          )
        ).addGlobalEvents(walkingSound)

      case PirateState.MoveRight =>
        val (walkingSound, lastPlayed) = updateWalkSound(gameTime, soundLastPlayed)

        Outcome(
          this.copy(
            facingRight = true,
            soundLastPlayed = lastPlayed
          )
        ).addGlobalEvents(walkingSound)

      case PirateState.FallingLeft =>
        Outcome(this.copy(facingRight = false))

      case PirateState.FallingRight =>
        Outcome(this.copy(facingRight = true))

      case PirateState.JumpingLeft =>
        Outcome(this.copy(facingRight = false))

      case PirateState.JumpingRight =>
        Outcome(this.copy(facingRight = true))

  def updateWalkSound(gameTime: GameTime, soundLastPlayed: Seconds): (Batch[GlobalEvent], Seconds) =
    if gameTime.running > soundLastPlayed + Seconds(0.25) then
      (Batch(PlaySound(assets.sounds.walk, Volume(0.5d))), gameTime.running)
    else (Batch.empty, soundLastPlayed)

object PirateViewState:

  val initial: PirateViewState =
    PirateViewState(true, Seconds.zero)
