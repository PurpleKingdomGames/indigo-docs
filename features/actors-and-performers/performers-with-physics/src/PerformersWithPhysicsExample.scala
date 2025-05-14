package indigoexamples

import indigo.*
import indigo.syntax.*
import indigo.scenes.*
import indigo.physics.*
import indigoextras.performers.*

import generated.*

import scala.scalajs.js.annotation.*

final case class CustomSceneModel(spawned: Boolean, target: Point)
object CustomSceneModel:
  val initial: CustomSceneModel =
    CustomSceneModel(false, Point.zero)

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
  def initial(startupData: StartUpData): Model =
    Model(
      sceneModel = CustomSceneModel.initial
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

  val subSystems: Set[SubSystem[Model]] =
    Set(
      StageManager[Model, Point](
        SubSystemId("performer system"),
        _.sceneModel.target
      ).withWorldOptions(
        WorldOptions.default
          .withForces(Vector2(0, 0))
          .withResistance(Resistance(0.25))
          .withSimulationSettings(SimulationSettings.default)
      )
    )

  def updateModel(
      context: SceneContext[StartUpData],
      sceneModel: CustomSceneModel
  ): GlobalEvent => Outcome[CustomSceneModel] =
    case FrameTick if !sceneModel.spawned && context.frame.viewport.size != Size.zero =>
      val viewportSize =
        context.frame.viewport.giveDimensions(context.frame.globalMagnification)

      val zombies: Batch[ZombiePerformer] =
        (0 to 30).toBatch.map { i =>
          val dice = Dice.fromSeed(i.toLong)

          val x = dice.roll(viewportSize.width / 2)
          val y = dice.roll(viewportSize.height / 2)

          val acceleration: Double = dice.roll(40) + 10

          ZombiePerformer(
            i,
            Point(x, y),
            acceleration
          )
        }

      Outcome(sceneModel.copy(spawned = true))
        .addGlobalEvents(
          PerformerEvent.Add(Constants.LayerKeys.background, ZombieTargetPerformer())
        )
        .addGlobalEvents(PerformerEvent.AddAll(Constants.LayerKeys.game, zombies))

    case _ =>
      Outcome(sceneModel)

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
        Constants.LayerKeys.background -> Layer.empty,
        Constants.LayerKeys.game       -> Layer.Stack.empty
      )
    )

object Constants:
  object LayerKeys:
    val background: LayerKey = LayerKey("background")
    val game: LayerKey       = LayerKey("game")

@JSExportTopLevel("IndigoGame")
object PerformersWithPhysicsExample extends IndigoGame[BootData, StartUpData, Model, ViewModel]:

  def scenes(bootData: BootData): NonEmptyList[Scene[StartUpData, Model, ViewModel]] =
    NonEmptyList(CustomScene)

  def initialScene(bootData: BootData): Option[SceneName] =
    Option(CustomScene.name)

  def eventFilters: EventFilters =
    EventFilters.Permissive

  def boot(flags: Map[String, String]): Outcome[BootResult[BootData, Model]] =
    Outcome(
      BootResult(
        Config.config.noResize.withMagnification(2),
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
    Outcome(Model.initial(startupData))

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

final case class ZombiePerformer(
    index: Int,
    position: Point,
    acceleration: Double
) extends Performer.Stunt[Point]:

  val radius = 5

  def id: PerformerId       = PerformerId("zombie-" + index)
  def depth: PerformerDepth = PerformerDepth.zero

  def initialCollider: Collider[PerformerId] =
    Collider
      .Circle(
        id,
        BoundingCircle(position.toVertex, radius.toDouble)
      )
      .withRestitution(Restitution(0.5))

  def update(context: PerformerContext[Point]): Performer.Stunt[Point] =
    this

  def updateCollider(
      context: PerformerContext[Point],
      collider: Collider[PerformerId]
  ): Collider[PerformerId] =
    val target = context
      .findColliderById(ZombieTargetPerformer.id)
      .map(_.boundingBox.position)
      .getOrElse(Vertex.zero)
    val toTarget = (target - collider.boundingBox.position).toVector2.normalise

    collider.withVelocity(collider.velocity + (toTarget * acceleration))

  def present(context: PerformerContext[Point], collider: Collider[PerformerId]): Batch[SceneNode] =
    val color =
      index % 3 match
        case 0 => RGBA.Cyan
        case 1 => RGBA.Yellow
        case _ => RGBA.SlateGray

    Batch(
      Shape.Circle(
        Circle(collider.position.toPoint, radius),
        Fill.Color(color),
        Stroke(1, RGBA.White)
      )
    )

final case class ZombieTargetPerformer() extends Performer.Lead[Point]:
  def id: PerformerId       = ZombieTargetPerformer.id
  def depth: PerformerDepth = PerformerDepth.zero

  val radius = 16

  def initialCollider: Collider[PerformerId] =
    Collider
      .Circle(
        id,
        BoundingCircle(Point.zero.toVertex, radius)
      )
      .makeStatic

  def update(context: PerformerContext[Point]): GlobalEvent => Outcome[Performer.Lead[Point]] =
    _ => Outcome(this)

  def updateCollider(
      context: PerformerContext[Point],
      collider: Collider[PerformerId]
  ): Collider[PerformerId] =
    collider.moveTo(context.frame.input.mouse.position.toVertex)

  def present(
      context: PerformerContext[Point],
      collider: Collider[PerformerId]
  ): Outcome[Batch[SceneNode]] =
    Outcome(
      Batch(
        Shape.Circle(
          Circle(collider.position.toPoint, radius),
          Fill.Color(RGBA.Red),
          Stroke(1, RGBA.White)
        )
      )
    )

object ZombieTargetPerformer:
  val id: PerformerId =
    PerformerId("zombie-target")
