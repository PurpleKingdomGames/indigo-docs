package indigoexamples

/** ## How to set up a window
  */

import indigo.*
import indigo.syntax.*
import indigoextras.ui.*

import generated.*

import scala.scalajs.js.annotation.*

final case class BootData()
object BootData:
  val empty: BootData =
    BootData()

final case class StartUpData()
object StartUpData:
  val empty: StartUpData =
    StartUpData()

final case class Model(num: Int)
object Model:
  val initial: Model =
    Model(42)

final case class ViewModel()
object ViewModel:
  val initial: ViewModel =
    ViewModel()

/** ### The Window and its components
  */
object CustomUI:

  /** The window itself has only a few properties. It needs a unique ID, the size of the grid the
    * components live on (usually 1, but ASCII art might be a larger grid, for example), the minimum
    * size of the window, and finally the content.
    *
    * The content can be anything that has a `Component` instance for it. In our case we're going to
    * build out some of the usual 'window' functionality, such as a close button. Grouped together,
    * these sorts of components are sometimes called the window's 'chrome'.
    *
    * There are other properties to the window, and below we set a start size and tell it how to
    * draw a background.
    */
  // ```scala
  val windowId: WindowId =
    WindowId("my window")

  val window: Window[ComponentGroup[Int], Int] =
    Window(
      id = windowId,
      snapGrid = Size(1),
      minSize = Dimensions(128, 64),
      content = windowChrome("Hello, Window!")
    )
      .resizeTo(320, 240)
      .withBackground { windowContext =>
        Outcome(
          Layer.Content(
            Shape.Box(
              windowContext.bounds.unsafeToRectangle,
              if windowContext.hasFocus then Fill.Color(RGBA.SlateGray)
              else Fill.Color(RGBA.SlateGray.mix(RGBA.Black)),
              Stroke(1, RGBA.White)
            )
          )
        )

      }
  // ```

  /** The window chrome is a `ComponentGroup` that contains all the components that make up the
    * window. In this case, we have a title bar, a close button, a resize button and the window
    * content itself which starts with a MaskedPane that contains a label.
    *
    * The `ComponentGroup` is a layout component, so it can contain other components and arrange
    * them. In this case, we're using a vertical layout with some padding.
    *
    * Normally, content in a component group is laid out by 'flowing' the components in the order
    * they were added. So in this case we have a vertical layout, and all new content items are
    * added below the last one.
    *
    * However, that isn't what we want for the standard chrome components of a window, for example,
    * we always want the close button in the top right corner, no matter what else is happening. To
    * arrange these special fixed position elements, we use the `anchor` method. This method takes a
    * component and an anchor point. The anchor point is a position on the window where the
    * component will be placed (anchored).
    *
    * Another important point to note is the bounds mode. Here, we're using `BoundsMode.inherit`
    * because this will make this component group 'inherit' the bounds provided to it by it's
    * parent, in this case, the Window instance.
    */
  // ```scala
  def windowChrome(title: String): ComponentGroup[Int] =
    ComponentGroup()
      .withBoundsMode(BoundsMode.inherit)
      .withLayout(ComponentLayout.Vertical(Padding(3, 1, 1, 1)))
      .anchor(
        content,
        Anchor.TopLeft.withPadding(Padding(20, 1, 1, 1))
      )
      .anchor(
        titleBar(title)
          .onDrag { (_: Int, dragData) =>
            Batch(
              WindowEvent
                .Move(
                  windowId,
                  dragData.position - dragData.offset,
                  Space.Screen
                )
            )
          }
          .reportDrag
          .withBoundsType(BoundsType.FillWidth(20, Padding(0))),
        Anchor.TopLeft
      )
      .anchor(
        resizeWindowButton.onDrag { (_: Int, dragData) =>
          Batch(
            WindowEvent
              .Resize(
                windowId,
                dragData.position.toDimensions,
                Space.Screen
              )
          )
        }.reportDrag,
        Anchor.BottomRight
      )
      .anchor(
        closeWindowButton
          .onClick(
            WindowEvent.Close(windowId)
          ),
        Anchor.TopRight
      )
  // ```

  // The chrome components and the content itself have been made in exactly the same way as the components presented in the other examples.

  val text =
    Text(
      "",
      DefaultFont.fontKey,
      Assets.assets.generated.DefaultFontMaterial
    )

  def content: MaskedPane[Label[Int], Int] =
    val label: Label[Int] =
      Label[Int](
        "Count: 0",
        (ctx, label) => Bounds(0, 0, 300, 100)
      ) { case (ctx, label) =>
        Outcome(
          Layer(
            text
              .withText(label.text(ctx))
              .moveTo(ctx.parent.coords.unsafeToPoint)
          )
        )
      }
        .withText((ctx: UIContext[Int]) => "Count: " + ctx.reference)

    MaskedPane(
      BoundsMode.offset(-2, -22),
      label
    )

  def titleBar(title: String): Button[Int] =
    Button[Int](Bounds(Dimensions(0))) { (ctx, btn) =>
      Outcome(
        Layer(
          Shape
            .Box(
              btn.bounds.unsafeToRectangle,
              Fill.Color(RGBA.SlateGray.mix(RGBA.Yellow).mix(RGBA.Black)),
              Stroke(1, RGBA.White)
            )
            .moveTo(ctx.parent.coords.unsafeToPoint),
          text
            .withText(title)
            .moveTo(ctx.parent.coords.unsafeToPoint + Point(4, 2))
        )
      )
    }

  def closeWindowButton: Button[Int] =
    val size = Size(20, 20)

    makeButton(size) { coords =>
      val innerBox = Rectangle(size).contract(4).moveTo(coords + Point(4))

      Batch(
        Shape.Line(innerBox.topLeft, innerBox.bottomRight, Stroke(2, RGBA.Black)),
        Shape.Line(innerBox.bottomLeft, innerBox.topRight, Stroke(2, RGBA.Black))
      )
    }

  def resizeWindowButton: Button[Int] =
    val size = Size(20, 20)

    makeButton(size) { coords =>
      val innerBox = Rectangle(size).contract(4).moveTo(coords + Point(4))

      Batch(
        Shape.Polygon(
          Batch(
            innerBox.bottomLeft,
            innerBox.bottomRight,
            innerBox.topRight
          ),
          Fill.Color(RGBA.Black)
        )
      )
    }

  def makeButton(size: Size)(extraNodes: Point => Batch[SceneNode]): Button[Int] =
    Button[Int](Bounds(Dimensions(size))) { (ctx, btn) =>
      Outcome(
        Layer(
          Shape
            .Box(
              btn.bounds.unsafeToRectangle,
              Fill.Color(RGBA.Magenta.mix(RGBA.Black)),
              Stroke(1, RGBA.Magenta)
            )
            .moveTo(ctx.parent.coords.unsafeToPoint)
        ).addNodes(extraNodes(ctx.parent.coords.unsafeToPoint))
      )
    }
      .presentDown { (ctx, btn) =>
        Outcome(
          Layer(
            Shape
              .Box(
                btn.bounds.unsafeToRectangle,
                Fill.Color(RGBA.Cyan.mix(RGBA.Black)),
                Stroke(1, RGBA.Cyan)
              )
              .moveTo(ctx.parent.coords.unsafeToPoint)
          ).addNodes(extraNodes(ctx.parent.coords.unsafeToPoint))
        )
      }
      .presentOver((ctx, btn) =>
        Outcome(
          Layer(
            Shape
              .Box(
                btn.bounds.unsafeToRectangle,
                Fill.Color(RGBA.Yellow.mix(RGBA.Black)),
                Stroke(1, RGBA.Yellow)
              )
              .moveTo(ctx.parent.coords.unsafeToPoint)
          ).addNodes(extraNodes(ctx.parent.coords.unsafeToPoint))
        )
      )

