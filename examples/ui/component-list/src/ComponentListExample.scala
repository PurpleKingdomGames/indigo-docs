package indigoexamples

/** ## How to set up a component list
  *
  * ### Imports
  */

import indigo.*
import indigo.syntax.*

// Before we do anything else, we'll need some additional imports:
// ``` scala
import indigoextras.ui.*
import indigoextras.ui.syntax.*
// ```

import generated.*

import scala.scalajs.js.annotation.*

/** ### Defining our component list
  *
  * We're going to set up a component list of labels. It isn't necessary, but in this example our
  * list will have a dynamic number of elements based on a value in the game's model, passed across
  * as reference data in the `UIContext`.
  *
  * This dynamic layout ability is something `ComponentGroup` cannot do, but reduces the available
  * layout options. So you need to choice the right component type for your needs.
  */
// ``` scala
object CustomComponents:

  val text =
    Text(
      "",
      DefaultFont.fontKey,
      Assets.assets.generated.DefaultFontMaterial
    )

  val listOfLabels: ComponentList[Int] =
    ComponentList(Dimensions(200, 80)) { (ctx: UIContext[Int]) =>
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
// ```

/** ### Setting up the Model
  *
  * The model contains the component list. Component lists define collections of components, and how
  * they should be laid out. They have various options that affect their layout behaviour.
  *
  * Here we initialise our model with the component list.
  */
// ``` scala
final case class Model(numOfLabels: Int, components: ComponentList[Int])
object Model:

  val initial: Model =
    Model(4, CustomComponents.listOfLabels)
// ```

@JSExportTopLevel("IndigoGame")
object ComponentListExample extends IndigoSandbox[Unit, Model]:

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

  /** ### Updating the Model
    *
    * One thing to note is that we need to construct a `UIContext` to pass to the component group.
    * This is important, if a little cumbersome.
    *
    * The `UIContext` holds any custom reference data we might like to propagate down through the
    * component hierarchy, but also information about the grid size the UI is operating on (normally
    * 1x1, this feature is really for use cases like ASCII / terminal UIs) and the magnification of
    * the UI.
    *
    * The `UIContext` also provides the top level position of the component hierarchy, so to move
    * the group and so the button to a new position, we need to tell the `UIContext` to move the
    * bounds by the desired amount using `moveParentBy`.
    *
    * In this example we need to update the context with the model value, and supply it along with
    * the event to the component list's update method.
    */
  // ``` scala
  def updateModel(context: Context[Unit], model: Model): GlobalEvent => Outcome[Model] =
    case e =>
      val ctx = UIContext(context)
        .moveParentBy(Coords(50, 50))
        .copy(reference = model.numOfLabels)

      model.components.update(ctx)(e).map { cl =>
        model.copy(components = cl)
      }
  // ```

  /** ### Presenting the component list
    *
    * The component list knows how to render everything, we just need to call the `present` method
    * with, once again, and instance of UIContext, and provide the results to a SceneUpdateFragment.
    */
  // ``` scala
  def present(context: Context[Unit], model: Model): Outcome[SceneUpdateFragment] =
    val ctx = UIContext(context)
      .moveParentBy(Coords(50, 50))
      .copy(reference = model.numOfLabels)

    model.components
      .present(ctx)
      .map(l => SceneUpdateFragment(l))
  // ```
