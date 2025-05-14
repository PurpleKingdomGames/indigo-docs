package pirate.core

import indigo.*

import pirate.scenes.level.viewmodel.LevelViewModel
import pirate.scenes.level.model.Pirate

final case class ViewModel(level: LevelViewModel):

  def update(gameTime: GameTime, pirate: Pirate): Outcome[ViewModel] =
    level.update(gameTime, pirate).map { l =>
      this.copy(
        level = l
      )
    }

object ViewModel:
  def initial: ViewModel =
    ViewModel(LevelViewModel.NotReady)
