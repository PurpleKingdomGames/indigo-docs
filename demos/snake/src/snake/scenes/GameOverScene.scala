package snake.scenes

import indigo.*
import indigo.scenes.*
import snake.init.{GameAssets, StartupData}
import snake.model.GameModel

object GameOverScene extends Scene[StartupData, GameModel]:
  type SceneModel     = GameOverScene.Model

  val name: SceneName =
    SceneName("game over")

  val modelLens: Lens[GameModel, GameOverScene.Model] =
    Lens.readOnly(
      m => GameOverScene.Model(m.score, m.startupData.viewConfig.center)
    )

  val eventFilters: EventFilters =
    EventFilters.Restricted

  val subSystems: Set[SubSystem[GameModel]] =
    Set()

  def updateModel(context: SceneContext, model: GameOverScene.Model): GlobalEvent => Outcome[GameOverScene.Model] = {
    case KeyboardEvent.KeyUp(Key.SPACE) =>
      Outcome(model)
        .addGlobalEvents(SceneEvent.JumpTo(StartScene.name))

    case _ =>
      Outcome(model)
  }

  def present(
      context: SceneContext,
      model: GameOverScene.Model
  ): Outcome[SceneUpdateFragment] =
    Outcome {
      val horizontalCenter: Int = model.center.x
      val verticalMiddle: Int   = model.center.y

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
              s"You scored: ${model.pointsScored.toString()} pts!",
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

  final case class Model(pointsScored: Int, center: Point)