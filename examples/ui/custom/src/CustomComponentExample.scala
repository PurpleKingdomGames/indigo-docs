package indigoexamples

/** ## How to create a custom component
  *
  * To make a custom component we need two things:
  *
  *   1. A data structure representing the state of the component.
  *   2. A `Component` instance that explains how our data structure can be used to describe a
  *      component.
  */

import indigo.*
import indigoextras.ui.*

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

final case class Model()
object Model:
  val initial: Model =
    Model()

final case class ViewModel()
object ViewModel:
  val initial: ViewModel =
    ViewModel()

/** ### Defining our component's behaviour by defining its data model
  *
  * We will create a very simple component that draws a coloured circle in the available space. We
  * infer from the name that we're doing something with a circle, but in terms of data there's only
  * two things we really need:
  *
  *   1. The colour the circle is going to be. This value won't change.
  *   2. The bounds of the circle. This value is a more active bit of state that will change as our
  *      component's parent is resized and the available space is cascaded down to our component.
  */
// ```scala
final case class ColourfulCircle(colour: RGBA, bounds: Bounds)
// ```

/** ### Defining our component's behaviour by defining its `Component` instance
  *
  * We need to define a `Component` instance for our `ColourfulCircle` data structure. This instance
  * will tell Indigo how to:
  *
  *   1. Calculate the bounds of the component.
  *   2. Update the model.
  *   3. Present the component.
  *   4. Refresh the component.
  *
  * The astute reader will notice that what we have here is a mini-TEA architecture. That's right, a
  * component is like a mini indigo game all by itself! It has initial state, a pure function to
  * update the state based on the previous state and an event, and a pure function to present the
  * state.
  *
  * The only things that are perhaps new, are the bounds function and the refresh function. These
  * two functions are essential because the allow the UI system to iterogate the component hierarchy
  * for the bounds of each component in order to do layout calculations, and in turn, to pass new
  * layout information back down to the components.
  *
  * In this example, the bounds is the stored value and we do no updates in response to events. The
  * interesting functions are `present` and `refresh`, where we draw the circle based on the model
  * data, and update the bounds based on the parent, respectively.
  */
// ```scala
object ColourfulCircle:

  given Component[ColourfulCircle, Unit]:
    def bounds(context: UIContext[Unit], model: ColourfulCircle): Bounds =
      model.bounds

    def updateModel(
        context: UIContext[Unit],
        model: ColourfulCircle
    ): GlobalEvent => Outcome[ColourfulCircle] =
      case _ =>
        Outcome(model)

    def present(
        context: UIContext[Unit],
        model: ColourfulCircle
    ): Outcome[Layer] =
      Outcome(
        Layer(
          Shape
            .Circle(
              Circle(
                Point.zero,
                if model.bounds.width < model.bounds.height then model.bounds.width / 2
                else model.bounds.height / 2
              ),
              Fill.Color(model.colour)
            )
            .moveTo(context.parent.bounds.center.unsafeToPoint)
        )
      )

    def refresh(
        context: UIContext[Unit],
        model: ColourfulCircle
    ): ColourfulCircle =
      model.copy(bounds = model.bounds.resize(context.parent.bounds.dimensions))
// ```

object CustomUI:

  val windowId: WindowId =
    WindowId("my window")

  val window: Window[ComponentGroup[Unit], Unit] =
    Window(
      id = windowId,
      snapGrid = Size(1),
      minSize = Dimensions(128, 64),
      content = content
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

  /** ### Using our custom component
    *
    * With our custom component defined, we can now use it by adding it to a `ComponentGroup` (or a
    * `ComponentList`, or even directly to a `Window`). This works, because the `add` function will
    * take _anything_ that has a `given` (AKA `implicit`) instance of a component, which we made
    * earlier.
    */
  // ```scala
  def content: ComponentGroup[Unit] =
    ComponentGroup()
      .withBoundsMode(BoundsMode.inherit)
      .withLayout(ComponentLayout.Horizontal())
      .add(ColourfulCircle(RGBA.Magenta, Bounds.zero))
  // ```

@JSExportTopLevel("IndigoGame")
object CustomComponentExample extends IndigoDemo[BootData, StartUpData, Model, ViewModel]:

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
          WindowManager[Unit, Model, Unit](
            id = SubSystemId("window-manager"),
            magnification = 1,
            snapGrid = Size(1),
            extractReference = _ => (),
            startUpData = (),
            layerKey = LayerKey("windows")
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
        LayerKey("windows") -> Layer.Stack.empty
      )
    )
