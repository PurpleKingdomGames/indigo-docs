package indigoexamples

import indigo.*
import indigo.syntax.*
import indigo.scenes.*
import indigoextras.performers.*
import generated.Config
import generated.Assets
import generated.DefaultFont

import scala.scalajs.js.annotation.*

final case class CustomSceneModel(spawned: Boolean)
object CustomSceneModel:
  val initial: CustomSceneModel =
    CustomSceneModel(
      false
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

  val subSystems: Set[SubSystem[Model]] =
    Set(
      StageManager[Model, Unit](
        SubSystemId("performer system"),
        _ => ()
      )
    )

  def updateModel(
      context: SceneContext[StartUpData],
      sceneModel: CustomSceneModel
  ): GlobalEvent => Outcome[CustomSceneModel] =
    case FrameTick if !sceneModel.spawned =>
      Outcome(sceneModel.copy(spawned = true))
        .addGlobalEvents(PerformerEvent.Add(Constants.LayerKeys.player, Player.initial))

    case _ =>
      Outcome(sceneModel)

  def updateViewModel(
      context: SceneContext[StartUpData],
      sceneModel: CustomSceneModel,
      sceneViewModel: ViewModel
  ): GlobalEvent => Outcome[ViewModel] =
    case _ => Outcome(sceneViewModel)

  val message =
    Text(
      "Use arrow keys to move the player.",
      DefaultFont.fontKey,
      Assets.assets.generated.DefaultFontMaterial
    ).scaleBy(Vector2(0.5, 0.5))
      .moveTo(10, 10)

  def present(
      context: SceneContext[StartUpData],
      sceneModel: CustomSceneModel,
      sceneViewModel: ViewModel
  ): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        Constants.LayerKeys.trail ->
          Layer.Stack.empty,
        Constants.LayerKeys.player ->
          Layer.Stack.empty,
        Constants.LayerKeys.message ->
          Layer.Content(
            Signal
              .Pulse(Seconds(0.5))
              .map { show =>
                if show then Batch(message)
                else Batch.empty
              }
              .at(context.frame.time.running)
          )
      )
    )

object Constants:
  object LayerKeys:
    val trail: LayerKey   = LayerKey("trail")
    val player: LayerKey  = LayerKey("player")
    val message: LayerKey = LayerKey("message")

@JSExportTopLevel("IndigoGame")
object PerformersExample extends IndigoGame[BootData, StartUpData, Model, ViewModel]:

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
        .withAssets(Assets.assets.assetSet ++ Assets.assets.generated.assetSet)
        .withFonts(DefaultFont.fontInfo)
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

