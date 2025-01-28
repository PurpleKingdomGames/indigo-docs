package indigoexamples

import indigo.*
import indigo.syntax.*
import indigoextras.ui.*
import indigoextras.ui.syntax.*
import indigo.shared.subsystems.SubSystemContext.*

import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*

object CustomComponents:

  private def drawCursor(offset: Coords, position: Int): Batch[SceneNode] =
    Batch(
      Shape.Box(
        Rectangle(offset.unsafeToPoint + Point(position * 10, 0), Size(2, 20)),
        Fill.Color(RGBA.Green)
      )
    )

  private def present: (Coords, Bounds, Input, Seconds) => Outcome[Layer] =
    (offset, bounds, input, runningTime) =>

      val cursor: Batch[SceneNode] =
        if input.hasFocus then
          input.cursor.blinkRate match
            case None =>
              drawCursor(offset, input.cursor.position)

            case Some(blinkRate) =>
              Signal
                .Pulse(blinkRate)
                .map(p => if (runningTime - input.cursor.lastModified < Seconds(0.5)) true else p)
                .map {
                  case false =>
                    Batch.empty

                  case true =>
                    drawCursor(offset, input.cursor.position)
                }
                .at(runningTime)
        else Batch.empty

      val border =
        if input.hasFocus then
          Batch(
            Shape
              .Box(
                bounds.unsafeToRectangle,
                Fill.None,
                Stroke(1, RGBA.Green)
              )
              .moveTo(offset.unsafeToPoint)
          )
        else Batch.empty

      Outcome(
        Layer(
          Batch(
            TextBox(input.text)
              .withColor(RGBA.White)
              .moveTo(offset.unsafeToPoint)
              .withSize(bounds.dimensions.unsafeToSize)
              .withFontSize(20.pixels)
              .withFontFamily(FontFamily.monospace)
          ) ++ cursor ++ border
        )
      )

  val component: Input =
    Input(Dimensions(200, 40))(present).withText("Hello, world!")

final case class Model(component: Input)
object Model:

  val initial: Model =
    Model(
      CustomComponents.component
    )

final case class Log(message: String) extends GlobalEvent

@JSExportTopLevel("IndigoGame")
object HitAreaExample extends IndigoSandbox[Unit, Model]:

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
      val ctx = UIContext(context.forSubSystems, Size(1), 1)
        .moveBoundsBy(Coords(50, 50))

      model.component.update(ctx)(e).map { c =>
        model.copy(component = c)
      }

  def present(context: Context[Unit], model: Model): Outcome[SceneUpdateFragment] =
    val ctx = UIContext(context.forSubSystems, Size(1), 1)
      .moveBoundsBy(Coords(50, 50))

    model.component
      .present(ctx)
      .map(l => SceneUpdateFragment(l))
