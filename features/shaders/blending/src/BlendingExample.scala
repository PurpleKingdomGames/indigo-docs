package indigoexamples

import indigo.*
import generated.Config
import generated.Assets
import ultraviolet.syntax.*

import scala.scalajs.js.annotation.*
import scala.annotation.nowarn

@JSExportTopLevel("IndigoGame")
object BlendingExample extends IndigoSandbox[Unit, Unit]:

  val config: GameConfig =
    Config.config.noResize
      .withMagnification(2)

  val assets: Set[AssetType] =
    Assets.assets.assetSet

  val fonts: Set[FontInfo]       = Set()
  val animations: Set[Animation] = Set()

  val shaders: Set[ShaderProgram] =
    Set(
      CustomEntityShader.shader,
      CustomBlendShader.shader
    )

  def setup(assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Unit] =
    Outcome(())

  def updateModel(context: Context[Unit], model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  /** In this scene we render 3 boxes, using 4 layers (mostly for clarity) and 1 blend material.
    *
    * Left to right:
    *
    *   - Box 1 is a simple graphic with a bitmap texture
    *   - Box 2 is a custom entity running the same custom shader we used in the basic entity shader
    *     example.
    *   - Box 3 is made by rendering Box 1 on one layer, then rendering Box 2 on a layer above it,
    *     and giving this layer our custom blend material explaining how we'd like it to be merge to
    *     the layer below.
    *
    * In this instance the example shows all the arguments you can supply to a `Blending` instance,
    * but you can also construct them with only the `BlendMaterial`, or even skip it and replace
    * `withBlending` with `withBlendMaterial`.
    */
  // ``` scala
  def present(context: Context[Unit], model: Unit): Outcome[SceneUpdateFragment] =
    val gap = 2

    Outcome(
      SceneUpdateFragment(
        Layer(
          Graphic(0, 0, 64, 64, Material.Bitmap(Assets.assets.nineslice))
            .moveTo(10, 10)
        ),
        Layer(
          BlankEntity(0, 0, 64, 64, ShaderData(CustomEntityShader.shader.id))
            .moveTo(10 + 64 + gap, 10)
        ),
        Layer(
          Graphic(0, 0, 64, 64, Material.Bitmap(Assets.assets.nineslice))
            .moveTo(10 + 64 + gap + 64 + gap, 10)
        ),
        Layer(
          BlankEntity(0, 0, 64, 64, ShaderData(CustomEntityShader.shader.id))
            .moveTo(10 + 64 + gap + 64 + gap, 10)
        ).withBlending(
          Blending(
            entity = Blend.Normal,
            layer = Blend.Normal,
            blendMaterial = CustomBlendMaterial(),
            clearColor = None
          )
        )
      )
    )
  // ```

object CustomEntityShader:

  val shader: ShaderProgram =
    UltravioletShader.entityFragment(
      ShaderId("custom-entity-shader"),
      EntityShader.fragment[FragmentEnv](fragment, FragmentEnv.reference)
    )

  @nowarn("msg=unused")
  inline def fragment: Shader[FragmentEnv, Unit] =
    Shader[FragmentEnv] { env =>
      def fragment(color: vec4): vec4 =
        val col: vec3 = 0.5f + 0.5f * cos(env.TIME + env.UV.xyx + vec3(0.0f, 2.0f, 4.0f))
        vec4(col, 1.0f)
    }

/** A `BlendMaterial` is nothing more than a class that provides the `ShaderData` used to tell
  * Indigo how to merge one layer down onto the layer below it.
  *
  * In terms of the process, each layer is first rendered, and then the blending is performed. In
  * effect, you are combining to images of the same size in the same location.
  */
// ``` scala
final case class CustomBlendMaterial() extends BlendMaterial:
  def toShaderData: ShaderData =
    ShaderData(CustomBlendShader.shader.id)
// ```

/** The structure of a blend shader is _very_ similar that seen in the basic entity shader example.
  * There are a number of differences though that all boil down to telling Indigo to expect a blend
  * shader instead of an entity shader.
  *
  * In terms of the actual code, the main thing to note is that the `BlendFragmentEnv` has replaced
  * our usual `FragmentEnv`, and this gives us access to `env.SRC` and `env.DST`, which are the
  * color values of the current pixel on each layer, where src is merging down to dst.
  *
  * `env.SRC` and `env.DST` are convenience variables that you get for free, but you can always
  * change which pixel you'd like to reference using
  * `texture2D(SRC_CHANNEL, <some custom UV coordinate)`.
  *
  * This shader mixes the two layers together 50-50.
  */
// ``` scala
object CustomBlendShader:

  val shader: ShaderProgram =
    UltravioletShader.blendFragment(
      ShaderId("custom-blend-shader"),
      BlendShader.fragment[BlendFragmentEnv](fragment, BlendFragmentEnv.reference)
    )

  @nowarn("msg=unused")
  inline def fragment: Shader[BlendFragmentEnv, Unit] =
    Shader[BlendFragmentEnv] { env =>
      def fragment(color: vec4): vec4 =
        mix(env.DST, env.SRC, 0.5f)
    }
// ```
