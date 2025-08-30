package indigoexamples

import indigo.*
import generated.Config
import generated.Assets
import ultraviolet.syntax.*

import scala.scalajs.js.annotation.*
import scala.annotation.nowarn

/** ## Setting up a BlankEntity with a custom shader
  *
  * First, we'll need a simple shader. This one renders pretty colours.
  */
// ```scala
object CustomShader:

  val shader: ShaderProgram =
    UltravioletShader.entityFragment(
      ShaderId("custom shader"),
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

@JSExportTopLevel("IndigoGame")
object BlankEntityExample extends IndigoSandbox[Unit, Unit]:

  val config: GameConfig =
    Config.config.noResize

  val assets: Set[AssetType] =
    Assets.assets.assetSet

  val fonts: Set[FontInfo]       = Set()
  val animations: Set[Animation] = Set()
  // Then we need to register the shader. In an IndigoDemo/Game you'll need to do this on the boot result object.
  // ```scala
  val shaders: Set[ShaderProgram] = Set(CustomShader.shader)
  // ```

  def setup(assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Unit] =
    Outcome(())

  def updateModel(context: Context[Unit], model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  // Finally, we can use a blank entity to render the shader into a space on the screen.
  // ```scala
  def present(context: Context[Unit], model: Unit): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        BlankEntity(Size(200, 200), ShaderData(CustomShader.shader.id))
          .moveTo(20, 20)
      )
    )
  // ```
