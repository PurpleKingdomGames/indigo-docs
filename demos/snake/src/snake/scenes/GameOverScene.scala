package snake.scenes

import indigo.*
import indigo.scenes.*
import snake.model.ViewModel
import snake.init.{GameAssets, StartupData}
import snake.model.GameModel

object GameOverScene extends Scene[StartupData, GameModel, ViewModel]:
  type SceneModel     = Int
  type SceneViewModel = Unit

  val name: SceneName =
    SceneName("game over")

  val modelLens: Lens[GameModel, Int] =
    Lens.readOnly(_.score)

  val viewModelLens: Lens[ViewModel, Unit] =
    Lens.unit

  val eventFilters: EventFilters =
    EventFilters.Restricted
      .withViewModelFilter(_ => None)

  val subSystems: Set[SubSystem[GameModel]] =
    Set()

  def updateModel(context: SceneContext[StartupData], pointsScored: Int): GlobalEvent => Outcome[Int] = {
    case KeyboardEvent.KeyUp(Key.SPACE) =>
      Outcome(pointsScored)
        .addGlobalEvents(SceneEvent.JumpTo(StartScene.name))

    case _ =>
      Outcome(pointsScored)
  }

  def updateViewModel(
      context: SceneContext[StartupData],
      pointsScored: Int,
      sceneViewModel: Unit
  ): GlobalEvent => Outcome[Unit] =
    _ => Outcome(sceneViewModel)

  def present(
      context: SceneContext[StartupData],
      pointsScored: Int,
      sceneViewModel: Unit
  ): Outcome[SceneUpdateFragment] =
    Outcome {
      val horizontalCenter: Int = context.startUpData.viewConfig.horizontalCenter
      val verticalMiddle: Int   = context.startUpData.viewConfig.verticalMiddle

      SceneUpdateFragment.empty
        .addLayer(
          LayerKey("ui") -> Layer(
            Text(
              "Game Over!",
              horizontalCenter,
              verticalMiddle - 20,
              GameAssets.fontKey,
              GameAssets.fontMaterial
            ).alignCenter,
            Text(
              s"You scored: ${pointsScored.toString()} pts!",
              horizontalCenter,
              verticalMiddle - 5,
              GameAssets.fontKey,
              GameAssets.fontMaterial
            ).alignCenter,
            Text(
              "(hit space to restart)",
              horizontalCenter,
              220,
              GameAssets.fontKey,
              GameAssets.fontMaterial
            ).alignCenter
          )
        )
    }
