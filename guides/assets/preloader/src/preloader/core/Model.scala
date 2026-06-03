package preloader.core

import preloader.scenes.LoadingState

final case class Model(
    startupData: StartupData,
    loadingScene: LoadingState
)
object Model:

  def initial(startupData: StartupData): Model =
    Model(
      startupData,
      LoadingState.NotStarted
    )
