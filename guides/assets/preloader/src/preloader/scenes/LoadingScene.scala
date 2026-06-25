package preloader.scenes

import indigo.*
import indigo.scenes.*
import indigoextras.subsystems.*

import preloader.core.Assets
import indigo.scenes.SceneEvent.JumpTo
import preloader.core.Model

object LoadingScene extends Scene[Model]:

  type SceneModel = Model

  val name: SceneName =
    SceneName("loading scene")

  val modelLens: Lens[Model, Model] =
    Lens.keepLatest

  val eventFilters: EventFilters =
    EventFilters.Restricted

  val subSystems: Set[SubSystem[Model]] =
    Set(AssetBundleLoader[Model])

  def updateModel(
      context: SceneContext,
      model: Model
  ): GlobalEvent => Outcome[Model] =
    case FrameTick =>
      model.loadingScene match
        case LoadingState.NotStarted =>
          Outcome(model.copy(loadingScene = LoadingState.InProgress(0)))
            .addGlobalEvents(
              AssetBundleLoaderEvent.Load(
                BindingKey("Loading"),
                Assets.remainingAssets(model.startupData.assetPath)
              )
            )

        case _ =>
          Outcome(model)

    case AssetBundleLoaderEvent.LoadProgress(_, percent, _, _) =>
      Outcome(model.copy(loadingScene = LoadingState.InProgress(percent)))

    case AssetBundleLoaderEvent.Success(_) =>
      Outcome(model.copy(loadingScene = LoadingState.Complete))
        .addGlobalEvents(JumpTo(LevelScene.name))

    case AssetBundleLoaderEvent.Failure(_, _) =>
      Outcome(model.copy(loadingScene = LoadingState.Error))

    case _ =>
      Outcome(model)

  def present(
      context: SceneContext,
      model: Model
  ): Outcome[SceneUpdateFragment] =
    val viewportCenter =
      context.frame.viewport.center

    val message: String =
      model.loadingScene match
        case LoadingState.NotStarted =>
          "Loading..."

        case LoadingState.InProgress(percent) =>
          s"Loading...${percent.toString()}%"

        case LoadingState.Complete =>
          "Loading...100%"

        case LoadingState.Error =>
          "Uh oh, loading failed..."

    val loadingText =
      Text(
        message,
        Assets.Fonts.fontKey,
        Assets.Fonts.fontMaterial
      ).alignCenter
        .moveTo(viewportCenter + Point(0, 10))

    Outcome(
      SceneUpdateFragment(
        loadingText,
        model.startupData.captainLoading.moveTo(viewportCenter)
      ).withMagnification(2)
    )

enum LoadingState:
  case NotStarted               extends LoadingState
  case InProgress(percent: Int) extends LoadingState
  case Complete                 extends LoadingState
  case Error                    extends LoadingState
