package pirate.core

import pirate.scenes.level.model.LevelModel
import pirate.scenes.loading.LoadingState

final case class Model(
    startupData: StartupData,
    loadingScene: LoadingState,
    gameScene: LevelModel
)
object Model:

  def initial(startupData: StartupData): Model =
    Model(
      startupData,
      LoadingState.NotStarted,
      LevelModel.NotReady
    )
