package pirate.core

import indigo.*
import indigo.json.Json
import indigo.shared.formats.TiledGridMap
import pirate.generated.Assets.*
import pirate.generated.CaptainAnim

object InitialLoad:

  def setup(
      screenDimensions: Rectangle,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[StartupData]] =
    Outcome(
      CaptainAnim.aseprite
        .toClips(assets.captain.CaptainClownNose)
        .map { captainClips =>
          makeStartupData(
            captainClips,
            levelDataStore(screenDimensions, assetCollection, dice)
          )
        } match
        case None =>
          Startup.Failure("Failed to start The Cursed Pirate")

        case Some(success) =>
          success
    )

  def makeStartupData(
      captainClips: Map[CycleLabel, Clip[Material.Bitmap]],
      levelDataStore: Option[LevelDataStore]
  ): Startup[StartupData] =
    val captainClipsPrepared =
      captainClips.map { case (label, clip) =>
        label ->
          clip
            .modifyMaterial(m => Material.ImageEffects(m.diffuse))
            .withRef(37, 64)
            .moveTo(300, 271)
      }

    val pirateClips =
      for {
        idle <- captainClipsPrepared.get(CycleLabel("Idle"))
        run  <- captainClipsPrepared.get(CycleLabel("Run"))
        fall <- captainClipsPrepared.get(CycleLabel("Fall"))
        jump <- captainClipsPrepared.get(CycleLabel("Jump"))
      } yield PirateClips(
        idleLeft = idle.flipHorizontal(true).withRef(idle.ref.moveBy(20, 0)),
        idleRight = idle,
        moveLeft = run.flipHorizontal(true).withRef(run.ref.moveBy(20, 0)),
        moveRight = run,
        fallLeft = fall.flipHorizontal(true).withRef(fall.ref.moveBy(20, 0)),
        fallRight = fall,
        jumpLeft = jump.flipHorizontal(true).withRef(jump.ref.moveBy(20, 0)),
        jumpRight = jump
      )

    pirateClips match
      case None =>
        Startup.Failure("Pirate captain animations failed to load")

      case Some(pcs) =>
        Startup
          .Success(
            StartupData(
              pcs.moveRight.modifyMaterial(_.withOverlay(Fill.Color(RGBA.White))),
              pcs,
              levelDataStore
            )
          )

  def levelDataStore(
      screenDimensions: Rectangle,
      assetCollection: AssetCollection,
      dice: Dice
  ): Option[LevelDataStore] =
    if assetCollection.findTextDataByName(assets.helm.ShipHelmData).isDefined &&
      assetCollection.findTextDataByName(assets.trees.PalmTreeData).isDefined &&
      assetCollection.findTextDataByName(assets.water.WaterReflectData).isDefined &&
      assetCollection.findTextDataByName(assets.flag.FlagData).isDefined &&
      assetCollection.findTextDataByName(assets.static.terrainData).isDefined
    then
      val tileMapper: Int => TileType =
        case 0 => TileType.Empty
        case _ => TileType.Solid

      val terrainData: Option[(Point, TiledGridMap[TileType], Group)] =
        for {
          json         <- assetCollection.findTextDataByName(assets.static.terrainData)
          tileMap      <- Json.tiledMapFromJson(json)
          terrainGroup <- tileMap.toGroup(assets.static.terrain)
          grid         <- tileMap.toGrid(tileMapper)
        } yield (Point(tileMap.tilewidth, tileMap.tileheight), grid, terrainGroup)

      for {

        helm <- loadSingleClip(
          assetCollection,
          assets.helm.ShipHelmData,
          assets.helm.ShipHelm,
          "Idle"
        )

        palm <- loadSingleClip(
          assetCollection,
          assets.trees.PalmTreeData,
          assets.trees.PalmTree,
          "P Front"
        )

        backPalm <- loadSingleClip(
          assetCollection,
          assets.trees.PalmTreeData,
          assets.trees.PalmTree,
          "P Back"
        )

        reflections <- loadSingleClip(
          assetCollection,
          assets.water.WaterReflectData,
          assets.water.WaterReflect,
          "Big"
        )

        flag <- loadSingleClip(
          assetCollection,
          assets.flag.FlagData,
          assets.flag.Flag,
          "Flapping"
        )

        terrain <- terrainData

      } yield LevelDataStore(
        reflections
          .withRef(85, 0)
          .moveTo(screenDimensions.horizontalCenter, screenDimensions.verticalCenter + 5),
        flag.withRef(22, 105).moveTo(200, 288),
        helm.withRef(31, 49).moveTo(605, 160),
        palm,
        backPalm,
        terrain._1,
        terrain._2,
        terrain._3
      )
    else None

  private def loadSingleClip(
      assetCollection: AssetCollection,
      jsonRef: AssetName,
      name: AssetName,
      cycleName: String
  ): Option[Clip[Material.Bitmap]] =
    for {
      json     <- assetCollection.findTextDataByName(jsonRef)
      aseprite <- Json.asepriteFromJson(json)
      clips    <- aseprite.toClips(name)
      clip     <- clips.get(CycleLabel(cycleName))
    } yield clip

final case class StartupData(
    captainLoading: Clip[Material.ImageEffects],
    captainClips: PirateClips,
    levelDataStore: Option[LevelDataStore]
)
final case class LevelDataStore(
    waterReflections: Clip[Material.Bitmap],
    flag: Clip[Material.Bitmap],
    helm: Clip[Material.Bitmap],
    palm: Clip[Material.Bitmap],
    backTallPalm: Clip[Material.Bitmap],
    tileSize: Point,
    terrainMap: TiledGridMap[TileType],
    terrain: Group
)

enum TileType:
  case Empty, Solid

final case class PirateClips(
    idleLeft: Clip[Material.ImageEffects],
    idleRight: Clip[Material.ImageEffects],
    moveLeft: Clip[Material.ImageEffects],
    moveRight: Clip[Material.ImageEffects],
    fallLeft: Clip[Material.ImageEffects],
    fallRight: Clip[Material.ImageEffects],
    jumpLeft: Clip[Material.ImageEffects],
    jumpRight: Clip[Material.ImageEffects]
)
