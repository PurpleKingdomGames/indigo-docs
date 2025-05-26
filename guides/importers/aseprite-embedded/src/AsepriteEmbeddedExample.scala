package indigoexamples

import indigo.*
import indigoexamples.generated.Config
import indigoexamples.generated.Assets
import indigoexamples.generated.CaptainAnim

import scala.scalajs.js.annotation.JSExportTopLevel

/** ## Embedding the pirate captain
  *
  * In this example, we're going to embed the pirate captain, convert him to Clip instances, and
  * play one of them.
  */
@JSExportTopLevel("IndigoGame")
object AsepriteEmbeddedExample extends IndigoDemo[Unit, StartupData, Unit, Unit]:

  val eventFilters: EventFilters =
    EventFilters.BlockAll

  def boot(flags: Map[String, String]): Outcome[BootResult[Unit, Unit]] =
    Outcome {
      val assetPath: String =
        flags.getOrElse("baseUrl", "")

      val config =
        Config.config
          .withMagnification(2)
          .noResize

      BootResult
        .noData(config)
        .withAssets(Assets.assets.assetSet(assetPath))
    }

  def setup(
      bootInfo: Unit,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[StartupData]] =
    InitialLoad.setup

  def initialModel(startupData: StartupData): Outcome[Unit] =
    Outcome(())

  def initialViewModel(startupData: StartupData, model: Unit): Outcome[Unit] =
    Outcome(())

  def updateModel(context: Context[StartupData], model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  def updateViewModel(
      context: Context[StartupData],
      model: Unit,
      viewModel: Unit
  ): GlobalEvent => Outcome[Unit] =
    _ => Outcome(viewModel)

  def present(
      context: Context[StartupData],
      model: Unit,
      viewModel: Unit
  ): Outcome[SceneUpdateFragment] =
    val viewport =
      context.frame.viewport.giveDimensions(context.frame.globalMagnification)

    Outcome(
      SceneUpdateFragment(
        context.startUpData.captainLoading.moveTo(viewport.center)
      )
    )

object InitialLoad:

  def setup: Outcome[Startup[StartupData]] =
    Outcome(
      CaptainAnim.aseprite
        .toClips(Assets.assets.CaptainClownNose)
        .map(makeStartupData) match
        case None =>
          Startup.Failure("Failed to start")

        case Some(success) =>
          success
    )

  def makeStartupData(
      captainClips: Map[CycleLabel, Clip[Material.Bitmap]]
  ): Startup[StartupData] =
    val captainClipsPrepared =
      captainClips.map { case (label, clip) =>
        label ->
          clip
            .withRef(37, 64)
            .moveTo(300, 271)
      }

    captainClipsPrepared.get(CycleLabel("Run")) match
      case None =>
        Startup.Failure("Pirate captain running animation could not be created")

      case Some(runningPirateClip) =>
        Startup.Success(StartupData(runningPirateClip))

final case class StartupData(captainLoading: Clip[Material.Bitmap])