/** ### The WindowManager
  *
  * To use the manager we need to be able to set up a subsystem, and for that we need at least
  * `IndigoDemo` or `IndigoGame` as our entry point. `IndigoSandbox` is not sufficient.
  *
  * In this basic example, there are only two things we need to do to get the window manager up and
  * running:
  *
  *   1. Set up the boot data correctly with the window manager subsystem;
  *   2. Pre-allocate a layer stack for the windows to be drawn into.
  *
  * In more complex set ups, you would certainly have things like events being sent from your
  * windows and handled in the update function, affecting your main game model, but we haven't done
  * that here.
  */
// ```scala
@JSExportTopLevel("IndigoGame")
object WindowExample extends IndigoDemo[BootData, StartUpData, Model, ViewModel]:
// ````

  def eventFilters: EventFilters =
    EventFilters.Permissive

  /** The boot function is where you set up the initial game configuration. In our example, we need
    * to first ensure we set up the required shaders, and then boot our window manager.
    *
    * By setting up the window manager here, it is a 'global' window manager. If we'd used
    * `IndigoGame` instead of `IndigoDemo`, we could have set up a window manager per scene either
    * in addition or instead of the global one.
    *
    * There are just a few things to draw attention to here with regards to the window manager
    * initialisation:
    *
    *   1. `extractReference`` is a function that takes the model and reads some values out it, that
    *      the windows and there components can then use (as a 'reference').
    *   2. The `startUpData` isn't in use here, but it allows you to set some initialisation data
    *      your windows can make use of.
    *   3. The `layerKey` is important because it tells the window manager where to draw the
    *      windows. See below for more details.
    *
    * With the instance created, we then need to register our windows with it, and at this moment we
    * can also do things like set the window's initial position if we like. This does mean that all
    * windows must be 'known' in advance. There are no dynamically generated windows at this time.
    * Windows can have dynamic _content_ by utilising the `ComponentList` or by making custom
    * components.
    *
    * We can also, optionally, tell the window manager which windows should be immediately open.
    * Anything not in the open list will, naturally, be closed and invisible until you tell the
    * window manager to open them by sending the `WindowEvent.Open` event with the relevant window
    * ID.
    */
  // ```scala
  def boot(flags: Map[String, String]): Outcome[BootResult[BootData, Model]] =
    Outcome(
      BootResult(
        Config.config.noResize,
        BootData.empty
      )
        .withFonts(DefaultFont.fontInfo)
        .withAssets(Assets.assets.assetSet ++ Assets.assets.generated.assetSet)
        .withShaders(indigoextras.ui.shaders.all)
        .withSubSystems(
          WindowManager[Unit, Model, Int](
            id = SubSystemId("window-manager"),
            magnification = 1,
            snapGrid = Size(1),
            extractReference = _.num,
            startUpData = (),
            layerKey = LayerKey("windows")
          )
            .register(CustomUI.window.moveTo(15, 15))
            .open(CustomUI.window.id)
        )
    )
  // ```

  def setup(
      bootData: BootData,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[StartUpData]] =
    Outcome(Startup.Success(StartUpData.empty))

  def initialModel(startupData: StartUpData): Outcome[Model] =
    Outcome(Model.initial)

  def initialViewModel(startupData: StartUpData, model: Model): Outcome[ViewModel] =
    Outcome(ViewModel.initial)

  def updateModel(context: Context[StartUpData], model: Model): GlobalEvent => Outcome[Model] =
    case _ =>
      Outcome(model)

  def updateViewModel(
      context: Context[StartUpData],
      model: Model,
      viewModel: ViewModel
  ): GlobalEvent => Outcome[ViewModel] =
    case _ =>
      Outcome(viewModel)

  /** During the main game presentation we need to provide a placeholder for the windows to be drawn
    * into. By adding this here, we are guaranteeing the placement of the windows in amoung the
    * other game elements.
    *
    * In this example we only have one layer, but consider a more complex set up where you want to
    * be sure of the order, for example:
    *
    *   - Background
    *   - Game elements
    *   - Game UI windows (e.g. inventory, character sheet, etc) which can be paused and greyed out.
    *   - Main menus (e.g. pause menu, game settings, etc)
    */
  // ```scala
  def present(
      context: Context[StartUpData],
      model: Model,
      viewModel: ViewModel
  ): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        LayerKey("windows") -> Layer.Stack.empty
      )
    )
  // ```
