package pirate

import indigo.*
import pirate.scenes.loading.LoadingScene
import pirate.scenes.level.LevelScene
import pirate.core.Model
import pirate.core.BootInformation
import pirate.core.LayerKeys
import pirate.generated.Config

import pirate.core.{Assets, InitialLoad, StartupData}

import indigoextras.subsystems.FPSCounter

class CursedPirateDemo() extends Game[BootInformation, StartupData, Model]:

  def gameId: GameId = GameId("CursedPirateDemo")

  def initialScene(bootInfo: BootInformation): Option[SceneName] =
    None

  def scenes(bootInfo: BootInformation): NonEmptyBatch[Scene[Model]] =
    NonEmptyBatch(
      LoadingScene(bootInfo.assetPath, bootInfo.screenDimensions),
      LevelScene(bootInfo.screenDimensions.width)
    )

  val eventFilters: EventFilters =
    EventFilters.Permissive

  def boot(flags: Map[String, String]): Outcome[BootResult[BootInformation, Model]] =
    Outcome {
      val assetPath: String =
        flags.getOrElse("baseUrl", "")

      val config =
        Config.config

      val screenDimensions =
        Rectangle(0, 0, 640, 360)

      BootResult(
        config,
        BootInformation(assetPath, screenDimensions)
      ).withAssets(Assets.initialAssets(assetPath))
        .withFonts(Assets.Fonts.fontInfo)
        .withSubSystems(
          FPSCounter[Model](
            Assets.Fonts.fontKey,
            pirate.generated.Assets.assets.fonts.boxyFontSmall,
            LayerKeys.fps
          )
        )
    }

  def setup(
      bootInfo: BootInformation,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[StartupData]] =
    InitialLoad
      .setup(bootInfo.screenDimensions, assetCollection)
      .createGlobalEvents {
        case Startup.Failure(_) =>
          Batch.empty

        case Startup.Success(startupData, _, _, _) =>
          Batch(ReplaceStartupData(startupData))
      }

  def initialModel(startupData: StartupData): Outcome[Model] =
    Outcome(Model.initial(startupData))

  def updateModel(context: Context, model: Model): GlobalEvent => Outcome[Model] =
    case ReplaceStartupData(newStartupData) =>
      Outcome(
        model.copy(startupData = newStartupData)
      )

    case _ =>
      Outcome(model)

  def present(
      context: Context,
      model: Model
  ): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment.empty
        .withLayers(
          LayerKeys.background  -> Layer.Content.empty,
          LayerKeys.bigClouds   -> Layer.Content.empty,
          LayerKeys.smallClouds -> Layer.Content.empty,
          LayerKeys.game        -> Layer.Content.empty,
          LayerKeys.fps         -> Layer.Content.empty
        )
        .withMagnification(Magnification.x2)
    )

final case class ReplaceStartupData(startupData: StartupData) extends GlobalEvent
