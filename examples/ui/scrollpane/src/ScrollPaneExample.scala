package indigoexamples

/** ## Setting up a ScrollPane
  */

import indigo.*
import indigo.syntax.*
import indigoextras.ui.*
import indigoextras.ui.syntax.*
import indigo.shared.subsystems.SubSystemContext.*
import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*

/** Much like the other examples, we need to define our components, and they've been placed in a
  * separate object.
  *
  * As well as the scroll pane, we also need something to put in it. In this case, we're going to
  * make a scroll pane that is half the size of a label, so that you can see the masking in action.
  *
  * We also need a scroll button to show or control (by dragging) how much the pane has been
  * scrolled by.
  *
  * Note that as with all components, there are a few different ways to constuct them. Here we're
  * using fixed bounds / sizes for simplicity, but there are other options.
  */
// ```scala
object CustomComponents:

  val labelBounds = Bounds(0, 0, 300, 70)

  val label: Label[Int] =
    Label[Int](
      "Count: 0",
      (_, label) => labelBounds
    ) { case (offset, label, dimensions) =>
      Outcome(
        Layer(
          TextBox(label)
            .withColor(RGBA.White)
            .moveTo(offset.unsafeToPoint)
            .withSize(dimensions.unsafeToSize)
            .withFontSize(64.pixels)
        )
      )
    }
      .withText((i: Int) => "Count: " + i)

  val scrollButton: Button[Unit] =
    Button[Unit](Bounds(16, 16)) { (coords, bounds, _) =>
      Outcome(
        Layer(
          Shape
            .Box(
              bounds.unsafeToRectangle,
              Fill.Color(RGBA.Magenta.mix(RGBA.Black)),
              Stroke(1, RGBA.Magenta)
            )
            .moveTo(coords.unsafeToPoint)
        )
      )
    }
      .presentDown { (coords, bounds, _) =>
        Outcome(
          Layer(
            Shape
              .Box(
                bounds.unsafeToRectangle,
                Fill.Color(RGBA.Cyan.mix(RGBA.Black)),
                Stroke(1, RGBA.Cyan)
              )
              .moveTo(coords.unsafeToPoint)
          )
        )
      }
      .presentOver((coords, bounds, _) =>
        Outcome(
          Layer(
            Shape
              .Box(
                bounds.unsafeToRectangle,
                Fill.Color(RGBA.Yellow.mix(RGBA.Black)),
                Stroke(1, RGBA.Yellow)
              )
              .moveTo(coords.unsafeToPoint)
          )
        )
      )

  val pane: ScrollPane[Label[Int], Int] =
    ScrollPane(
      BindingKey("scroll pane"),
      labelBounds.dimensions.withHeight(labelBounds.dimensions.height / 2),
      label,
      scrollButton
    )
      .withScrollBackground { bounds =>
        Layer(
          Shape.Box(
            bounds.unsafeToRectangle,
            Fill.Color(RGBA.Yellow.mix(RGBA.Black)),
            Stroke.None
          )
        )
      }
// ```

final case class Model(count: Int, component: ScrollPane[Label[Int], Int])
object Model:

  val initial: Model =
    Model(
      42,
      CustomComponents.pane
    )

@JSExportTopLevel("IndigoGame")
object ScrollPaneExample extends IndigoSandbox[Unit, Model]:

  val config: GameConfig =
    Config.config.noResize

  val assets: Set[AssetType] =
    Assets.assets.assetSet

  val fonts: Set[FontInfo]       = Set()
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
      val ctx = UIContext(context.forSubSystems, Size(1), 1)
        .moveBoundsBy(Coords(50, 50))
        .copy(reference = model.count)

      model.component.update(ctx)(e).map { c =>
        model.copy(component = c)
      }

  /** For the present function, we're going to render something slightly more elaborate than usual.
    *
    * Rendering a scroll pane is the same as rendering any other component, but so that you can see
    * where the label and the mask are, we're also going to render a couple of shapes illustrating
    * their boundaries.
    */
  // ```scala
  def present(context: Context[Unit], model: Model): Outcome[SceneUpdateFragment] =
    val ctx = UIContext(context.forSubSystems, Size(1), 1)
      .moveBoundsBy(Coords(50, 50))
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
        labelBounds.resize(labelBounds.size.width, labelBounds.size.height / 2),
        Fill.None,
        Stroke(1, RGBA.Cyan)
      )

    model.component
      .present(ctx)
      .map(c => SceneUpdateFragment(c).addLayer(labelBorder, maskBorder))
  // ```
