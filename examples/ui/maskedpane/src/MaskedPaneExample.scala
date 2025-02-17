package indigoexamples

/** ## Setting up a MaskedPane
  */

import indigo.*
import indigoextras.ui.*
import indigoextras.ui.syntax.*
import generated.*

import scala.scalajs.js.annotation.*

/** Much like the other examples, we need to define our components, and they've been placed in a
  * separate object.
  *
  * As well as the masked pane, we also need something to put in it. In this case, we're going to
  * make a masked pane that is smaller than the label, so that you can see the masking in action. To
  * do that, we're going to use fixed sizes for the label and the mask, which isn't what you'd
  * normally do.
  */
// ```scala
object CustomComponents:

  val labelBounds = Bounds(0, 0, 100, 16)

  val text =
    Text(
      "",
      DefaultFont.fontKey,
      Assets.assets.generated.DefaultFontMaterial
    )

  val label: Label[Int] =
    Label[Int](
      "",
      (ctx, label: String) => labelBounds
    ) { case (ctx, label) =>
      Outcome(
        Layer(
          text
            .withText(label.text(ctx))
            .moveTo(ctx.parent.coords.unsafeToPoint)
        )
      )
    }
      .withText(ctx => "Count: " + ctx.reference)

  val pane: MaskedPane[Label[Int], Int] =
    MaskedPane(
      labelBounds.dimensions / 2,
      label
    )
// ```

final case class Model(count: Int, component: MaskedPane[Label[Int], Int])
object Model:

  val initial: Model =
    Model(
      42,
      CustomComponents.pane
    )

@JSExportTopLevel("IndigoGame")
object MaskedPaneExample extends IndigoSandbox[Unit, Model]:

  val config: GameConfig =
    Config.config.noResize.withMagnification(2)

  val assets: Set[AssetType] =
    Assets.assets.assetSet ++ Assets.assets.generated.assetSet

  val fonts: Set[FontInfo]       = Set(DefaultFont.fontInfo)
  val animations: Set[Animation] = Set()

  /** When using masked and scroll panes, you need to remember to register the shaders they use. The
    * `all` import is conveniently a `Set`, so you can just concatenate it onto any other shaders
    * you are using.
    */
  // ```scala
  val shaders: Set[ShaderProgram] =
    Set() ++ indigoextras.ui.shaders.all
  // ```

  def setup(assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Model] =
    Outcome(Model.initial)

  def updateModel(context: Context[Unit], model: Model): GlobalEvent => Outcome[Model] =
    case e =>
      val ctx = UIContext(context)
        .withMagnification(2)
        .moveParentBy(Coords(50, 50))
        .copy(reference = model.count)

      model.component.update(ctx)(e).map { c =>
        model.copy(component = c)
      }

  /** For the present function, we're going to render something slightly more elaborate than usual.
    *
    * Rendering a masked pane is the same as rendering any other component, but so that you can see
    * where the label and the mask are, we're also going to render a couple of shapes illustrating
    * their boundaries.
    */
  // ```scala
  def present(context: Context[Unit], model: Model): Outcome[SceneUpdateFragment] =
    val ctx = UIContext(context)
      .withMagnification(2)
      .moveParentBy(Coords(50, 50))
      .copy(reference = model.count)

    val labelBounds =
      CustomComponents.labelBounds.unsafeToRectangle.moveBy(50, 50)

    val labelBorder =
      Shape.Box(
        labelBounds,
        Fill.None,
        Stroke(1, RGBA.Green)
      )

    val maskBorder =
      Shape.Box(
        labelBounds.resize(labelBounds.size / 2),
        Fill.None,
        Stroke(1, RGBA.Cyan)
      )

    model.component
      .present(ctx)
      .map(c => SceneUpdateFragment(c).addLayer(labelBorder, maskBorder))
  // ```
