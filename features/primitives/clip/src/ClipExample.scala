package indigoexamples

import indigo.*
import indigoexamples.generated.Config
import indigoexamples.generated.Assets

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("IndigoGame")
object ClipExample extends Game[Unit, Unit, Unit]:

  def gameId: GameId = GameId("ClipExample")

  val eventFilters: EventFilters =
    EventFilters.Permissive

  def boot(flags: Map[String, String]): Outcome[BootResult[Unit, Unit]] =
    Outcome {
      val config =
        Config.config

      BootResult
        .noData(config)
        .withAssets(Assets.assets.assetSetRelative)
    }

  def initialScene(bootData: Unit): Option[SceneName] = None

  def scenes(bootData: Unit): NonEmptyBatch[Scene[Unit, Unit]] =
    NonEmptyBatch(Scene.empty)

  def setup(
      bootInfo: Unit,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Unit] =
    Outcome(())

  def updateModel(context: Context, model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  def present(
      context: Context,
      model: Unit
  ): Outcome[SceneUpdateFragment] =

    // ```scala
    val clip: Clip[Material.Bitmap] =
      Clip(Size(64, 128), ClipSheet(9, FPS(10)), ClipPlayMode.default, Assets.assets.FlagMaterial)

    Outcome(SceneUpdateFragment(clip).withMagnification(2))
    // ```
