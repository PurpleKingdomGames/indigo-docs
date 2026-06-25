package indigoexamples

import indigo.*
import indigo.syntax.*
import indigo.syntax.animations.*
import generated.Config
import generated.Assets

class AnimationPart3() extends Game[Unit, Unit, Unit]:

  def gameId: GameId = GameId("AnimationPart3")

  def boot(flags: Map[String, String]): Outcome[BootResult[Unit, Unit]] =
    Outcome(
      BootResult(Config.config, ())
        .withAssets(Assets.assets.assetSetRelative)
    )

  def initialScene(bootData: Unit): Option[SceneName] = None

  def scenes(bootData: Unit): NonEmptyBatch[Scene[Unit]] =
    NonEmptyBatch(Scene.empty)

  def eventFilters: EventFilters = EventFilters.Permissive

  def setup(bootData: Unit, assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Unit] =
    Outcome(())

  def updateModel(context: Context, model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  val circle =
    Shape.Circle(
      Circle(Point.zero, 25),
      Fill.Color(RGBA.Red),
      Stroke(2, RGBA.White)
    )

  /** ## How to make a timeline animation
    *
    * In this animation, we have two layers.
    *
    * The first layer initially waits 2 seconds. Then over the next 5 seconds, it calculates a
    * points position diagonally (lerp means linear interpolation) from one corner of the viewport
    * to the other, and finally moves a circle to that position. All of this is performed using an
    * 'ease-in-out' function that accelerates the movement up initially and slows it down towards
    * the end.
    *
    * The second layer also waits 2 seconds for consistency, then fades the circles fill color in,
    * over time.
    *
    * The function inside the `animate` block is built up using `SignalFunction`s (see below) to
    * describe the value transformation that results in the animated movement. There are lots of
    * helpful signal functions available on the `SignalFunction` companion object for you to make
    * use of.
    */
  // ```scala
  def myTimelineAnimation(viewportSize: Size): Timeline[Shape.Circle] =
    timeline(
      layer(
        startAfter(2.seconds),
        animate(10.seconds) { circle =>
          easeInOut >>>
            lerp(Point(60), viewportSize.toPoint - Point(60)) >>>
            SignalFunction(pt => circle.moveTo(pt))
        }
      ),
      layer(
        startAfter(2.seconds),
        animate(10.seconds) { circle =>
          lerp >>>
            SignalFunction { alpha =>
              circle.withFill(Fill.Color(RGBA.Green.withAlpha(alpha)))
            }
        }
      )
    )

  def present(context: Context, model: Unit): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        myTimelineAnimation(context.frame.viewport.size)
          .atOrLast(context.frame.time.running)(circle)
          .toBatch
      )
    )
  // ```

/** ## Summary
  *
  * Designed to ease the production of coordinated animations for menu and game over screens,
  * timelines provide a powerful abstraction over signals and signal functions, to allow you to
  * describe complicated animations and state transitions, with many moving parts.
  *
  * Of course, all of this abstraction comes at a cost, namely: Allocations. While Timeline
  * animations are great for a few complicated situations, they are not well suited to cases where
  * you need many instances of small animations. For those, signals, imported animations and even
  * handrolled animation code can offer a better performance vs ease of use trade off.
  *
  * ...but what is the lightest, fastest, most performant animation technique of all?
  *
  * In part 4 we'll answer that question by diving into shaders!
  */
