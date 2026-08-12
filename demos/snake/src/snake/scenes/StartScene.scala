package snake.scenes

import indigo.*
import indigo.scenes.*

import snake.init.GameAssets
import snake.GameReset
import snake.model.GameModel
import snake.generated.Assets

object StartScene extends Scene[GameModel]:
  type SceneModel = Point

  val name: SceneName =
    SceneName("start")

  val modelLens: Lens[GameModel, Point] =
    Lens.readOnly(
      _.startupData.viewConfig.center
    )

  val eventFilters: EventFilters =
    EventFilters.Restricted

  val subSystems: Set[SubSystem[GameModel]] =
    Set()

  def updateModel(
      context: SceneContext,
      center: Point
  ): GlobalEvent => Outcome[Point] = {
    case KeyboardEvent.KeyUp(Key.SPACE) =>
      Outcome(center)
        .addGlobalEvents(
          GameReset,
          SceneEvent.JumpTo(ControlsScene.name)
        )

    case _ =>
      Outcome(center)
  }

  def present(
      context: SceneContext,
      center: Point
  ): Outcome[SceneUpdateFragment] =
    Outcome {
      val horizontalCenter: Int = center.x
      val verticalMiddle: Int   = center.y

      SceneUpdateFragment(
        LayerKey("ui") -> Layer.Content(
          drawTitleText(horizontalCenter, verticalMiddle) ++
            SharedElements.drawHitSpaceToStart(horizontalCenter, Seconds(1), context.frame.time)
        )
      )
        .withAudio(
          Assets.assets.introSceneAudio
        )
    }

  def drawTitleText(center: Int, middle: Int): Batch[SceneNode] =
    Batch(
      Text("snake!", center, middle - 20, GameAssets.fontKey, GameAssets.fontMaterial).alignCenter,
      Text(
        "presented in glorious 1 bit graphics",
        center,
        middle - 5,
        GameAssets.fontKey,
        GameAssets.fontMaterial
      ).alignCenter,
      Text(
        "Made with Indigo",
        center,
        middle + 10,
        GameAssets.fontKey,
        GameAssets.fontMaterial
      ).alignCenter
    )
