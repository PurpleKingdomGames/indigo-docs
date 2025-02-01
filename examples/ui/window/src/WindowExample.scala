package indigoexamples

import indigo.*
import indigo.syntax.*
import indigoextras.ui.*
import indigo.shared.subsystems.SubSystemContext.*

import generated.Config
import generated.Assets

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

@JSExportTopLevel("IndigoGame")
object WindowExample extends IndigoDemo[BootData, StartUpData, Model, ViewModel]:

  def eventFilters: EventFilters =
    EventFilters.Permissive

  def boot(flags: Map[String, String]): Outcome[BootResult[BootData, Model]] =
    Outcome(
      BootResult(
        Config.config.noResize,
        BootData.empty
      )
        .withAssets(Assets.assets.assetSet)
        .withShaders(indigoextras.ui.shaders.all)
        .withSubSystems(
          WindowManager[Unit, Model, Int](
            id = SubSystemId("window-manager"),
            magnification = 1,
            snapGrid = Size(1),
            extractReference = _.num,
            startUpData = (),
            layerKey = BindingKey("windows")
          )
            .register(CustomUI.window.moveTo(15, 15))
            .open(CustomUI.window.id)
        )
    )

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

  def present(
      context: Context[StartUpData],
      model: Model,
      viewModel: ViewModel
  ): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        BindingKey("windows") -> Layer.Stack.empty
      )
    )

object CustomUI:

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

  def content: MaskedPane[Label[Int], Int] =
    val label: Label[Int] =
      Label[Int](
        "Count: 0",
        (_, label) => Bounds(0, 0, 300, 100)
      ) { case (offset, label, dimensions) =>
        Outcome(
          Layer(
            TextBox(label)
              .withColor(RGBA.White)
              .moveTo(offset.unsafeToPoint)
              .withSize(dimensions.unsafeToSize)
              .withFontSize(50.pixels)
          )
        )
      }
        .withText((i: Int) => "Count: " + i)

    MaskedPane(
      BindingKey("masked pane"),
      BoundsMode.offset(-2, -22),
      label
    )

  def titleBar(title: String): Button[Int] =
    Button[Int](Bounds(Dimensions(0))) { (coords, bounds, _) =>
      Outcome(
        Layer(
          Shape
            .Box(
              bounds.unsafeToRectangle,
              Fill.Color(RGBA.SlateGray.mix(RGBA.Yellow).mix(RGBA.Black)),
              Stroke(1, RGBA.White)
            )
            .moveTo(coords.unsafeToPoint),
          TextBox(title)
            .withColor(RGBA.White)
            .withSize(bounds.unsafeToRectangle.size)
            .withFontSize(12.pixels)
            .moveTo(coords.unsafeToPoint + Point(4, 2))
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
    Button[Int](Bounds(Dimensions(size))) { (coords, bounds, _) =>
      Outcome(
        Layer(
          Shape
            .Box(
              bounds.unsafeToRectangle,
              Fill.Color(RGBA.Magenta.mix(RGBA.Black)),
              Stroke(1, RGBA.Magenta)
            )
            .moveTo(coords.unsafeToPoint)
        ).addNodes(extraNodes(coords.unsafeToPoint))
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
          ).addNodes(extraNodes(coords.unsafeToPoint))
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
          ).addNodes(extraNodes(coords.unsafeToPoint))
        )
      )
