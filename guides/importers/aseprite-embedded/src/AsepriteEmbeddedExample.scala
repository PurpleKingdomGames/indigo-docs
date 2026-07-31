package indigoexamples

import indigo.*
import indigoexamples.generated.Config
import indigoexamples.generated.Assets
import indigoexamples.generated.CaptainAnim

/** ## Embedding the pirate captain
  *
  * In this example, we're going to embed the pirate captain, convert him to Clip instances, and
  * play one of them.
  */
class AsepriteEmbeddedExample() extends Game[Unit, StartupData, StartupData]:

  def gameId: GameId = GameId("AsepriteEmbeddedExample")

  val eventFilters: EventFilters =
    EventFilters.BlockAll

  def boot(flags: Map[String, String]): Outcome[BootResult[Unit, StartupData]] =
    Outcome {
      val assetPath: String =
        flags.getOrElse("baseUrl", "")

      val config =
        Config.config

      BootResult
        .noData(config)
        .withAssets(Assets.assets.assetSetRelativeTo(assetPath))
    }

  def initialScene(bootData: Unit): Option[SceneName] = None

  def scenes(bootData: Unit): NonEmptyBatch[Scene[StartupData]] =
    NonEmptyBatch(Scene.empty)

  def setup(
      bootInfo: Unit,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[StartupData]] =
    InitialLoad.setup

  def initialModel(startupData: StartupData): Outcome[StartupData] =
    Outcome(startupData)

  def updateModel(context: Context, model: StartupData): GlobalEvent => Outcome[StartupData] =
    _ => Outcome(model)

  def present(
      context: Context,
      model: StartupData
  ): Outcome[SceneUpdateFragment] =
    val viewportCenter =
      context.frame.viewport.center / Magnification.x2.toInt

    Outcome(
      SceneUpdateFragment(
        LayerKey("demo") -> Layer(
          model.captainLoading.moveTo(viewportCenter)
        )
      ).withMagnification(Magnification.x2)
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
