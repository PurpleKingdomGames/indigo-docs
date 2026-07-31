package indigoexamples

import indigo.*
import generated.Config
import generated.Assets

class ShapeBoxExample() extends Game[Unit, Unit, Unit]:

  def gameId: GameId = GameId("box")

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

    val size = Size(100)

    Outcome(
      SceneUpdateFragment(
        LayerKey("demo") -> Layer(
          Shape
            .Box(
              Rectangle(Point.zero, size),
              Fill.LinearGradient(Point.zero, RGBA.Cyan, size.toPoint, RGBA.Magenta),
              Stroke(2, RGBA.White)
            )
            .moveTo(viewportCenter - size.toPoint / 2)
        )
      )
    )
