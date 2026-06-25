package pirate.scenes.level

import indigo.*
import indigo.scenes.*
import indigo.physics.*

import pirate.scenes.level.subsystems.CloudsAutomata
import pirate.scenes.level.subsystems.CloudsSubSystem
import pirate.core.{StartupData, Model}
import pirate.scenes.level.model.LevelModel
import pirate.scenes.level.model.Pirate
import pirate.scenes.level.model.PirateRespawn
import pirate.core.SpaceConvertors

final case class LevelScene(screenWidth: Int) extends Scene[StartupData, Model]:
  type SceneModel = Model

  val name: SceneName = LevelScene.name

  val modelLens: Lens[Model, Model] =
    Lens.keepLatest

  val eventFilters: EventFilters =
    EventFilters.Restricted

  val subSystems: Set[SubSystem[Model]] =
    Set(
      CloudsAutomata.automata,
      CloudsSubSystem(screenWidth)
    )

  def updateModel(
      context: SceneContext,
      model: Model
  ): GlobalEvent => Outcome[Model] =
    case PirateRespawn(at) =>
      model.gameScene match
        case LevelModel.NotReady =>
          Outcome(model)

        case r @ LevelModel.Ready(_, _, world, _, _) =>
          Outcome(
            model.copy(
              gameScene = r.copy(
                world = world.modifyByTag("pirate")(_.moveTo(at).withVelocity(Vector2.zero))
              )
            )
          )

    case FrameTick if model.gameScene.notReady =>
      (model.gameScene, model.startupData.levelDataStore) match
        case (LevelModel.NotReady, Some(levelDataStore)) =>
          val pirate   = Pirate.initial
          val platform = levelDataStore.terrainMap.layers.head.rowCount

          Outcome(
            model.copy(
              gameScene =
                LevelModel.makeReady(pirate, platform, SpaceConvertors(levelDataStore.tileSize))
            )
          )

        case _ =>
          Outcome(model)

    case FrameTick =>
      model.gameScene
        .update(context.frame.time, context.frame.input)
        .map(gs => model.copy(gameScene = gs))

    case _ =>
      Outcome(model)

  def present(
      context: SceneContext,
      model: Model
  ): Outcome[SceneUpdateFragment] =
    Outcome(
      model.gameScene match
        case m @ LevelModel.Ready(_, _, _, _, _) =>
          LevelView.draw(
            context.frame.time,
            m,
            model.startupData.captainClips,
            model.startupData.levelDataStore
          )

        case _ =>
          SceneUpdateFragment.empty
    )

object LevelScene:
  val name: SceneName =
    SceneName("demo")
