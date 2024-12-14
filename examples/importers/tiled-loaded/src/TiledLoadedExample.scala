package indigoexamples

import indigo.*
import indigo.json.Json
import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*

/** ## How to load and use a Tiled map
  */

/** In this example we're going to need a bit of a model to help us organise our data.
  *
  * The top level model will contain the loaded tilemap, which we'll use later directly during
  * rendering, and a custom game map.
  *
  * Normally, the tiledMap would probably make more sense to live in the view model, since it is
  * purely a rendering concern, but since this example is build in the sandbox we don't have that
  * option. You'll have to use your imagination!
  *
  * The game map is a custom representation of the tilemap that we can use to drive game logic. It
  * is all very well importing a game level from Tiled, but you will almost certainly want a model
  * representation so that you can, for example, check for collisions, or whether moves are valid,
  * or ask what lives where on a map, etc. In this example, we only have two types of tile.
  *
  * Note the useful helpers in the GameMap companion object.
  */
// ```scala
final case class Model(tiledMap: TiledMap, gameMap: GameMap)

final case class GameMap(grid: List[List[MapTile]])
object GameMap:
  val empty: GameMap = GameMap(List.empty)

  def fromTiledGrid(grid: TiledGridMap[MapTile]): GameMap =
    GameMap(grid.toList2DPerLayer.head.map(_.map(_.tile)))

enum MapTile:
  case Platform, Empty
// ```

@JSExportTopLevel("IndigoGame")
object TiledLoadedExample extends IndigoSandbox[TiledMap, Model]:

  val config: GameConfig =
    Config.config.noResize

  val assets: Set[AssetType] =
    Assets.assets.assetSet

  val fonts: Set[FontInfo]       = Set()
  val animations: Set[Animation] = Set()
  val shaders: Set[Shader]       = Set()

  /** ### Loading the data
    *
    * To make use of the tiled data, we need to load and parse it, using the Tiled Json helper. In
    * this example that will happen during the initial loading phase, but in a real game you might
    * want to load it on demand using the asset loader.
    */
  // ```scala
  def setup(assetCollection: AssetCollection, dice: Dice): Outcome[Startup[TiledMap]] =
    Outcome {
      val maybeTiledMap = for {
        j <- assetCollection.findTextDataByName(Assets.assets.terrainData)
        t <- Json.tiledMapFromJson(j)
      } yield t

      maybeTiledMap match {
        case None =>
          Startup.Failure("Could not generate TiledMap from data.")

        case Some(tiledMap) =>
          Startup.Success(tiledMap)
      }
    }
  // ```

  /** ### Initialising the model
    *
    * Our model is then initialised with the tilemap (which would normally live in the view model,
    * probably), and we make use of the `toGrid` method on the tilemap to convert it into our custom
    * game map.
    *
    * We're not using the custom game map in this example, but it's being println'd out so that you
    * can see it in the JS console if you run the example.
    */
  // ```scala
  def initialModel(startupData: TiledMap): Outcome[Model] =
    val gameMap = startupData
      .toGrid {
        case 0 => MapTile.Empty
        case _ => MapTile.Platform
      }
      .map(GameMap.fromTiledGrid)
      .getOrElse(GameMap.empty)

    println(gameMap)

    Outcome(
      Model(startupData, gameMap)
    )
  // ```

  def updateModel(context: FrameContext[TiledMap], model: Model): GlobalEvent => Outcome[Model] =
    _ => Outcome(model)

  /** ### Rendering the tilemap
    *
    * To display the tiled map, we just use the `toGroup` method on the tilemap to convert it into
    * an Indigo group of primitives that reference the terrain image asset.
    *
    * `toGroup` is a simple convenience method that will create a `Graphic` for each tile in the
    * map. If you want more control you'll either need to interpret the `TiledMap` yourself, or make
    * use of the custom version we made earlier.
    */
  // ```scala
  def present(context: FrameContext[TiledMap], model: Model): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        model.tiledMap.toGroup(Assets.assets.terrain)
      )
    )
  // ```
