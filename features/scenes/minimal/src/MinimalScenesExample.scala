package indigoexamples

import indigo.*
import indigo.scenes.*

import generated.*

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

object CustomScene extends Scene[Model]:

  val name: SceneName = SceneName("Custom Scene")

  type SceneModel = CustomSceneModel

  val modelLens: Lens[Model, CustomSceneModel] =
    Lens(
      model => model.sceneModel,
      (model, sceneModel) => model.copy(sceneModel)
    )

  val eventFilters: EventFilters = EventFilters.Permissive

  val subSystems: Set[SubSystem[Model]] = Set()

  def updateModel(
      context: SceneContext,
      sceneModel: CustomSceneModel
  ): GlobalEvent => Outcome[CustomSceneModel] =
    case _ => Outcome(sceneModel)

  def present(
      context: SceneContext,
      sceneModel: CustomSceneModel
  ): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        LayerKey("demo") -> Layer.Content(
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
    )

class MinimalScenesExample() extends Game[BootData, StartUpData, Model]:

  def gameId: GameId = GameId("minimal-scenes")

  def scenes(bootData: BootData): NonEmptyBatch[Scene[Model]] =
    NonEmptyBatch(CustomScene)

  def initialScene(bootData: BootData): Option[SceneName] =
    Option(CustomScene.name)

  def eventFilters: EventFilters =
    EventFilters.Permissive

  def boot(flags: Map[String, String]): Outcome[BootResult[BootData, Model]] =
    Outcome(
      BootResult(
        Config.config,
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

  def updateModel(context: Context, model: Model): GlobalEvent => Outcome[Model] =
    case _ => Outcome(model)

  def present(
      context: Context,
      model: Model
  ): Outcome[SceneUpdateFragment] =
    Outcome(SceneUpdateFragment.empty)
