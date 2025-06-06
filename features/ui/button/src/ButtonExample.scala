package indigoexamples

/** ## How to set up a custom button
  *
  * ### Imports
  */

import indigo.*

// Before we do anything else, we'll need some additional imports:
// ``` scala
import indigoextras.ui.*
import indigoextras.ui.syntax.*
// ```

import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*

/** ### Defining a custom button
  *
  * To keep the code nice and tidy, we'll define our custom button in a separate object.
  *
  * The button essentially only has a few elements to it:
  *
  *   1. A reference type - unused here, so set to Unit. The reference data can be anything you
  *      like, and allows you to pass values down through the component hierarchy for reference
  *      during update or interaction.
  *   2. A set of bounds - the size of the button.
  *   3. Presentation functions - define how the button looks in different states. The normal state
  *      is required as part of the button definition, but over and down are optional.
  *   4. Actions - what happens when the button is clicked, pressed, or released. In this example,
  *      we just emit an event telling Indigo to log a message to the console (handling during model
  *      update).
  *
  * To render the button, we're just using shapes, but you could use sprites, text, or anything else
  * you can think of.
  */
// ``` scala
object CustomComponents:

  val customButton: Button[Unit] =
    Button[Unit](Bounds(32, 32)) { (ctx, btn) =>
      Outcome(
        Layer(
          Shape
            .Box(
              btn.bounds.unsafeToRectangle,
              Fill.Color(RGBA.Magenta.mix(RGBA.Black)),
              Stroke(1, RGBA.Magenta)
            )
            .moveTo(ctx.parent.coords.unsafeToPoint)
        )
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
          )
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
          )
        )
      )
      .onClick(Log("Button clicked"))
      .onPress(Log("Button pressed"))
      .onRelease(Log("Button released"))
// ```

final case class Log(message: String) extends GlobalEvent

/** ### Setting up the Model
  *
  * Here we initialise our model with the custom button in it.
  */
// ``` scala
final case class Model(button: Button[Unit])
object Model:

  val initial: Model =
    Model(
      CustomComponents.customButton
    )
// ```

@JSExportTopLevel("IndigoGame")
object ButtonExample extends IndigoSandbox[Unit, Model]:

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
    * We do two things here, first we handle the logging of messages when the button is clicked -
    * nothing fancy. Then we pass all other events to the component group to handle in case it's
    * interested.
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
    */
  // ``` scala
  def updateModel(context: Context[Unit], model: Model): GlobalEvent => Outcome[Model] =
    case Log(message) =>
      println(message)
      Outcome(model)

    case e =>
      val ctx = UIContext(context, 1)
        .moveParentBy(Coords(50, 50))
      // .resizeParentTo(model.button.bounds.dimensions)

      model.button.update(ctx)(e).map { b =>
        model.copy(button = b)
      }
  // ```

  /** ### Presenting the Button
    *
    * To render the button we need to call the `present` method with, once again, and instance of
    * UIContext, and provide the results to a SceneUpdateFragment.
    */
  // ``` scala
  def present(context: Context[Unit], model: Model): Outcome[SceneUpdateFragment] =
    val ctx = UIContext(context, 1)
      .moveParentBy(Coords(50, 50))
    // .resizeParentTo(model.button.bounds.dimensions)

    model.button
      .present(ctx)
      .map(l => SceneUpdateFragment(l))
  // ```
