package preloader.core

import indigo.*
import preloader.generated.Assets.*
import preloader.generated.CaptainAnim

object InitialLoad:

  def setup(assetPath: String): Outcome[Startup[StartupData]] =
    Outcome(
      CaptainAnim.aseprite
        .toClips(assets.captain.CaptainClownNose)
        .map { captainClips =>
          makeStartupData(
            assetPath,
            captainClips
          )
        } match
        case None =>
          Startup.Failure("Failed to start")

        case Some(success) =>
          success
    )

  def makeStartupData(
      assetPath: String,
      captainClips: Map[CycleLabel, Clip[Material.Bitmap]]
  ): Startup[StartupData] =
    val captainClipsPrepared =
      captainClips.map { case (label, clip) =>
        label ->
          clip
            .modifyMaterial(m => Material.ImageEffects(m.diffuse))
            .withRef(37, 64)
            .moveTo(300, 271)
      }

    captainClipsPrepared.get(CycleLabel("Run")) match
      case None =>
        Startup.Failure("Pirate captain running animation could not be created")

      case Some(runningPirateClip) =>
        Startup
          .Success(
            StartupData(
              assetPath,
              runningPirateClip.modifyMaterial(_.withOverlay(Fill.Color(RGBA.White)))
            )
          )

final case class StartupData(assetPath: String, captainLoading: Clip[Material.ImageEffects])
