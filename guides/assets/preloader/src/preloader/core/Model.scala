package preloader.core

import preloader.scenes.LoadingState

final case class Model(
    loadingScene: LoadingState
)
object Model:

  def initial: Model =
    Model(
      LoadingState.NotStarted
    )
