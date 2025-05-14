package pirate.core

import pirate.scenes.level.model.LevelModel
import pirate.scenes.loading.LoadingState

final case class Model(
    loadingScene: LoadingState,
    gameScene: LevelModel
)
object Model:

  def initial: Model =
    Model(
      LoadingState.NotStarted,
      LevelModel.NotReady
    )
