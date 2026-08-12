package indigoexamples

import indigo.*
import generated.Config
import generated.Assets

class ImageEffectsExample() extends Game[Unit, Unit, Unit]:

  def gameId: GameId = GameId("ImageEffectsExample")

  def boot(flags: Map[String, String]): Outcome[BootResult[Unit, Unit]] =
    Outcome(
      BootResult(Config.config, ())
        .withAssets(Assets.assets.assetSetRelative)
    )

  def initialScene(bootData: Unit): Option[SceneName] = None

  def scenes(bootData: Unit): NonEmptyBatch[Scene[Unit]] =
    NonEmptyBatch(Scene.empty)

  def eventFilters: EventFilters = EventFilters.Permissive

  def setup(bootData: Unit, assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Unit] =
    Outcome(())

  def updateModel(context: Context, model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  /** In this example we set up a series of graphics with an image effects material. Each one has a
    * different effect applied to it:
    *
    *   - Tint - Applies a colour tint to the texture, like looking through coloured glass.
    *   - Alpha - Changes the transparency of the texture.
    *   - Saturation - Changes the saturation of the texture, from full colour to greyscale.
    *   - Overlay - Applies a colour overlay to the texture, where the texture has a solid colour
    *     applied on, affected by the overlay amount.
    *   - Overlay (linear gradient) - As with overlay, but a linear (straight line) gradient between
    *     two colours.
    *   - Overlay (radial gradient) - As with overlay, but a radial (circular) gradient between two
    *     colours.
    */
  // ``` scala
  val material: Material.ImageEffects =
    Assets.assets.junctionboxAlbedoMaterial.toImageEffects

  val graphic: Graphic[Material.ImageEffects] =
    Graphic(40, 40, material)
      .withRef(20, 20)

  val viewCenter: Point = (Point(550, 400) / 4) + Point(0, -25)

  def present(context: Context, model: Unit): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        LayerKey("demo") -> Layer.Content(
          graphic
            .moveTo(viewCenter)
            .moveBy(0, -40)
            .modifyMaterial(_.withTint(RGBA.Red)),
          graphic
            .moveTo(viewCenter)
            .moveBy(-60, -40)
            .modifyMaterial(_.withAlpha(0.5)),
          graphic
            .moveTo(viewCenter)
            .moveBy(-30, -40)
            .modifyMaterial(_.withSaturation(0.0)),
          graphic
            .moveTo(viewCenter)
            .moveBy(30, -40)
            .modifyMaterial(_.withOverlay(Fill.Color(RGBA.Magenta.withAmount(0.75)))),
          graphic
            .moveTo(viewCenter)
            .moveBy(60, -40)
            .modifyMaterial(
              _.withOverlay(
                Fill.LinearGradient(Point.zero, RGBA.Magenta, Point(40), RGBA.Cyan.withAmount(0.5))
              )
            ),
          graphic
            .moveTo(viewCenter)
            .moveBy(-60, 10)
            .modifyMaterial(
              _.withOverlay(
                Fill.RadialGradient(
                  Point(20),
                  10,
                  RGBA.Magenta.withAmount(0.5),
                  RGBA.Cyan.withAmount(0.25)
                )
              )
            )
        )
      ).withMagnification(Magnification.x2)
    )
  // ```
