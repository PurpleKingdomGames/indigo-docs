package indigoexamples

import indigo.*
import indigoextras.ui.*
import indigoextras.ui.syntax.*

import generated.Config
import generated.Assets

object CustomComponents:

  val component: HitArea[Unit] =
    HitArea[Unit](Bounds(32, 32))
      .onClick(Log("Button clicked"))
      .onPress(Log("Button pressed"))
      .onRelease(Log("Button released"))
      .withStroke(Stroke(1, RGBA.Green))

final case class Model(component: HitArea[Unit])
object Model:

  val initial: Model =
    Model(
      CustomComponents.component
    )

final case class Log(message: String) extends GlobalEvent

class HitAreaExample() extends Game[Unit, Unit, Model]:

  def gameId: GameId = GameId("HitAreaExample")

  def boot(flags: Map[String, String]): Outcome[BootResult[Unit, Model]] =
    Outcome(
      BootResult(Config.config, ())
        .withAssets(Assets.assets.assetSetRelative)
    )

  def initialScene(bootData: Unit): Option[SceneName] = None

  def scenes(bootData: Unit): NonEmptyBatch[Scene[Model]] =
    NonEmptyBatch(Scene.empty)

  def eventFilters: EventFilters = EventFilters.Permissive

  def setup(bootData: Unit, assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Model] =
    Outcome(Model.initial)

  def updateModel(context: Context, model: Model): GlobalEvent => Outcome[Model] =
    case Log(message) =>
      println(message)
      Outcome(model)

    case e =>
      val ctx = UIContext(context, 1)
        .moveParentBy(Coords(50, 50))

      model.component.update(ctx)(e).map { c =>
        model.copy(component = c)
      }

  def present(context: Context, model: Model): Outcome[SceneUpdateFragment] =
    val ctx = UIContext(context, 1)
      .moveParentBy(Coords(50, 50))

    model.component
      .present(ctx)
      .map(l => SceneUpdateFragment(l))