final case class Player(
    position: Vector2,
    direction: Radians,
    lastDropped: Seconds
) extends Performer.Support[Unit]:

  def id: PerformerId = PerformerId("player")

  def depth: PerformerDepth = PerformerDepth(1000)

  def update(context: PerformerContext[Unit]): GlobalEvent => Outcome[Player] =
    case FrameTick =>
      val moveSpeed   = 2.0
      val rotateSpeed = 0.1

      def rotateLeft: Radians                 = direction - rotateSpeed
      def rotateRight: Radians                = direction + rotateSpeed
      def moveForward(dir: Radians): Vector2  = position + Vector2(0, -1).rotateTo(dir) * moveSpeed
      def moveBackward(dir: Radians): Vector2 = position - Vector2(0, -1).rotateTo(dir) * moveSpeed

      val (newPosition, newDirection) =
        context.frame.input.mapInputs(
          InputMapping(
            Combo.withKeyInputs(Key.ARROW_DOWN, Key.ARROW_LEFT) -> {
              val dir = rotateLeft
              (moveBackward(dir), dir)
            },
            Combo.withKeyInputs(Key.ARROW_DOWN, Key.ARROW_RIGHT) -> {
              val dir = rotateRight
              (moveBackward(dir), dir)
            },
            Combo.withKeyInputs(Key.ARROW_UP, Key.ARROW_LEFT) -> {
              val dir = rotateLeft
              (moveForward(dir), dir)
            },
            Combo.withKeyInputs(Key.ARROW_UP, Key.ARROW_RIGHT) -> {
              val dir = rotateRight
              (moveForward(dir), dir)
            },
            Combo.withKeyInputs(Key.ARROW_LEFT)  -> (position, rotateLeft),
            Combo.withKeyInputs(Key.ARROW_RIGHT) -> (position, rotateRight),
            Combo.withKeyInputs(Key.ARROW_UP)    -> (moveForward(direction), direction),
            Combo.withKeyInputs(Key.ARROW_DOWN)  -> (moveBackward(direction), direction)
          ),
          (position, direction)
        )

      val (maybeBreadcrumb, droppedAt) =
        if context.frame.time.running - lastDropped > Seconds(0.1) then
          (
            Batch(
              Breadcrumb(
                PerformerId("breadcrumb-" + context.frame.dice.rollAlphaNumeric(12)),
                position.toPoint,
                context.frame.time.running
              )
            ),
            context.frame.time.running
          )
        else (Batch.empty, lastDropped)

      Outcome(
        Player(
          newPosition,
          newDirection,
          droppedAt
        )
      ).addGlobalEvents(
        PerformerEvent.AddAll(
          Constants.LayerKeys.trail,
          maybeBreadcrumb
        )
      )

    case _ =>
      Outcome(this)

  val skinColor = RGBA.fromHexString("#eec39a")
  val skinOutline = RGBA.fromHexString("#d9a066")

  def present(context: PerformerContext[Unit]): Outcome[Batch[SceneNode]] =
    Outcome(
      Batch(
        Shape.Circle(Circle(position.toPoint, 22), Fill.Color(skinOutline)),
        Shape.Circle(
          Circle(position.toPoint + (Vector2(0, 1).rotateTo(direction) * 20).toPoint, 8),
          Fill.Color(skinOutline)
        ),
        Shape.Circle(Circle(position.toPoint, 20), Fill.Color(skinColor)),
        Shape.Circle(
          Circle(position.toPoint + (Vector2(0, 1).rotateTo(direction) * 20).toPoint, 6),
          Fill.Color(skinColor)
        ),
        Shape.Circle(
          Circle(position.toPoint + (Vector2(0, 1).rotateTo(direction - 0.5) * 15).toPoint, 6),
          Fill.Color(RGBA.White)
        ),
        Shape.Circle(
          Circle(position.toPoint + (Vector2(0, 1).rotateTo(direction + 0.5) * 15).toPoint, 6),
          Fill.Color(RGBA.White)
        ),
        Shape.Circle(
          Circle(position.toPoint + (Vector2(0, 1).rotateTo(direction - 0.42) * 17).toPoint, 4),
          Fill.Color(RGBA.Black)
        ),
        Shape.Circle(
          Circle(position.toPoint + (Vector2(0, 1).rotateTo(direction + 0.42) * 17).toPoint, 4),
          Fill.Color(RGBA.Black)
        )
      )
    )

object Player:
  val initial: Player =
    Player(Vector2(135, 100), Radians.zero, Seconds.zero)

final case class Breadcrumb(id: PerformerId, position: Point, droppedAt: Seconds)
    extends Performer.Support[Unit]:
  def depth: PerformerDepth = PerformerDepth.zero

  def update(context: PerformerContext[Unit]): GlobalEvent => Outcome[Breadcrumb] =
    case FrameTick if context.frame.time.running - droppedAt > Seconds(2) =>
      Outcome(this).addGlobalEvents(PerformerEvent.Remove(id))

    case _ =>
      Outcome(this)

  def present(context: PerformerContext[Unit]): Outcome[Batch[SceneNode]] =
    Outcome(
      Batch(
        Shape.Circle(
          Circle(position, 2),
          Fill.Color(
            RGBA.White.withAlpha(1.0 - ((context.frame.time.running - droppedAt).toDouble / 2.0))
          )
        )
      )
    )
