package snake.scenes

import indigo.*
import indigo.scenes.*

import snake.init.GameAssets
import snake.model.ViewModel
import snake.init.StartupData
import snake.GameReset
import snake.model.GameModel
import snake.generated.Assets

object StartScene extends Scene[StartupData, GameModel, ViewModel]:
  type SceneModel     = Unit
  type SceneViewModel = Unit

  val name: SceneName =
    SceneName("start")

  val modelLens: Lens[GameModel, Unit] =
    Lens.unit

  val viewModelLens: Lens[ViewModel, Unit] =
    Lens.unit

  val eventFilters: EventFilters =
    EventFilters.Restricted
      .withViewModelFilter(_ => None)

  val subSystems: Set[SubSystem[GameModel]] =
    Set()

  def updateModel(
      context: SceneContext[StartupData],
      snakeGameModel: Unit
  ): GlobalEvent => Outcome[Unit] = {
    case KeyboardEvent.KeyUp(Key.SPACE) =>
      Outcome(snakeGameModel)
        .addGlobalEvents(
          GameReset,
          SceneEvent.JumpTo(ControlsScene.name)
        )

    case _ =>
      Outcome(snakeGameModel)
  }

  def updateViewModel(
      context: SceneContext[StartupData],
      snakeGameModel: Unit,
      snakeViewModel: Unit
  ): GlobalEvent => Outcome[Unit] =
    _ => Outcome(snakeViewModel)

  def present(
      context: SceneContext[StartupData],
      snakeGameModel: Unit,
      snakeViewModel: Unit
  ): Outcome[SceneUpdateFragment] =
    Outcome {
      val horizontalCenter: Int = context.startUpData.viewConfig.horizontalCenter
      val verticalMiddle: Int   = context.startUpData.viewConfig.verticalMiddle

      SceneUpdateFragment.empty
        .addLayer(
          LayerKey("ui") -> Layer(
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
      Text("Made with Indigo", center, middle + 10, GameAssets.fontKey, GameAssets.fontMaterial).alignCenter
    )
