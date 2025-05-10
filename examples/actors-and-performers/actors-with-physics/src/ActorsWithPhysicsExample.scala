package indigoexamples

import indigo.*
import indigo.syntax.*
import indigo.scenes.*
import indigoextras.actors.*
import indigo.physics.*

import generated.*

import scala.scalajs.js.annotation.*

final case class CustomSceneModel(
    spawned: Boolean,
    target: Point,
    actorPool: ActorPool[Map[Int, Point], ZombieActor],
    world: World[ZombieSimTag]
)
object CustomSceneModel:
  val initial: CustomSceneModel =
    CustomSceneModel(
      false,
      Point.zero,
      ActorPool.empty,
      World.empty.withResistance(Resistance(0.25))
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

  val subSystems: Set[SubSystem[Model]] = Set()

  def updateModel(
      context: SceneContext[StartUpData],
      sceneModel: CustomSceneModel
  ): GlobalEvent => Outcome[CustomSceneModel] =
    case FrameTick if !sceneModel.spawned && context.frame.viewport.size != Size.zero =>
      val viewportSize =
        context.frame.viewport.giveDimensions(context.frame.globalMagnification)
        
      val zombies: Batch[(ZombieActor, Collider[ZombieSimTag])] =
        (0 to 30).toBatch.map { i =>
          val dice = Dice.fromSeed(i.toLong)

          val x = dice.roll(viewportSize.width / 2)
          val y = dice.roll(viewportSize.height / 2)

          val acceleration: Double = dice.roll(40) + 10

          val zombie =
            ZombieActor(
              i,
              Point.zero,
              acceleration
            )

          val collider =
            Collider(
              ZombieSimTag.Zombie(i),
              BoundingCircle(x.toDouble, y.toDouble, 5.toDouble)
            )
              .withRestitution(Restitution(0.5))

          (zombie, collider)
        }

      val colliders =
        zombies.map(_._2)

      Outcome(
        sceneModel.copy(
          spawned = true,
          actorPool = zombies.map(_._1).foldLeft(sceneModel.actorPool) { (system, follower) =>
            system.spawn(follower)
          },
          world = sceneModel.world.withColliders(colliders)
        )
      )

    case FrameTick =>

      val target =
        sceneModel.target.toVertex

      sceneModel.world
        .modifyAll { collider =>
          collider match
            case c: Collider.Circle[ZombieSimTag] =>
              c.tag match
                case ZombieSimTag.Zombie(index) =>
                  val toTarget = (target - c.bounds.position).toVector2.normalise
                  val acceleration =
                    sceneModel.actorPool.find(_.index == index).map(_.acceleration).getOrElse(0.0)

                  c.withVelocity(c.velocity + (toTarget * acceleration))

                case ZombieSimTag.Target =>
                  c

            case c =>
              c
        }
        .update(context.frame.time.delta)(
          Collider
            .Circle(ZombieSimTag.Target, BoundingCircle(sceneModel.target.toVertex, 16))
            .makeStatic
        )
        .flatMap { updatedWorld =>

          val positionLookup: Map[Int, Point] =
            updatedWorld.collect { case c @ Collider.Circle(ZombieSimTag.Zombie(index)) =>
              index -> c.bounds.position.toPoint
            }.toMap

          sceneModel.actorPool
            .update(context.context, positionLookup)(FrameTick)
            .map { system =>
              sceneModel.copy(
                target = context.frame.input.mouse.position,
                actorPool = system,
                world = updatedWorld
              )
            }
        }

    case e =>
      sceneModel.actorPool
        .update(context.context, Map.empty)(e)
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
    sceneModel.actorPool.present(context.context, Map.empty).map { zombies =>
      SceneUpdateFragment(
        Layer.Content(
          Shape.Circle(Circle(sceneModel.target, 16), Fill.Color(RGBA.Red), Stroke(2, RGBA.White))
        ),
        Layer.Content(
          zombies
        )
      )
    }

@JSExportTopLevel("IndigoGame")
object ActorsWithPhysicsExample extends IndigoGame[BootData, StartUpData, Model, ViewModel]:

  def scenes(bootData: BootData): NonEmptyList[Scene[StartUpData, Model, ViewModel]] =
    NonEmptyList(CustomScene)

  def initialScene(bootData: BootData): Option[SceneName] =
    Option(CustomScene.name)

  def eventFilters: EventFilters =
    EventFilters.Permissive

  def boot(flags: Map[String, String]): Outcome[BootResult[BootData, Model]] =
    Outcome(
      BootResult(
        Config.config.noResize
          .withClearColor(RGBA(0.4, 0.2, 0.5, 1))
          .withMagnification(2),
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

final case class ZombieActor(index: Int, position: Point, acceleration: Double):
  val depth: Int = 0

  def moveTo(newPosition: Point): ZombieActor =
    this.copy(position = newPosition)

object ZombieActor:

  given Ordering[ZombieActor] =
    Ordering.by(_.depth)

  given Actor[Map[Int, Point], ZombieActor] with

    def update(
        context: ActorContext[Map[Int, Point], ZombieActor],
        actor: ZombieActor
    ): GlobalEvent => Outcome[ZombieActor] =
      case FrameTick =>
        Outcome(
          context.reference
            .get(actor.index)
            .map { pos =>
              actor.moveTo(pos)
            }
            .getOrElse(actor)
        )

      case _ =>
        Outcome(actor)

    def present(
        context: ActorContext[Map[Int, Point], ZombieActor],
        actor: ZombieActor
    ): Outcome[Batch[SceneNode]] =
      val color =
        actor.index % 3 match
          case 0 => RGBA.Cyan
          case 1 => RGBA.Yellow
          case _ => RGBA.SlateGray

      Outcome(
        Batch(
          Shape.Circle(Circle(actor.position, 5), Fill.Color(color), Stroke(1, RGBA.White))
        )
      )

enum ZombieSimTag derives CanEqual:
  case Zombie(index: Int)
  case Target

  def isZombie: Boolean =
    this match
      case Zombie(_) => true
      case Target    => false
