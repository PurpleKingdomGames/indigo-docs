package snake.scenes

import indigo.*
import indigo.scenes.*
import snake.model.ControlScheme
import snake.init.GameAssets
import snake.model.GameModel

object ControlsScene extends Scene[GameModel]:
  type SceneModel = ControlsScene.Model

  val name: SceneName =
    SceneName("controls")

  val modelLens: Lens[GameModel, ControlsScene.Model] =
    Lens(
      m => ControlsScene.Model(m.controlScheme, m.startupData.viewConfig.center),
      (m, c) => m.copy(controlScheme = c.scheme)
    )

  val eventFilters: EventFilters =
    EventFilters.Restricted

  val subSystems: Set[SubSystem[GameModel]] =
    Set()

  def updateModel(
      context: SceneContext,
      model: ControlsScene.Model
  ): GlobalEvent => Outcome[ControlsScene.Model] =
    case KeyboardEvent.KeyUp(Key.SPACE) =>
      Outcome(model)
        .addGlobalEvents(SceneEvent.JumpTo(GameScene.name))

    case KeyboardEvent.KeyUp(Key.ARROW_UP) | KeyboardEvent.KeyUp(Key.ARROW_DOWN) =>
      Outcome(model.copy(scheme = model.scheme.swap))

    case _ =>
      Outcome(model)

  def present(
      context: SceneContext,
      model: ControlsScene.Model
  ): Outcome[SceneUpdateFragment] =
    Outcome {
      val horizontalCenter: Int = model.center.x
      val verticalMiddle: Int   = model.center.y

      SceneUpdateFragment(
        LayerKey("ui") -> Layer.Content(
          drawControlsText(24, verticalMiddle, model.scheme) ++
            Batch(drawSelectText(horizontalCenter)) ++
            SharedElements.drawHitSpaceToStart(horizontalCenter, Seconds(1), context.frame.time)
        )
      )
    }

  def drawControlsText(center: Int, middle: Int, controlScheme: ControlScheme): Batch[SceneNode] =
    Batch(
      Text(
        "select controls",
        center,
        middle - 20,
        GameAssets.fontKey,
        GameAssets.fontMaterial
      ).alignLeft
    ) ++ {
      controlScheme match
        case ControlScheme.Turning(_, _) =>
          Batch(
            Text(
              "[_] direction (all arrow keys)",
              center,
              middle - 5,
              GameAssets.fontKey,
              GameAssets.fontMaterial
            ).alignLeft,
            Text(
              "[x] turn (left and right arrows)",
              center,
              middle + 10,
              GameAssets.fontKey,
              GameAssets.fontMaterial
            ).alignLeft
          )

        case ControlScheme.Directed(_, _, _, _) =>
          Batch(
            Text(
              "[x] direction (all arrow keys)",
              center,
              middle - 5,
              GameAssets.fontKey,
              GameAssets.fontMaterial
            ).alignLeft,
            Text(
              "[_] turn (left and right arrows)",
              center,
              middle + 10,
              GameAssets.fontKey,
              GameAssets.fontMaterial
            ).alignLeft
          )

    }

  def drawSelectText(center: Int): SceneNode =
    Text(
      "Up / Down arrows to select.",
      center,
      205,
      GameAssets.fontKey,
      GameAssets.fontMaterial
    ).alignCenter

  final case class Model(scheme: ControlScheme, center: Point)
