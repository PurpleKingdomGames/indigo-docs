package indigoexamples

import indigo.*
import indigo.syntax.*
import indigo.physics.*
import generated.Config
import generated.Assets
import generated.DefaultFont

import scala.scalajs.js.annotation.*

/** ## How to set up a simple physics simulation
  *
  * This example shows how to set up and run a simple physics simulation in Indigo, and render the
  * result.
  */

/** ### Step 1: Define a tag for each type of object in the simulation
  *
  * Indigo's physics engine uses tags to identify different types of objects in the simulation. They
  * are custom tags that you define, and can be any type, even a simple String, but an ADT / enum is
  * recommended.
  *
  * Tags are used to identify objects in the simulations, such as how they interact with one
  * another, e.g. objects of tag x do not collide with objects of tag y, and also to help you when
  * it comes to rendering and querying the simulation to find out what happened.
  *
  * In this example, we define two tags: `StaticCircle` and `Ball`. Static circles just sit there
  * and balls are bouncy and subject to gravity.
  */
// ```scala
enum MyTag:
  case StaticCircle
  case Ball
// ```

/** ### Step 2: Define a model for the simulation
  *
  * This is the model for our game, and in this case mostly just holds the `World` instance.
  *
  * The `World` is the physics simulation itself, and is where all the magic happens. The key thing
  * to appreciate about the World is that unlike in other game engines, the World is not mixed up
  * with the rest of the model or any of the view code. It is an entirely self-contained simultation
  * of 'colliders'.
  *
  * When you want to render something in the world, you query the world for the colliders you are
  * interested in and then render them however you like. That isn't quite what we'll do in this
  * demo, as the world also has a `present` method that can use to render everything in the
  * simulatation. Generally using the world `present` isn't the intended use, but it is very useful
  * for debugging since it allows you to 'see' where all the colliders are in relation to your
  * normal view elements.
  *
  * A quick note on the 'running' Boolean. This isn't just convenient, it serves a practical purpose
  * in this demo. Normally, you wouldn't be running a simulation the instant you start a game, you'd
  * probably at least show a title screen. Since we're going straight into it, the game will 'start'
  * before everything is really, and it will start with an initially volatile framerate, throwing
  * the simulation off. To get around this, we'll pause the simulation until the user presses SPACE.
  */
// ```scala
final case class Model(running: Boolean, world: World[MyTag])
// ```

/** We'll initialise the model and the world in the companion object for the model.
  *
  * The world is initialised in an empty state with a given size. We then add some forces to
  * simulate gravity in "units per second", units in this case being pixels. The simulation does not
  * care what scale you want to model it at, and often it helps to use a 'world space' that suits
  * you domain model. In this case we've made the size the same as the game screen size, so the
  * units are 1:1 with pixels, which makes rendering simpler.
  *
  * We also add some resistance to the simulation, which will slow down the balls over time but
  * causing them to globally lose energy.
  *
  * Elements within the simulation are known as 'colliders'. Indigo only supports two types of
  * collider at present: circles and boxes, and both are 'axis aligned', meaning they do not rotate.
  * Limited, but good enough for many games, we hope to expand on this later.
  *
  * Colliders have many properties, but the two we care about here are `makeStatic` and restitution.
  *
  * `makeStatic` is a method that tells the simulation that the collider does not move, and that no
  * calculation is required for it. The obvious use in a video game is for walls and floors, but you
  * can also use it for elements that, for example, a user is dragging / moving that affect the
  * world.
  *
  * Restitution determines how bouncy an object is. A value of 1.0 means the object will bounce back
  * perfectly, here we've set it to 0.8 so that it's bouncy, but loses energy with each impact.
  */
// ```scala
object Model:

  def initial: Model =
    Model(false, world)

  def world: World[MyTag] =
    val staticCircles =
      (0 to 8).toBatch.map { i =>
        Collider(
          MyTag.StaticCircle,
          BoundingCircle(i.toDouble * 100 + 30, 200.0, 20.0)
        ).makeStatic
      }

    val balls =
      (0 to 15).toBatch.map { i =>
        Collider(
          MyTag.Ball,
          BoundingCircle(
            i.toDouble * 50 + 28,
            (if i % 2 == 0 then 20 else 40) + i.toDouble * 2,
            15.0
          )
        )
          .withRestitution(Restitution(0.8))
      }

    World
      .empty[MyTag]
      .addForces(Vector2(0, 600))
      .withResistance(Resistance(0.01))
      .withColliders(staticCircles)
      .addColliders(
        Collider(MyTag.StaticCircle, BoundingCircle(-100.0d, 700.0, 300.0)).makeStatic,
        Collider(MyTag.StaticCircle, BoundingCircle(900.0d, 700.0, 300.0)).makeStatic,
        Collider(MyTag.StaticCircle, BoundingCircle(400.0d, 800.0, 300.0)).makeStatic
      )
      .addColliders(balls)
// ```

/** ### Step 3: Defining the rest of the game.
  */
@JSExportTopLevel("IndigoGame")
object BasicPhysicsExample extends IndigoSandbox[Unit, Model]:

  val config: GameConfig =
    Config.config.noResize

  val assets: Set[AssetType] =
    Assets.assets.assetSet ++ Assets.assets.generated.assetSet

  val fonts: Set[FontInfo]        = Set(DefaultFont.fontInfo)
  val animations: Set[Animation]  = Set()
  val shaders: Set[ShaderProgram] = Set()

  def setup(assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  // We'll need to actually initialise our model.
  // ```scala
  def initialModel(startupData: Unit): Outcome[Model] =
    Outcome(Model.initial)
  // ```

  /** Then during update, if the game is running, update the simulation by supplying the frame delta
    * time, and updating the model with the new version of the world.
    */
  // ```scala
  def updateModel(context: Context[Unit], model: Model): GlobalEvent => Outcome[Model] =
    case FrameTick if model.running =>
      model.world
        .update(context.frame.time.delta)
        .map(updated => model.copy(world = updated))

    case KeyboardEvent.KeyUp(Key.SPACE) =>
      Outcome(model.copy(running = !model.running))

    case _ =>
      Outcome(model)
  // ```

  val message =
    Text(
      "Press SPACE to pause/unpause",
      DefaultFont.fontKey,
      Assets.assets.generated.DefaultFontMaterial
    )

  /** Finally we'll present the simulation using the `world.present` function, which will
    * conveniently allow us to render all the colliders in the simulation. In this case we'll
    * produce a `Batch` of `Shapes` for ease.
    *
    * Below that, the flashing text is created using a `Signal` that pulses every second and renders
    * the text, or not.
    */
  // ```scala
  def present(context: Context[Unit], model: Model): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        model.world.present {
          case c: Collider.Circle[_] =>
            Shape.Circle(
              c.bounds.position.toPoint,
              c.bounds.radius.toInt,
              Fill.Color(RGBA.White.withAlpha(0.2)),
              Stroke(1, RGBA.White)
            )

          case c: Collider.Box[_] =>
            Shape.Box(
              c.bounds.toRectangle,
              Fill.Color(RGBA.White.withAlpha(0.2)),
              Stroke(1, RGBA.White)
            )
        } ++
          Signal
            .Pulse(1.seconds)
            .map { show =>
              if show then
                val bounds = context.services.bounds.find(message).getOrElse(Rectangle.zero)

                Batch(
                  message.moveTo((800 - bounds.width) / 2, 600 - bounds.height - 10)
                )
              else Batch.empty
            }
            .at(context.frame.time.running)
      )
    )
  // ```
