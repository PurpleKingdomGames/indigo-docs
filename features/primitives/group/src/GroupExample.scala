package indigoexamples

import indigo.*
import generated.Config
import generated.Assets

class GroupExample() extends Game[Unit, Unit, Unit]:

  def gameId: GameId = GameId("GroupExample")

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

  /** # How to use a group
    *
    * In this example we arrange three circle relative to each other, and then move the group the
    * middle of the screen.
    */
  // ```scala
  def present(context: Context, model: Unit): Outcome[SceneUpdateFragment] =
    val viewportCenter =
      context.frame.viewport.center

    val radius = 50

    Outcome(
      SceneUpdateFragment(
        LayerKey("demo") -> Layer(
          Group(
            Shape
              .Circle(
                Circle(Point.zero, radius),
                Fill.Color(RGBA.Red.withAlpha(0.75)),
                Stroke(2, RGBA.White)
              )
              .moveTo(Point(0, -25)),
            Shape
              .Circle(
                Circle(Point.zero, radius),
                Fill.Color(RGBA.Green.withAlpha(0.75)),
                Stroke(2, RGBA.White)
              )
              .moveTo(Point(25, 25)),
            Shape
              .Circle(
                Circle(Point.zero, radius),
                Fill.Color(RGBA.Blue.withAlpha(0.75)),
                Stroke(2, RGBA.White)
              )
              .moveTo(Point(-25, 25))
          ).moveTo(viewportCenter)
            .withRef(25, 25)
        )
      )
    )
  // ```
