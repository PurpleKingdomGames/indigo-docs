package indigoexamples

import indigo.*
import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*

@JSExportTopLevel("IndigoGame")
object AnimationPart2 extends IndigoSandbox[Unit, Unit]:

  val config: GameConfig =
    Config.config.noResize

  val assets: Set[AssetType] =
    Assets.assets.assetSet

  val fonts: Set[FontInfo]        = Set()
  val animations: Set[Animation]  = Set()
  val shaders: Set[ShaderProgram] = Set()

  def setup(assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Unit] =
    Outcome(())

  def updateModel(context: Context[Unit], model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  val circle =
    Shape.Circle(
      Circle(Point.zero, 25),
      Fill.Color(RGBA.Red),
      Stroke(2, RGBA.White)
    )

  val blueCircle =
    Shape.Circle(
      Circle(Point.zero, 25),
      Fill.Color(RGBA.Blue),
      Stroke(2, RGBA.White)
    )

  def present(context: Context[Unit], model: Unit): Outcome[SceneUpdateFragment] =

    /** ## Encoding our animation as a `Signal`
      *
      * Let's encode our movement animation as a reusable, stateless signal.
      *
      * We're going to limit (clamp) the time to a maximum of 10 seconds, after that, we'll just
      * return the last position.
      *
      * When the running time is supplied: Where the circle ought to be by now is calculated and
      * returned.
      */
// ```scala
    def calculateXPosition(from: Int, to: Int, over: Seconds): Signal[Int] =
      Signal { t =>
        val maxDuration: Double     = over.toDouble
        val clampedTime: Double     = if (t.toDouble > maxDuration) maxDuration else t.toDouble
        val distanceToMove: Double  = to - from
        val pixelsPerSecond: Double = distanceToMove / maxDuration

        from + (pixelsPerSecond * clampedTime).toInt
      }
// ```

    // To use the signal, we need to call its constructor to make it, and then supply the game's running time.
    // ```scala
    val circle1 =
      circle.moveTo(
        calculateXPosition(60, 740, Seconds(10)).at(context.frame.time.running),
        100
      )
    // ```

    /** ## Built-in Signals
      *
      * Indigo has a number of build in Signals. The one we made above is a crude version of a
      * standard signal called `Lerp`.
      *
      * Here are a few moreexamples:
      */

    /** ### Orbit
      *
      * Returns a position in orbit around a given point.
      */
    // ```scala
    val circle2 =
      Signal.Orbit(context.frame.viewport.center + Point(0, 200), 50).map { position =>
        circle.moveTo(position.toPoint)
      }
    // ```

    /** ### SmoothPulse
      *
      * Smoothly oscilates from 0.0 to 1.0 and back again.
      */
    // ```scala
    val circle3 =
      Signal.SmoothPulse.map { amount =>
        circle.moveTo(60 + (680d * amount).toInt, 200)
      }
    // ```

    /** ### Pulse
      *
      * Switches from true to false and back again.
      */
    // ```scala
    val circle4 =
      Signal.Pulse(Seconds(0.5)).map { isTrue =>
        if isTrue then circle.moveTo(60 + 680, 300)
        else circle.moveTo(60, 300)
      }
    // ```

    /** ## Let's do the time warp!
      *
      * To get the value out of a signal, you just need to tell give it a time in seconds, e.g.
      * `signal.at(Seconds(5))`. Where things get interesting is that there is no requirement to
      * give it the "next" time. You can give it any time you like.
      *
      * Examples: Want to play the animation backwards? `signal.at(total time - running time)`
      *
      * Want to play the animation at half speed? `signal.at(running time * 0.5)`
      *
      * Want to jump to random "frames" in the animation? `signal.at(Seconds(dice.roll(10)))`
      *
      * Want to squidge (technical term) backwards and forwards through the animation?
      * `Signal.SmoothPulse.flatMap(signal.at(total time * _))`
      */

    /** ## Signal construction
      *
      * Making signals can get complicated, particularly if you try to wrap up all of the business
      * logic in a single `Signal` definition as we have done so far.
      *
      * Signals are Monads, meaning that many of the usual functions like `map`, `ap`, and `flatMap`
      * that you'd expect to see are available to use, and allow you to use common Scala idioms for
      * their construction, such as for comprehensions.
      *
      * This helps a lot with building signal values, but another useful construct is the
      * `SignalFunction`.
      *
      * Signal functions are covered elsewhere in the docs, but below is a small example. Note that
      * they cover a lot of the same ground as signals themselves, but are more composable.
      */
// ```scala
    val moveWithEasing: SignalFunction[Seconds, Shape.Circle] =
      SignalFunction.easeInOut(Seconds(20)) >>>
        SignalFunction.lerp(Point(60, 60), Point(800 - 60, 600 - 60)) >>>
        SignalFunction { pt =>
          blueCircle.moveTo(pt)
        }

    val circleSignal: Signal[Shape.Circle] =
      Signal.Time |> moveWithEasing
// ```

    Outcome(
      SceneUpdateFragment(
        circle1,
        circle2.at(context.frame.time.running),
        circle3.at(context.frame.time.running),
        circle4.at(context.frame.time.running),
        circleSignal.at(context.frame.time.running)
      )
    )

/** ## Summary
  *
  * Using signals and signal functions provides a great abstraction for animation, much better than
  * coding it up directly. Perfect for simple animations.
  *
  * That said, things could get pretty messy if you were trying to do a complicated animation. What
  * if you were trying to coordinate several moving elements or effects at once, such as in a menu
  * or game over screen?
  *
  * What we need an abstraction for our abstraction, and luckily, one is available. In the next
  * section, we'll look at timelines.
  */
