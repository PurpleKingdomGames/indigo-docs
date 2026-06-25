package indigoexamples

import indigo.*
import generated.Config
import generated.Assets

class FillTypeExample() extends Game[Unit, Unit, Unit]:

  def gameId: GameId = GameId("FillTypeExample")

  def boot(flags: Map[String, String]): Outcome[BootResult[Unit, Unit]] =
    Outcome(
      BootResult(config, ())
        .withAssets(assets)
        .withFonts(fonts)
        .withAnimations(animations)
        .withShaders(shaders)
    )

  def initialScene(bootData: Unit): Option[SceneName] = None

  def scenes(bootData: Unit): NonEmptyBatch[Scene[Unit, Unit]] =
    NonEmptyBatch(Scene.empty)

  def eventFilters: EventFilters = EventFilters.Permissive

  val config: EngineConfig =
    Config.config

  val assets: Set[AssetType] =
    Assets.assets.assetSetRelative

  val fonts: Set[FontInfo]        = Set()
  val animations: Set[Animation]  = Set()
  val shaders: Set[ShaderProgram] = Set()

  def setup(bootData: Unit, assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Unit] =
    Outcome(())

  def updateModel(context: Context, model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  /** In this example, we set up four graphics with a bitmap material. The graphics are 128x128 in
    * size, but the texture is only 64x64. Each one then has a different fill type applied to its
    * material: normal (default), tile, stretch, and nineSlice.
    *
    * Just so that we can see the boundaries of each graphic, a rectangle with a green border has
    * been drawn around each one.
    */
  // ``` scala
  val material = Material.Bitmap(Assets.assets.nineslice)

  def present(context: Context, model: Unit): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        Graphic(0, 0, 128, 128, material.normal).moveTo(0, 0),
        Graphic(0, 0, 128, 128, material.tile).moveTo(128, 0),
        Graphic(0, 0, 128, 128, material.stretch).moveTo(0, 128),
        Graphic(0, 0, 128, 128, material.nineSlice(16, 16, 32, 32)).moveTo(128, 128),
        Shape.Box(Rectangle(128, 0, 128, 128), Fill.None, Stroke(1, RGBA.Green)),
        Shape.Box(Rectangle(0, 128, 128, 128), Fill.None, Stroke(1, RGBA.Green)),
        Shape.Box(Rectangle(0, 0, 128, 128), Fill.None, Stroke(1, RGBA.Green)),
        Shape.Box(Rectangle(128, 128, 128, 128), Fill.None, Stroke(1, RGBA.Green))
      )
    )
  // ```
