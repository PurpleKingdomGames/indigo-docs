package indigoexamples

import indigo.*
import indigo.syntax.*
import indigo.scenes.*
import indigoextras.actors.*

import generated.*

import scala.scalajs.js.annotation.*

final case class CustomSceneModel(
    spawned: Boolean,
    target: Point,
    actorPool: ActorPool[Point, FollowingActor]
)
object CustomSceneModel:
  val initial: CustomSceneModel =
    CustomSceneModel(
      false,
      Point.zero,
      ActorPool.empty
    )

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

  val initial: Model =
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

  val subSystems: Set[SubSystem[Model]] = Set()

  def updateModel(
      context: SceneContext[StartUpData],
      sceneModel: CustomSceneModel
  ): GlobalEvent => Outcome[CustomSceneModel] =
    case FrameTick if !sceneModel.spawned =>
      val followers: Batch[FollowingActor] =
        (0 to 10).toBatch.map { i =>
          val dice  = Dice.fromSeed(i.toLong)
          val depth = 10 - i

          if 0 == i % 2 then
            FollowingActor.YellowFollower(
              i,
              depth,
              Vertex.zero,
              dice.roll(12),
              0.1 + (dice.rollDouble * 0.9),
              dice.roll(80)
            )
          else
            FollowingActor.PinkFollower(
              i,
              depth,
              Vertex.zero,
              dice.roll(6),
              0.1 + (dice.rollDouble * 0.9),
              dice.roll(30)
            )
        }

      Outcome(
        sceneModel.copy(
          spawned = true,
          actorPool = followers.foldLeft(sceneModel.actorPool) { (system, follower) =>
            system.spawn(follower)
          }
        )
      )

    case FrameTick =>
      val orbit =
        Signal.SmoothPulse
          .affectTime(2.0)
          .flatMap { d =>
            Signal.Orbit(
              context.frame.viewport.giveDimensions(context.frame.globalMagnification).center,
              (250 * d) + 25
            )
          }
          .at(context.frame.time.running * 0.5)
          .toPoint

      sceneModel.actorPool
        .update(context.context, orbit)(FrameTick)
        .map { system =>
          sceneModel.copy(actorPool = system, target = orbit)
        }

    case e =>
      sceneModel.actorPool
        .update(context.context, sceneModel.target)(e)
        .map { system =>
          sceneModel.copy(actorPool = system)
        }

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
    sceneModel.actorPool.present(context.context, sceneModel.target).map { followers =>
      SceneUpdateFragment(
        Layer.Content(
          Shape.Circle(Circle(sceneModel.target, 8), Fill.Color(RGBA.Cyan))
        ),
        Layer.Content(
          followers
        )
      )
    }

@JSExportTopLevel("IndigoGame")
object ActorsExample extends IndigoGame[BootData, StartUpData, Model, ViewModel]:

  def scenes(bootData: BootData): NonEmptyBatch[Scene[StartUpData, Model, ViewModel]] =
    NonEmptyBatch(CustomScene)

  def initialScene(bootData: BootData): Option[SceneName] =
    Option(CustomScene.name)

  def eventFilters: EventFilters =
    EventFilters.Permissive

  def boot(flags: Map[String, String]): Outcome[BootResult[BootData, Model]] =
    Outcome(
      BootResult(
        Config.config.noResize.withClearColor(RGBA(0.4, 0.2, 0.5, 1)),
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

enum FollowingActor(
    val index: Int,
    val depthIndex: Int,
    val location: Vertex,
    val radius: Int,
    val divisorValue: Double
):
  case PinkFollower(num: Int, depth: Int, position: Vertex, divisor: Int, alpha: Double, size: Int)
      extends FollowingActor(num, depth, position, size, divisor.toDouble)

  case YellowFollower(
      num: Int,
      depth: Int,
      position: Vertex,
      divisor: Int,
      alpha: Double,
      size: Int
  ) extends FollowingActor(num, depth, position, size, divisor.toDouble)

  def moveTo(newPosition: Vertex): FollowingActor =
    this match
      case f: FollowingActor.PinkFollower =>
        f.copy(position = newPosition)

      case f: FollowingActor.YellowFollower =>
        f.copy(position = newPosition)

  def colour: RGBA =
    this match
      case FollowingActor.PinkFollower(_, _, _, _, alpha, _) =>
        RGBA.Magenta.withAlpha(alpha)

      case FollowingActor.YellowFollower(_, _, _, _, alpha, _) =>
        RGBA.Yellow.withAlpha(alpha)

object FollowingActor:

  given Ordering[FollowingActor] =
    Ordering.by(_.depthIndex)

  given Actor[Point, FollowingActor] with

    def update(
        context: ActorContext[Point, FollowingActor],
        actor: FollowingActor
    ): GlobalEvent => Outcome[FollowingActor] =
      case FrameTick =>
        val target =
          context.find(_.index == actor.index - 1) match
            case Some(follower) =>
              follower.location

            case None =>
              context.reference.toVertex

        Outcome(
          actor.moveTo(actor.location + ((target - actor.location) / actor.divisorValue))
        )

      case _ =>
        Outcome(actor)

    def present(
        context: ActorContext[Point, FollowingActor],
        actor: FollowingActor
    ): Outcome[Batch[SceneNode]] =
      Outcome(
        Batch(
          Shape.Circle(
            Circle(actor.location.toPoint, actor.radius),
            Fill.Color(actor.colour),
            Stroke(2, RGBA.Black)
          )
        )
      )
