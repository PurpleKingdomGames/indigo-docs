package indigoexamples

/** ## How to set up a custom label
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

// And until issue [#814](https://github.com/PurpleKingdomGames/indigo/issues/814) is resolved, we'll also need this import for convenience:
// ``` scala
import indigo.shared.subsystems.SubSystemContext.*
// ```

import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*

/** ### Defining a custom label
  *
  * To keep the code nice and tidy, we'll define our custom label in a separate object.
  *
  * To render the label we're going to use a TextBox. TextBox is not a great choice in practice,
  * because it is relatively expensive, and to calculate the bounds for dynamic text we'll need to
  * find a way to supply a `Context.Services.Bounds` instance. But for this demo, it'll do.
  *
  * Text in labels can be static, or dynamic based on the 'reference' data in the `UIContext`, as in
  * the example below.
  */
// ``` scala
object CustomComponents:

  val customLabel: Label[Int] =
    Label[Int](
      "Count: 0",
      (_, label) => Bounds(0, 0, 150, 20)
    ) { case (offset, label, dimensions) =>
      Outcome(
        Layer(
          TextBox(label)
            .withColor(RGBA.White)
            .moveTo(offset.unsafeToPoint)
            .withSize(dimensions.unsafeToSize)
            .withFontSize(20.pixels)
        )
      )
    }
      .withText((i: Int) => "Count: " + i)
// ```

/** ### Setting up the Model
  *
  * Here we initialise our model with our label, and the 'count', which will be injected as the
  * reference data, later.
  */
// ``` scala
final case class Model(count: Int, label: Label[Int])
object Model:

  val initial: Model =
    Model(
      42,
      CustomComponents.customLabel
    )
// ```

@JSExportTopLevel("IndigoGame")
object LabelExample extends IndigoSandbox[Unit, Model]:

  val config: GameConfig =
    Config.config.noResize

  val assets: Set[AssetType] =
    Assets.assets.assetSet

  val fonts: Set[FontInfo]        = Set()
  val animations: Set[Animation]  = Set()
  val shaders: Set[ShaderProgram] = Set()

  def setup(assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Model] =
    Outcome(Model.initial)

  /** ### Updating the Model
    *
    * We need to construct a `UIContext` to pass to the component group, and then we can update the
    * label by supply it with the context and the event.
    */
  // ``` scala
  def updateModel(context: Context[Unit], model: Model): GlobalEvent => Outcome[Model] =
    case e =>
      val ctx = UIContext(context.forSubSystems, Size(1), 1)
        .moveBoundsBy(Coords(50, 50))
        .copy(reference = model.count)

      model.label.update(ctx)(e).map { l =>
        model.copy(label = l)
      }
  // ```

  /** ### Presenting the Label
    *
    * We need to call the `present` method with, once again, and instance of UIContext, and provide
    * the results to a SceneUpdateFragment.
    */
  // ``` scala
  def present(context: Context[Unit], model: Model): Outcome[SceneUpdateFragment] =
    val ctx = UIContext(context.forSubSystems, Size(1), 1)
      .moveBoundsBy(Coords(50, 50))
      .copy(reference = model.count)

    model.label
      .present(ctx)
      .map(l => SceneUpdateFragment(l))
  // ```
