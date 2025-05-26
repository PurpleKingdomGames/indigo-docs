package indigoexamples

import indigo.*
import generated.Config
import generated.Assets
import indigoextras.datatypes.IncreaseTo

import scala.scalajs.js.annotation.*

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

@JSExportTopLevel("IndigoGame")
object TimeVaryingValuesExample extends IndigoSandbox[Unit, LumberJack]:

  val config: GameConfig =
    Config.config.noResize
      .withMagnification(2)

  val assets: Set[AssetType] =
    Assets.assets.assetSet

  val fonts: Set[FontInfo]        = Set()
  val animations: Set[Animation]  = Set()
  val shaders: Set[ShaderProgram] = Set()

  def setup(
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[Unit]] =
    Outcome(
      Startup.Success(())
    )

  def initialModel(startupData: Unit): Outcome[LumberJack] =
    Outcome(LumberJack.initial)

  def updateModel(
      context: Context[Unit],
      lumberJack: LumberJack
  ): GlobalEvent => Outcome[LumberJack] =
    case FrameTick =>
      Outcome(lumberJack.update(context.frame.time))

    case _ =>
      Outcome(lumberJack)

  def present(
      context: Context[Unit],
      lumberJack: LumberJack
  ): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        lumberJack.present(
          context.frame.viewport.giveDimensions(context.frame.globalMagnification).center
        )
      )
    )
