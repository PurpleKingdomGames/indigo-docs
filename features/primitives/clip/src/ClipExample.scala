package indigoexamples

import indigo.*
import indigoexamples.generated.Config
import indigoexamples.generated.Assets

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("IndigoGame")
object ClipExample extends IndigoDemo[Unit, Unit, Unit, Unit]:

  val eventFilters: EventFilters =
    EventFilters.Permissive

  def boot(flags: Map[String, String]): Outcome[BootResult[Unit, Unit]] =
    Outcome {
      val config =
        Config.config
          .withMagnification(2)
          .noResize

      BootResult
        .noData(config)
        .withAssets(Assets.assets.assetSet)
    }

  def setup(
      bootInfo: Unit,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Unit] =
    Outcome(())

  def initialViewModel(startupData: Unit, model: Unit): Outcome[Unit] =
    Outcome(())

  def updateModel(context: Context[Unit], model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  def updateViewModel(
      context: Context[Unit],
      model: Unit,
      viewModel: Unit
  ): GlobalEvent => Outcome[Unit] =
    _ => Outcome(viewModel)

  def present(
      context: Context[Unit],
      model: Unit,
      viewModel: Unit
  ): Outcome[SceneUpdateFragment] =

    // ```scala
    val clip: Clip[Material.Bitmap] =
      Clip(Size(64, 128), ClipSheet(9, FPS(10)), ClipPlayMode.default, Assets.assets.FlagMaterial)

    Outcome(SceneUpdateFragment(clip))
    // ```
