package indigoexamples

import indigo.*
import indigo.syntax.*
import generated.Config
import generated.Assets
import ultraviolet.syntax.*

import scala.scalajs.js.annotation.*

/** ## Animating with shaders
  *
  * In this example we're going to write a shader for a little bouncing yellow box, the kind that
  * could be used to show the currently highlighted grid square in a grid based rpg or strategy
  * game.
  *
  * Let's go through the code:
  */

object CustomShader:

  val shader: ShaderProgram =
    UltravioletShader.entityFragment(
      ShaderId("custom shader"),
      EntityShader.fragment[FragmentEnv](fragment, FragmentEnv.reference)
    )

  inline def fragment: Shader[FragmentEnv, Unit] =
    Shader[FragmentEnv] { env =>
      import ultraviolet.sdf.*

      def sdBox: (vec2, vec2) => Float =
        (p, b) => box(p, b)

      def fragment(color: vec4): vec4 =

        /** As usual, animation is about the running time of the game, and in our case we need to
          * speed time up in order to get the bounce speed we're after.
          */
        // ```scala
        val time: Float = env.TIME * 4.0f
        // ```

        /** Next we're going to set up some constants / values / parameters. Yes, in this example
          * these are hardcoded values. On the other hand, they're also relative values (shaders
          * operate in 0.0 to 1.0 coordinate space), and if we wanted to we could pass them in using
          * a UBO, but that isn't needed for this example.
          */
        // ```scala
        val halfStrokeWidth: Float = 0.02f
        val halfGlowWidth: Float   = 0.07f
        val minDistance: Float     = 0.3f
        val distMultiplier: Float  = 0.05f
        // ```

        /** In order to describe the box shape, we're going to use something called an SDF (Signed
          * Distance Function) that will tell us whether this pixel (fragment) is outside the box (>
          * 0), on the edge of the box (~= 0), or inside the box (< 0).
          *
          * We're using one of Ultraviolets built in helper functions to do that, and it requires
          * two parameters:
          *
          *   1. The position of this pixel (i.e. the UV coordinate), re-centered around the origin
          *      (0,0).
          *   2. The 'halfsize' of the box (can be a rectangle or a square).
          *
          * What's important about the halfsize here is that this is the thing we're animating!
          * We're using a sin wave, using the current time as the argument, pushing out the value by
          * a minimum distance. This produces an SDF distance value for a box that is bouncing /
          * changing size.
          *
          * > The argument to `sin` _should_ be an angle, but we're cheating a bit and using the
          * running time in seconds, which more or less does what we want.
          */
        // ```scala
        val halfsize = vec2(minDistance + abs(sin(time) * distMultiplier))
        val sdf      = sdBox(env.UV - 0.5f, halfsize)
        // ```

        /** The SDF value is a smooth gradiant, so to make it a hard 'frame' (like a picture frame),
          * we need to do two things to it:
          *
          *   1. We need to make it 'annular', so that the values inside and outside the shader are
          *      positive, and it ends up looking like a top down view of a square crater.
          *   2. We need to use a `step` function to swap out the gradient for a hard edge.
          */
        // ```scala
        val frame = 1.0f - step(0.0f, abs(-sdf) - halfStrokeWidth)
        // ```

        /** Time for some color! Colors in this example are represented as `vec3`s, where (x, y, z)
          * are equivalent to (red, green, blue).
          *
          * The main color (`col`) is yellow, i.e. full red, full green, no blue.
          *
          * Note that we're calculating the colors separately from the alphas, this is important.
          */
        // ```scala
        val col = vec3(1.0, 1.0, 0.0f)
        // ```

        /** We know that the color is going to be yellow, but what we need to do now is work out the
          * alpha of all that yellow. Obviouly, the alpha will be 0.0 outside the frame, and 1.0
          * inside the middle of the frame, but we also want a little glow effect, which is really
          * just a gradiant ramp in the alpha channel.
          *
          * The glow amount (alpha of the glow) is roughly calculated by looking at the current SDF
          * value, and saying that if the value is between an upper and lower bound, than
          * 'smoothstep' that value.
          *
          * Smoothstepping is a process of interpolating between the upper and lower bound values,
          * to produce a value between 0.0 and 1.0. For example, if the lower bound was 10, and the
          * upper bound was 20, and the value was 15, we'd get a result of 0.5. It isn't quite as
          * simple as that that because of the 'smooth' part. The value produced isn't a linear
          * interpolation, its eased in and out based on an S curve.
          *
          * The final alpha is the glow amount, knocked back by 50%, combined with the frame value
          * which you may recall was either 0.0 or 1.0.
          *
          * This can mean that by our process, a pixel could have an alpha value > 1.0, but since
          * that's unrepresentable, it doesn't matter for our purposes and for all intents and
          * purposes, the value will be clamped to a 0.0 to 1.0 range.
          */
        // ```scala
        val glowAmount = smoothstep(0.95f, 1.05f, 1.0f - (abs(sdf) - halfGlowWidth))
        val alpha      = (glowAmount * 0.5f) + frame
        // ```

        /** Finally we're going to return the pixel colour, a `vec4`, i.e. (red, green, blue,
          * alpha).
          *
          * The thing to note here is that its been constructed with the vec3 colour value and the
          * alpha, i.e. `vec4(vec3(r, g, b), a)` but curiously, the colour has been multiplied by
          * the alpha.
          *
          * This is not an error! It's to do with something called pre-multiplied alpha, and is
          * essential for the colours to come out looking right. (See the Ultraviolet docs for more
          * details.)
          */
        // ```scala
        vec4(col * alpha, alpha)
        // ```
    }

@JSExportTopLevel("IndigoGame")
object AnimationPart4 extends IndigoSandbox[Unit, Unit]:

  val config: GameConfig =
    Config.config.noResize

  val assets: Set[AssetType] =
    Assets.assets.assetSet

  val fonts: Set[FontInfo]        = Set()
  val animations: Set[Animation]  = Set()
  val shaders: Set[ShaderProgram] = Set(CustomShader.shader)

  def setup(assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Unit] =
    Outcome(())

  def updateModel(context: Context[Unit], model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  val lightBlueGrey = RGBA.fromHexString("#9badb7")
  val darkBlueGrey  = RGBA.fromHexString("#3f3f74")
  val darkPurple    = RGBA.fromHexString("#76428a").mix(RGBA.Black)

  val tileSize = Size(64)

  val gridSquare =
    Shape.Box(
      Rectangle(tileSize),
      Fill.RadialGradient(Point(16), 32, lightBlueGrey, darkBlueGrey),
      Stroke(6, darkPurple)
    )

  val offset = Point(10)

  val grid =
    (0 to 2).flatMap { y =>
      (0 to 2).map { x =>
        gridSquare.moveTo(Point(x, y) * tileSize.toPoint).moveBy(offset)
      }
    }.toBatch

  def present(context: Context[Unit], model: Unit): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        grid :+
          BlankEntity(tileSize + Size(10), ShaderData(CustomShader.shader.id))
            .moveTo(Point(64) + offset + Point(-5))
      )
    )
