package indigoexamples

import indigo.*
import generated.Config
import generated.Assets

class ShapeCircleExample() extends Game[Unit, Unit, Unit]:

  def gameId: GameId = GameId("ShapeCircleExample")

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

  def present(context: Context, model: Unit): Outcome[SceneUpdateFragment] =
    val viewportCenter =
      context.frame.viewport.center

    val radius = 50

    Outcome(
      SceneUpdateFragment(
        LayerKey("demo") -> Layer.Content(
          Shape
            .Circle(
              Circle(Point.zero, radius),
              Fill.LinearGradient(Point.zero, RGBA.Cyan, Point(radius * 2), RGBA.Magenta),
              Stroke(2, RGBA.White)
            )
            .moveTo(viewportCenter)
        )
      )
    )
