package indigoexamples

import indigo.*
import indigoextras.ui.*
import indigoextras.ui.syntax.*
import indigo.shared.subsystems.SubSystemContext.*

import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*

object CustomComponents:

  val component: Switch[Unit] =
    Switch[Unit, Unit](BoundsType.fixed(40, 40))(
      (coords, bounds, _) =>
        Outcome(
          Layer(
            Shape
              .Box(
                bounds.unsafeToRectangle,
                Fill.Color(RGBA.Green.mix(RGBA.Black)),
                Stroke(1, RGBA.Green)
              )
              .moveTo(coords.unsafeToPoint)
          )
        ),
      (coords, bounds, _) =>
        Outcome(
          Layer(
            Shape
              .Box(
                bounds.unsafeToRectangle,
                Fill.Color(RGBA.Red.mix(RGBA.Black)),
                Stroke(1, RGBA.Red)
              )
              .moveTo(coords.unsafeToPoint)
          )
        )
    )
      .onSwitch(value => Batch(Log("Switched to: " + value)))
      .switchOn

final case class Log(message: String) extends GlobalEvent

final case class Model(button: Switch[Unit])
object Model:

  val initial: Model =
    Model(
      CustomComponents.component
    )

@JSExportTopLevel("IndigoGame")
object SwitchExample extends IndigoSandbox[Unit, Model]:

  val config: GameConfig =
    Config.config.noResize

  val assets: Set[AssetType] =
    Assets.assets.assetSet

  val fonts: Set[FontInfo]        = Set()
  val animations: Set[Animation]  = Set()
  val shaders: Set[ShaderProgram] = Set()

  def setup(assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Model] =
    Outcome(Model.initial)

  def updateModel(context: Context[Unit], model: Model): GlobalEvent => Outcome[Model] =
    case Log(message) =>
      println(message)
      Outcome(model)

    case e =>
      val ctx = UIContext(context.forSubSystems, Size(1), 1).moveBoundsBy(Coords(50, 50))

      model.button.update(ctx)(e).map { b =>
        model.copy(button = b)
      }

  def present(context: Context[Unit], model: Model): Outcome[SceneUpdateFragment] =
    val ctx = UIContext(context.forSubSystems, Size(1), 1).moveBoundsBy(Coords(50, 50))

    model.button
      .present(ctx)
      .map(l => SceneUpdateFragment(l))
