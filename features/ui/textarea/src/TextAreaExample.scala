package indigoexamples

import indigo.*
import indigoextras.ui.*
import indigoextras.ui.syntax.*

import generated.*

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

class TextAreaExample() extends Game[Unit, Unit, Model]:

  def gameId: GameId = GameId("TextAreaExample")

  def boot(flags: Map[String, String]): Outcome[BootResult[Unit, Model]] =
    Outcome(
      BootResult(Config.config, ())
        .withAssets(
          Assets.assets.assetSetRelative ++
            Assets.assets.generated.assetSetRelative
        )
        .withFonts(DefaultFont.fontInfo)
    )

  def initialScene(bootData: Unit): Option[SceneName] = None

  def scenes(bootData: Unit): NonEmptyBatch[Scene[Unit, Model]] =
    NonEmptyBatch(Scene.empty)

  def eventFilters: EventFilters = EventFilters.Permissive

  def setup(bootData: Unit, assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Model] =
    Outcome(Model.initial)

  def updateModel(context: Context, model: Model): GlobalEvent => Outcome[Model] =
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
