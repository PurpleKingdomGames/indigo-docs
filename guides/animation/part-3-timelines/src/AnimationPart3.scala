package indigoexamples

import indigo.*
import indigo.syntax.*
import indigo.syntax.animations.*
import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*

@JSExportTopLevel("IndigoGame")
object AnimationPart3 extends IndigoSandbox[Unit, Unit]:

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

  def present(context: Context[Unit], model: Unit): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        myTimelineAnimation(context.frame.viewport.bounds.size)
          .atOrLast(context.frame.time.running)(circle)
          .toBatch
      )
    )
  // ```
