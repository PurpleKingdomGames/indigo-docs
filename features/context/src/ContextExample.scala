package indigoexamples

import indigo.*

import scala.annotation.nowarn

@nowarn("msg=unused")
class ContextExample() extends Game[Unit, Unit, Boolean]:

  def boot(flags: Map[String, String]): Outcome[BootResult[Unit, Boolean]] =
    Outcome(BootResult.default)

  def eventFilters: EventFilters =
    EventFilters.Permissive

  def gameId: GameId =
    GameId("context")

  def initialScene(bootData: Unit): Option[SceneName] =
    None

  def scenes(bootData: Unit): NonEmptyBatch[Scene[Boolean]] =
    NonEmptyBatch(Scene.empty)

  def setup(bootData: Unit, assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Boolean] =
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

  @nowarn("msg=statement")
  def updateModel(
      context: Context,
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
      context.frame.viewport
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

      Outcome(true)

    case _ =>
      Outcome(runOnce)

  def present(
      context: Context,
      model: Boolean
  ): Outcome[SceneUpdateFragment] =
    val viewportCenter =
      context.frame.viewport.center

    Outcome(
      SceneUpdateFragment(
        LayerKey("demo") -> Layer(
          boxShape.moveTo(viewportCenter - boxSize.toPoint / 2)
        )
      )
    )
