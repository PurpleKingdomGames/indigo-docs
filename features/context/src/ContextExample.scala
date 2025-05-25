package indigoexamples

import indigo.*
import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*
import scala.annotation.nowarn
import indigo.platform.renderer.ScreenCaptureConfig

/** In this example we're going to need an example of some start up data, so that we can see how to
  * access it via the context object. Here is its definition:
  */
// ```scala
final case class ExampleCustomStartupData(count: Int)
// ```

@nowarn("msg=unused")
@JSExportTopLevel("IndigoGame")
object ContextExample extends IndigoSandbox[ExampleCustomStartupData, Boolean]:

  val config: GameConfig =
    Config.config.noResize

  val assets: Set[AssetType] =
    Assets.assets.assetSet

  val fonts: Set[FontInfo]        = Set()
  val animations: Set[Animation]  = Set()
  val shaders: Set[ShaderProgram] = Set()

  def setup(
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[ExampleCustomStartupData]] =
    Outcome(
      Startup.Success(
        ExampleCustomStartupData(42)
      )
    )

  def initialModel(startupData: ExampleCustomStartupData): Outcome[Boolean] =
    Outcome(false)

  /** For this example, we'll also need a primitive whose bounds we can measure. */
  // ```scala
  val boxSize = Size(100)
  val boxShape: Shape.Box =
    Shape
      .Box(
        Rectangle(Point.zero, boxSize),
        Fill.LinearGradient(Point.zero, RGBA.Cyan, boxSize.toPoint, RGBA.Magenta),
        Stroke(2, RGBA.White)
      )
  // ```

  def updateModel(
      context: Context[ExampleCustomStartupData],
      runOnce: Boolean
  ): GlobalEvent => Outcome[Boolean] =
    case FrameTick if !runOnce =>
      /** Context comes in three parts: Frame, services, and everything else. No matter which flavor
        * of context object you have available, frame and services will always be present.
        */

      /** ## Frame
        *
        * Frame information and helpers are light weight, stateless, immutable and referentially
        * transparent values and utilities. Which means that you can safely reference them without
        * adding much burden should you need to write unit tests.
        *
        * Below are some examples.
        */
      // ```scala
      context.frame.globalMagnification
      context.frame.viewport
      context.frame.viewport.giveDimensions(context.frame.globalMagnification)
      context.frame.dice.roll(6)
      context.frame.dice.rollDouble
      context.frame.dice.rollAlphaNumeric(16)
      context.frame.input.mouse.position
      context.frame.time.running
      context.frame.time.delta
      // ```

      /** ## Services
        *
        * Services on the other hand and stateful, side-effecting, and potentially expensive
        * operations to be used with caution. That doesn't mean "don't use them", it just means they
        * need a little more consideration.
        *
        * Some services are shown below.
        */

      /** ### Random
        *
        * Random differs from Dice in that it is zero based (Dice always have at least 1 side!), and
        * it uses a stateful long running Random instance, meaning that it does give better random
        * numbers at the cost of testing complexity.
        */
      // ```scala
      context.services.random.nextInt
      context.services.random.alphanumeric(16)
      // ```

      /** ### Boundary Locator
        *
        * The boundary locator asks Indigo to calculate the bounding box of any visual primitive.
        * Sometimes this is easy, sometimes it's expensive, depending on the primitive type. Text
        * for example, requires the bounds calculation of every glyph, and then the total bounds of
        * all the arranged glyphs.
        */
      // ```scala
      context.services.bounds.find(boxShape)
      // ```

      /** ### Screen capture
        *
        * The screen capture is unusual in as much as it runs the side effect _right now_(!) and
        * returns either an error message or the result. You can capture one or several screeshots
        * at once, each with a different configuration. For example, you might want one at 1:1 scale
        * and another smaller one for a save game thumbnail.
        */
      // ```scala
      val screenshots: Set[AssetType] =
        context.services.screen
          .capture(Batch(ScreenCaptureConfig.default))
          .collect { case Right(image) => image }
          .toSet
      // ```

      /** In order to use screenshots, you need to tell Indigo to load the assets and make them
        * available as textures, and then listen for their availability using events like the ones
        * below.
        */
      // ```scala
      AssetEvent.LoadAssetBatch(screenshots, BindingKey("captureScreen"), true)
      AssetEvent.AssetBatchLoaded(BindingKey("captureScreen"), assets = Set(), available = true)
      // ```

      /** ## Everything else!
        *
        * The 'everything else' varies depending on the nature of the context object, a
        * `SceneContext` for example will provide scene specific information. A standard context
        * object contains the startup data, so that it can be referenced from anywhere.
        */
      // ```scala
      context.startUpData
      // ```

      Outcome(true)

    case _ =>
      Outcome(runOnce)

  def present(
      context: Context[ExampleCustomStartupData],
      model: Boolean
  ): Outcome[SceneUpdateFragment] =
    val viewportCenter =
      context.frame.viewport.giveDimensions(context.frame.globalMagnification).center

    Outcome(
      SceneUpdateFragment(
        boxShape.moveTo(viewportCenter - boxSize.toPoint / 2)
      )
    )
