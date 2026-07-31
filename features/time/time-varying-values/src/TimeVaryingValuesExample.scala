package indigoexamples

import indigo.*
import generated.Config
import generated.Assets
import indigoextras.datatypes.IncreaseTo

/** ## Tracking a lumberjack's progress
  *
  * You have a lumberjack, he walks over to a tree and is now going to cut it down. Work is effort
  * over time, and you can track his progress by having an `IncreaseTo` time varying value in your
  * model.
  *
  * Here is an oversimplified example. We've made a little lumberjack case class, and all our
  * lumberjack can do is this one job. While the work is being done (which starts immediately), the
  * 'lumberjack' (a circle) will transition from red to green, signaling completion.
  */
// ```scala
final case class LumberJack(chopWood: IncreaseTo):
  def update(gameTime: GameTime): LumberJack =
    this.copy(
      chopWood = chopWood.update(gameTime.delta)
    )

  def present(viewportCenter: Point): SceneNode =
    Shape.Circle(
      Circle(viewportCenter, 50),
      Fill.Color(RGBA.Red.mix(RGBA.Green, chopWood.toDouble / 100.0)),
      Stroke(2, RGBA.White)
    )
object LumberJack:
  val initial: LumberJack =
    LumberJack(
      IncreaseTo(
        value = 0,
        unitsPerSecond = 10,
        limit = 100
      )
    )
// ```

class TimeVaryingValuesExample() extends Game[Unit, Unit, LumberJack]:

  def gameId: GameId = GameId("TimeVaryingValuesExample")

  def boot(flags: Map[String, String]): Outcome[BootResult[Unit, LumberJack]] =
    Outcome(
      BootResult(Config.config, ())
        .withAssets(Assets.assets.assetSetRelative)
    )

  def initialScene(bootData: Unit): Option[SceneName] = None

  def scenes(bootData: Unit): NonEmptyBatch[Scene[LumberJack]] =
    NonEmptyBatch(Scene.empty)

  def eventFilters: EventFilters = EventFilters.Permissive

  def setup(
      bootData: Unit,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[Unit]] =
    Outcome(
      Startup.Success(())
    )

  def initialModel(startupData: Unit): Outcome[LumberJack] =
    Outcome(LumberJack.initial)

  def updateModel(
      context: Context,
      lumberJack: LumberJack
  ): GlobalEvent => Outcome[LumberJack] =
    case FrameTick =>
      Outcome(lumberJack.update(context.frame.time))

    case _ =>
      Outcome(lumberJack)

  def present(
      context: Context,
      lumberJack: LumberJack
  ): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        LayerKey("demo") -> Layer(
          lumberJack.present(
            context.frame.viewport.center / 2
          )
        )
      ).withMagnification(Magnification.x2)
    )
