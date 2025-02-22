package indigoexamples

/** ## Setting up a ScrollPane
  */

import indigo.*
import indigo.syntax.*
import indigoextras.ui.*
import indigoextras.ui.syntax.*
import generated.*

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

  val scrollPaneBounds = Bounds(0, 0, 200, 100)

  val text =
    Text(
      "",
      DefaultFont.fontKey,
      Assets.assets.generated.DefaultFontMaterial
    )

  val listOfLabels: ComponentList[Int] =
    ComponentList(Dimensions(200, 200)) { (ctx: UIContext[Int]) =>
      (1 to ctx.reference).toBatch.map { i =>
        ComponentId("lbl" + i) -> Label[Int](
          "Custom label " + i,
          (_, label) => Bounds(0, 0, 250, 20)
        ) { case (ctx, label) =>
          Outcome(
            Layer(
              text
                .withText(label.text(ctx))
                .moveTo(ctx.parent.coords.unsafeToPoint)
            )
          )
        }
      }
    }
      .withLayout(ComponentLayout.Vertical(Padding(10)))

  val scrollButton: Button[Unit] =
    Button[Unit](Bounds(16, 16)) { (ctx, btn) =>
      Outcome(
        Layer(
          Shape
            .Box(
              Rectangle(
                ctx.parent.bounds.unsafeToRectangle.position,
                btn.bounds.dimensions.unsafeToSize
              ),
              Fill.Color(RGBA.Magenta.mix(RGBA.Black)),
              Stroke(1, RGBA.Magenta)
            )
        )
      )
    }
      .presentDown { (ctx, btn) =>
        Outcome(
          Layer(
            Shape
              .Box(
                Rectangle(
                  ctx.parent.bounds.unsafeToRectangle.position,
                  btn.bounds.dimensions.unsafeToSize
                ),
                Fill.Color(RGBA.Cyan.mix(RGBA.Black)),
                Stroke(1, RGBA.Cyan)
              )
          )
        )
      }
      .presentOver((ctx, btn) =>
        Outcome(
          Layer(
            Shape
              .Box(
                Rectangle(
                  ctx.parent.bounds.unsafeToRectangle.position,
                  btn.bounds.dimensions.unsafeToSize
                ),
                Fill.Color(RGBA.Yellow.mix(RGBA.Black)),
                Stroke(1, RGBA.Yellow)
              )
          )
        )
      )

  val pane: ScrollPane[ComponentList[Int], Int] =
    ScrollPane(
      BindingKey("scroll pane"),
      scrollPaneBounds.dimensions,
      listOfLabels,
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

final case class Model(count: Int, component: ScrollPane[ComponentList[Int], Int])
object Model:

  val initial: Model =
    Model(
      4,
      CustomComponents.pane
    )

@JSExportTopLevel("IndigoGame")
object ScrollPaneExample extends IndigoSandbox[Unit, Model]:

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

  /** Rendering a scroll pane is the same as rendering any other component, but so that you can see
    * where the scroll pane is, we're also going to render a shapes around it as a border.
    */
  // ```scala
  def present(context: Context[Unit], model: Model): Outcome[SceneUpdateFragment] =
    val ctx = UIContext(context)
      .withMagnification(2)
      .moveParentBy(Coords(50, 50))
      .copy(reference = model.count)

    val scrollPaneBorder =
      Shape.Box(
        CustomComponents.scrollPaneBounds.unsafeToRectangle.moveTo(ctx.parent.coords.unsafeToPoint),
        Fill.None,
        Stroke(1, RGBA.Cyan)
      )

    model.component
      .present(ctx)
      .map(c => SceneUpdateFragment(c).addLayer(scrollPaneBorder))
  // ```
