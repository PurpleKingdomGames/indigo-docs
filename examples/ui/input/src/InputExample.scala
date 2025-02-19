package indigoexamples

import indigo.*
import indigoextras.ui.*
import indigoextras.ui.syntax.*

import generated.*

import scala.scalajs.js.annotation.*

object CustomComponents:

  private val text =
    Text("", DefaultFont.fontKey, Assets.assets.generated.DefaultFontMaterial)

  private def drawCursor(ctx: UIContext[Unit], input: Input[Unit]): Batch[SceneNode] =
    val pt =
      ctx.services.bounds
        .get(
          text.withText(input.text.take(input.cursor.position))
        )
        .topRight + Point(0, -4)

    Batch(
      Shape.Box(
        Rectangle(ctx.parent.coords.unsafeToPoint + pt, Size(2, 20)),
        Fill.Color(RGBA.Green)
      )
    )

  private def present: (UIContext[Unit], Input[Unit]) => Outcome[Layer] =
    (ctx, input) =>
      val cursor: Batch[SceneNode] =
        if input.hasFocus then
          input.cursor.blinkRate match
            case None =>
              drawCursor(ctx, input)

            case Some(blinkRate) =>
              Signal
                .Pulse(blinkRate)
                .map(p =>
                  if (ctx.frame.time.running - input.cursor.lastModified < Seconds(0.5)) true else p
                )
                .map {
                  case false =>
                    Batch.empty

                  case true =>
                    drawCursor(ctx, input)
                }
                .at(ctx.frame.time.running)
        else Batch.empty

      val border =
        if input.hasFocus then
          Batch(
            Shape
              .Box(
                ctx.parent.bounds.unsafeToRectangle,
                Fill.None,
                Stroke(1, RGBA.Green)
              )
              .moveTo(ctx.parent.coords.unsafeToPoint)
          )
        else Batch.empty

      Outcome(
        Layer(
          Batch(
            text
              .withText(input.text)
              .moveTo(ctx.parent.coords.unsafeToPoint)
          ) ++ cursor ++ border
        )
      )

  val component: Input[Unit] =
    Input(
      Dimensions(200, 40),
      (ctx: UIContext[Unit], inputText: String) =>
        ctx.services.bounds.get(text.withText(inputText)).width
    )(present)
      .withText("Hello, world!")
      .withCursorBlinkRate(Seconds(0.3))

final case class Model(component: Input[Unit])
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
    Assets.assets.assetSet ++ Assets.assets.generated.assetSet

  val fonts: Set[FontInfo]        = Set(DefaultFont.fontInfo)
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
      val ctx = UIContext(context)
        .moveParentBy(Coords(50, 50))

      model.component.update(ctx)(e).map { c =>
        model.copy(component = c)
      }

  def present(context: Context[Unit], model: Model): Outcome[SceneUpdateFragment] =
    val ctx = UIContext(context)
      .moveParentBy(Coords(50, 50))

    model.component
      .present(ctx)
      .map(l => SceneUpdateFragment(l))
