package indigoexamples

import indigo.*
import indigo.scenes.*

import generated.*

import scala.scalajs.js.annotation.*

final case class CustomSceneModel()

final case class BootData()
object BootData:
  val empty: BootData =
    BootData()

final case class StartUpData()
object StartUpData:
  val initial: StartUpData =
    StartUpData()

final case class Model(sceneModel: CustomSceneModel)
object Model:
  def initial: Model =
    Model(
      sceneModel = CustomSceneModel()
    )

final case class ViewModel()
object ViewModel:
  val initial: ViewModel =
    ViewModel()

object CustomScene extends Scene[StartUpData, Model, ViewModel]:

  val name: SceneName = SceneName("Custom Scene")

  type SceneModel     = CustomSceneModel
  type SceneViewModel = ViewModel

  val modelLens: Lens[Model, CustomSceneModel] =
    Lens(
      model => model.sceneModel,
      (model, sceneModel) => model.copy(sceneModel)
    )

  val viewModelLens: Lens[ViewModel, ViewModel] =
    Lens.keepLatest

  val eventFilters: EventFilters = EventFilters.Permissive

  val subSystems: Set[SubSystem[Model]] = Set()

  def updateModel(
      context: SceneContext[StartUpData],
      sceneModel: CustomSceneModel
  ): GlobalEvent => Outcome[CustomSceneModel] =
    case _ => Outcome(sceneModel)

  def updateViewModel(
      context: SceneContext[StartUpData],
      sceneModel: CustomSceneModel,
      sceneViewModel: ViewModel
  ): GlobalEvent => Outcome[ViewModel] =
    case _ => Outcome(sceneViewModel)

  def present(
      context: SceneContext[StartUpData],
      sceneModel: CustomSceneModel,
      sceneViewModel: ViewModel
  ): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        Shape
          .Box(
            Rectangle(0, 0, 60, 60),
            Fill.LinearGradient(Point(0), RGBA.Magenta, Point(45), RGBA.Cyan)
          )
          .withRef(30, 30)
          .moveTo(100, 100)
          .rotateTo(Radians.fromSeconds(context.frame.time.running * 0.25))
      )
    )

@JSExportTopLevel("IndigoGame")
object MinimalScenesExample extends IndigoGame[BootData, StartUpData, Model, ViewModel]:

  def scenes(bootData: BootData): NonEmptyBatch[Scene[StartUpData, Model, ViewModel]] =
    NonEmptyBatch(CustomScene)

  def initialScene(bootData: BootData): Option[SceneName] =
    Option(CustomScene.name)

  def eventFilters: EventFilters =
    EventFilters.Permissive

  def boot(flags: Map[String, String]): Outcome[BootResult[BootData, Model]] =
    Outcome(
      BootResult(
        Config.config.noResize,
        BootData.empty
      )
    )

  def setup(
      bootData: BootData,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[StartUpData]] =
    Outcome(Startup.Success(StartUpData.initial))

  def initialModel(startupData: StartUpData): Outcome[Model] =
    Outcome(Model.initial)

  def initialViewModel(startupData: StartUpData, model: Model): Outcome[ViewModel] =
    Outcome(ViewModel.initial)

  def updateModel(context: Context[StartUpData], model: Model): GlobalEvent => Outcome[Model] =
    case _ => Outcome(model)

  def updateViewModel(
      context: Context[StartUpData],
      model: Model,
      viewModel: ViewModel
  ): GlobalEvent => Outcome[ViewModel] =
    case _ => Outcome(viewModel)

  def present(
      context: Context[StartUpData],
      model: Model,
      viewModel: ViewModel
  ): Outcome[SceneUpdateFragment] =
    Outcome(SceneUpdateFragment.empty)
