package preloader.scenes

import indigo.*
import indigo.scenes.*
import preloader.core.Assets

import preloader.core.{StartupData, Model, ViewModel}

object LevelScene extends Scene[StartupData, Model, ViewModel]:
  type SceneModel     = Model
  type SceneViewModel = ViewModel

  val name: SceneName = SceneName("game scene")
  val modelLens: Lens[Model, Model] =
    Lens.keepLatest

  val viewModelLens: Lens[ViewModel, ViewModel] =
    Lens.keepLatest

  val eventFilters: EventFilters =
    EventFilters.Restricted

  val subSystems: Set[SubSystem[Model]] =
    Set()

  def updateModel(
      context: SceneContext[StartupData],
      model: Model
  ): GlobalEvent => Outcome[Model] =
    case _ =>
      Outcome(model)

  def updateViewModel(
      context: SceneContext[StartupData],
      model: Model,
      viewModel: ViewModel
  ): GlobalEvent => Outcome[ViewModel] =
    case _ =>
      Outcome(viewModel)

  def present(
      context: SceneContext[StartupData],
      model: Model,
      viewModel: SceneViewModel
  ): Outcome[SceneUpdateFragment] =
    val viewport =
      context.frame.viewport.giveDimensions(context.frame.globalMagnification)

    val loadingText =
      Text(
        "Loading complete!",
        Assets.Fonts.fontKey,
        Assets.Fonts.fontMaterial
      ).alignCenter
        .moveTo(viewport.center + Point(0, 10))

    Outcome(
      SceneUpdateFragment(
        loadingText
      )
    )
