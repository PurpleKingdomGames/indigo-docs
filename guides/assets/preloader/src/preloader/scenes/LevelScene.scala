package preloader.scenes

import indigo.*
import indigo.scenes.*
import preloader.core.Assets

import preloader.core.{StartupData, Model}

object LevelScene extends Scene[StartupData, Model]:
  type SceneModel = Model

  val name: SceneName = SceneName("game scene")
  val modelLens: Lens[Model, Model] =
    Lens.keepLatest

  val eventFilters: EventFilters =
    EventFilters.Restricted

  val subSystems: Set[SubSystem[Model]] =
    Set()

  def updateModel(
      context: SceneContext,
      model: Model
  ): GlobalEvent => Outcome[Model] =
    case _ =>
      Outcome(model)

  def present(
      context: SceneContext,
      model: Model
  ): Outcome[SceneUpdateFragment] =
    val viewportCenter =
      (context.frame.viewport / 2).toPoint

    val loadingText =
      Text(
        "Loading complete!",
        Assets.Fonts.fontKey,
        Assets.Fonts.fontMaterial
      ).alignCenter
        .moveTo(viewportCenter + Point(0, 10))

    Outcome(
      SceneUpdateFragment(
        loadingText
      ).withMagnification(2)
    )
