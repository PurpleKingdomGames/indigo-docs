package indigoexamples

import indigo.*
import indigoextras.ui.*
import indigoextras.ui.syntax.*

import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*

object CustomComponents:

  val component: Switch[Unit] =
    Switch[Unit](Bounds(40, 40))(
      (ctx, switch) =>
        Outcome(
          Layer(
            Shape
              .Box(
                switch.bounds.unsafeToRectangle,
                Fill.Color(RGBA.Green.mix(RGBA.Black)),
                Stroke(1, RGBA.Green)
              )
              .moveTo(ctx.parent.coords.unsafeToPoint)
          )
        ),
      (ctx, switch) =>
        Outcome(
          Layer(
            Shape
              .Box(
                switch.bounds.unsafeToRectangle,
                Fill.Color(RGBA.Red.mix(RGBA.Black)),
                Stroke(1, RGBA.Red)
              )
              .moveTo(ctx.parent.coords.unsafeToPoint)
          )
        )
    )
      .onSwitch((ctx, switch) => Batch(Log("Switched to: " + switch.state)))
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
      val ctx = UIContext(context).moveParentBy(Coords(50, 50))

      model.button.update(ctx)(e).map { b =>
        model.copy(button = b)
      }

  def present(context: Context[Unit], model: Model): Outcome[SceneUpdateFragment] =
    val ctx = UIContext(context).moveParentBy(Coords(50, 50))

    model.button
      .present(ctx)
      .map(l => SceneUpdateFragment(l))
