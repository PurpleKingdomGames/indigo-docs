package indigoexamples

import indigo.*
import indigoextras.ui.*
import indigoextras.ui.syntax.*

import generated.*

import scala.scalajs.js.annotation.*

object CustomComponents:

  val text =
    Text(
      "",
      DefaultFont.fontKey,
      Assets.assets.generated.DefaultFontMaterial
    )

  val component: TextArea[Unit] =
    TextArea[Unit](
      "This is just,\nsome text.",
      (ctx, txt) => Bounds(ctx.services.bounds.get(text.withText(txt)))
    ) { (ctx, textArea) =>
      Outcome(
        Layer(
          text
            .withText(textArea.text(ctx))
            .moveTo(ctx.parent.coords.unsafeToPoint),
          Shape
            .Box(
              textArea.bounds(ctx).unsafeToRectangle,
              Fill.None,
              Stroke(1, RGBA.Green)
            )
            .moveTo(ctx.parent.coords.unsafeToPoint)
        )
      )
    }

final case class Model(component: TextArea[Unit])
object Model:

  val initial: Model =
    Model(CustomComponents.component)

@JSExportTopLevel("IndigoGame")
object TextAreaExample extends IndigoSandbox[Unit, Model]:

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
