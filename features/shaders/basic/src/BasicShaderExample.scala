package indigoexamples

/** ## How to write and use a custom shader
  */

import indigo.*

// We'll need an extra import for shader writing with Ultraviolet:
// ``` scala
import ultraviolet.syntax.*
// ```
import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*
import scala.annotation.nowarn

@JSExportTopLevel("IndigoGame")
object BasicShaderExample extends IndigoSandbox[Unit, Unit]:

  val config: GameConfig =
    Config.config.noResize

  val assets: Set[AssetType] =
    Assets.assets.assetSet

  val fonts: Set[FontInfo]       = Set()
  val animations: Set[Animation] = Set()

  // We then need to add our custom shader to the list of shaders:
  // ``` scala
  val shaders: Set[ShaderProgram] = Set(CustomShader.shader)
  // ```

  def setup(assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Unit] =
    Outcome(())

  def updateModel(context: Context[Unit], model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  /** A `BlankEntity` is then used to display the shader. A BlankEntity is the most basic form of
    * entity, literally just registering a space on the screen, with no notion of how to paint into
    * it until you supply a shader to do the work.
    */
  // ``` scala
  def present(context: Context[Unit], model: Unit): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        BlankEntity(10, 10, 200, 200, ShaderData(CustomShader.shader.id))
      )
    )
  // ```

/** The job of our shader is to manufacture a colour for each fragment (read: pixel - kinda..),
  * defined at a vec4 of floats for the red, green, blue, and alpha values.
  *
  * The custom shader is defined in two parts:
  *
  *   1. The shader definition, in this case and ultraviolet fragment shader with a unique id, and a
  *      reference to the shader code with it's supplied environment.
  *   2. The shader code itself. In this case we've made a fragment shader that cycles through the
  *      colour wheel. (This is based on the default [shadertoy](https://www.shadertoy.com/) code!)
  */
// ``` scala
object CustomShader:

  val shader: ShaderProgram =
    UltravioletShader.entityFragment(
      ShaderId("custom-shader"),
      EntityShader.fragment[FragmentEnv](fragment, FragmentEnv.reference)
    )

  @nowarn("msg=unused")
  inline def fragment: Shader[FragmentEnv, Unit] =
    Shader[FragmentEnv] { env =>
      def fragment(color: vec4): vec4 =
        val col: vec3 = 0.5f + 0.5f * cos(env.TIME + env.UV.xyx + vec3(0.0f, 2.0f, 4.0f))
        vec4(col, 1.0f)
    }
// ```
