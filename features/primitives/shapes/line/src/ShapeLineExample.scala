package indigoexamples

import indigo.*
import generated.Config
import generated.Assets

class ShapeLineExample() extends Game[Unit, Unit, Unit]:

  def gameId: GameId = GameId("ShapeLineExample")

  def boot(flags: Map[String, String]): Outcome[BootResult[Unit, Unit]] =
    Outcome(
      BootResult(Config.config, ())
        .withAssets(Assets.assets.assetSetRelative)
    )

  def initialScene(bootData: Unit): Option[SceneName] = None

  def scenes(bootData: Unit): NonEmptyBatch[Scene[Unit, Unit]] =
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
      (context.frame.viewport / 2).toPoint

    val start = Point(10, 20)
    val end   = Point(100, 80)

    Outcome(
      SceneUpdateFragment(
        Shape.Line(start, end, Stroke(2, RGBA.White))
      )
    )
