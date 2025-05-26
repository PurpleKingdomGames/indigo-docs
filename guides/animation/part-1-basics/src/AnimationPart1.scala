package indigoexamples

import indigo.*
import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*

/** ## Example of procedural animation
  *
  * Let's look at a real example and illustrate some of the problems a challenges we need to
  * overcome.
  *
  * In this example we will simply move a red circle across the screen from left to right, using the
  * frame delta to ensure a constant speed.
  *
  * How hard could it be?
  */

/** The first thing we need to do is model some state. State?! Oh yes. In animation you either need
  * to know the current state, or write some clever code that can fully work on the current
  * animation position based only on the initial conditions and the running time. More on that
  * later, for now, we're going to update a known position.
  *
  * The position is wrapped in a model case class in a nod to realism, but curiously, the position
  * is modelled as a `Vertex`, which takes `Double`s, rather that a `Point` which uses `Int` as
  * its unit type. What is going on? `Point` would seem to be the obvious choice, since we're
  * moving across the screen in pixels, but let's do the math:
  *
  *   1. We want to move across the screen at 50 pixels per second, and our game runs at 60 frames
  *      per second.
  *   1. 60 FPS is on average 16.667 milliseconds per frame, but we need to convert that to seconds,
  *      so: 0.016667.
  *   1. 50 pixels per second * 0.016667 frame delta = 0.83335.
  *
  * So we need to move our circle 0.83335 pixels every frame, on average.
  *
  * If conversion of `Double` to `Int` _rounded_ the value then we'd jitter and stutter across the
  * screen - sometimes moving a bit, sometimes not at all.
  *
  * Actually, conversion of `Double` to `Int` _floors_ the value, meaning `0`, so we never move...
  * Oh dear!
  *
  * By modelling the position as a type based on `Double` and converting to pixels at the last
  * moment during presentation, we can avoid all these difficulties.
  */
// ```scala
final case class Model(position: Vertex)
// ```

@JSExportTopLevel("IndigoGame")
object AnimationPart1 extends IndigoSandbox[Unit, Model]:

  val config: GameConfig =
    Config.config.noResize

  val assets: Set[AssetType] =
    Assets.assets.assetSet

  val fonts: Set[FontInfo]        = Set()
  val animations: Set[Animation]  = Set()
  val shaders: Set[ShaderProgram] = Set()

  def setup(assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  // We need to initialise our state with some acceptable values.
  // ```scala
  def initialModel(startupData: Unit): Outcome[Model] =
    Outcome(Model(Vertex(60, 120)))
  // ```

  // During update we do our now familiar bit of maths, multiplying the speed by the frame delta
  // ```scala
  def updateModel(context: Context[Unit], model: Model): GlobalEvent => Outcome[Model] =
    _ =>
      val pixelsPerSecond = 50

      Outcome(
        model.copy(
          position = model.position.withX(
            model.position.x + (pixelsPerSecond * context.frame.time.delta.toDouble)
          )
        )
      )
  // ```

  // When we draw the circle, we simply move it to the position held in the model.
  // ```scala
  def present(context: Context[Unit], model: Model): Outcome[SceneUpdateFragment] =
    val circle =
      Shape.Circle(
        Circle(Point.zero, 50),
        Fill.Color(RGBA.Red),
        Stroke(2, RGBA.White)
      )

    Outcome(
      SceneUpdateFragment(
        circle.moveTo(model.position.toPoint)
      )
    )
  // ```

// Gosh that was hard work! Imagine trying to do that for every moving thing on the screen! There must be another way, surely?
