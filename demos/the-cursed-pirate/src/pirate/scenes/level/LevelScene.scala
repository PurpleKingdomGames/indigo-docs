package pirate.scenes.level

import indigo.*
import indigo.scenes.*
import indigo.physics.*

import pirate.scenes.level.subsystems.CloudsAutomata
import pirate.scenes.level.subsystems.CloudsSubSystem
import pirate.core.{StartupData, Model, ViewModel}
import pirate.scenes.level.model.LevelModel
import pirate.scenes.level.viewmodel.LevelViewModel
import pirate.scenes.level.viewmodel.PirateViewState
import pirate.scenes.level.model.Pirate
import pirate.scenes.level.model.PirateRespawn
import pirate.core.SpaceConvertors

final case class LevelScene(screenWidth: Int) extends Scene[StartupData, Model, ViewModel]:
  type SceneModel     = LevelModel
  type SceneViewModel = LevelViewModel

  val name: SceneName = LevelScene.name
  val modelLens: Lens[Model, LevelModel] =
    Lens(
      _.gameScene,
      (m, sm) => m.copy(gameScene = sm)
    )

  val viewModelLens: Lens[ViewModel, LevelViewModel] =
    Lens(
      _.level,
      (vm, svm) => vm.copy(level = svm)
    )

  val eventFilters: EventFilters =
    EventFilters.Restricted

  val subSystems: Set[SubSystem[Model]] =
    Set(
      CloudsAutomata.automata,
      CloudsSubSystem(screenWidth)
    )

  def updateModel(
      context: SceneContext[StartupData],
      model: LevelModel
  ): GlobalEvent => Outcome[LevelModel] =
    case PirateRespawn(at) =>
      model match
        case LevelModel.NotReady =>
          Outcome(model)

        case r @ LevelModel.Ready(_, _, world) =>
          Outcome(
            r.copy(
              world = world.modifyByTag("pirate")(_.moveTo(at).withVelocity(Vector2.zero))
            )
          )

    case FrameTick if model.notReady =>
      (model, context.startUpData.levelDataStore) match
        case (LevelModel.NotReady, Some(levelDataStore)) =>
          val pirate   = Pirate.initial
          val platform = levelDataStore.terrainMap.layers.head.rowCount

          Outcome(LevelModel.makeReady(pirate, platform, SpaceConvertors(levelDataStore.tileSize)))

        case _ =>
          Outcome(model)

    case FrameTick =>
      model.update(context.frame.time, context.frame.input)

    case _ =>
      Outcome(model)

  def updateViewModel(
      context: SceneContext[StartupData],
      model: LevelModel,
      viewModel: LevelViewModel
  ): GlobalEvent => Outcome[LevelViewModel] =
    case FrameTick if viewModel.notReady =>
      (viewModel, context.startUpData.levelDataStore) match
        case (LevelViewModel.NotReady, Some(levelDataStore)) =>
          Outcome(
            LevelViewModel.Ready(SpaceConvertors(levelDataStore.tileSize), PirateViewState.initial)
          )

        case _ =>
          Outcome(viewModel)

    case FrameTick =>
      model match
        case LevelModel.NotReady =>
          Outcome(viewModel)

        case LevelModel.Ready(pirate, _, _) =>
          viewModel.update(context.frame.time, pirate)

    case _ =>
      Outcome(viewModel)

  def present(
      context: SceneContext[StartupData],
      model: LevelModel,
      viewModel: SceneViewModel
  ): Outcome[SceneUpdateFragment] =
    Outcome(
      (model, viewModel) match
        case (m @ LevelModel.Ready(_, _, _), vm @ LevelViewModel.Ready(_, _)) =>
          LevelView.draw(
            context.frame.time,
            m,
            vm,
            context.startUpData.captainClips,
            context.startUpData.levelDataStore
          )

        case _ =>
          SceneUpdateFragment.empty
    )

object LevelScene:
  val name: SceneName =
    SceneName("demo")
