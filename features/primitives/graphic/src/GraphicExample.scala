package indigoexamples

import indigo.*
import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*

/** This example shows how to create a simple graphic and display it on the screen. It demonstrates
  * a number of transformations.
  */
@JSExportTopLevel("IndigoGame")
object GraphicExample extends IndigoSandbox[Unit, Unit]:

  val config: GameConfig =
    Config.config.noResize

  val assets: Set[AssetType] =
    Assets.assets.assetSet

  val fonts: Set[FontInfo]        = Set()
  val animations: Set[Animation]  = Set()
  val shaders: Set[ShaderProgram] = Set()

  def setup(assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Unit] =
    Outcome(())

  def updateModel(context: Context[Unit], model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  /** The graphic in this example has been setup using an ImageEffects material that allows you to
    * set properties like transparency (alpha). However you have also use the Bitmap material for
    * simpler rendering use cases.
    *
    * The graphic also has a reference point set, which is used to determine the origin of the
    * graphic. By default the origin is in the top left corner of the graphic, meaning that if you
    * do `graphic.moveTo(10, 10)`, the graphics top left will be placed at 10, 10. If you set the
    * reference point to the center of the graphic, then the center of the graphic will be placed at
    * pixel coordinates (10, 10). In the example below, the reference point is set to (48, 48), so
    * that if we did the `moveTo` again it would place the graphic at (10, 10), offset by 48 pixels
    * in both directions. This is very handy for placing characters in a game. If you set the
    * reference point to be at their feet, the character will 'stand' on the moveTo coordinate.
    */
  // ```scala
  val graphic: Graphic[Material.ImageEffects] =
    Graphic(0, 0, 256, 256, Material.ImageEffects(Assets.assets.graphics))
      .withRef(48, 48)
  // ```

  /** This is another graphic that is based on the first graphic, but has had some transformations
    * including graphic's party piece, the `crop`. Setting the crop allows you to take a sub-section
    * of the image and display it.
    */
  // ```scala
  val basic: Graphic[Material.ImageEffects] =
    graphic
      .withCrop(128, 0, 96, 96)
      .moveTo(200, 200)
      .scaleBy(1.5, 1.5)
      .withRef(96, 96)
  // ```

  def present(context: Context[Unit], model: Unit): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment.empty
        .addLayer(
          basic.modifyMaterial(_.withAlpha(0.5)),
          basic
            .rotateTo(Radians(Math.PI / 8))
            .modifyMaterial(_.withAlpha(0.75)),
          basic
            .rotateTo(Radians(Math.PI / 4))
            .modifyMaterial(_.withAlpha(0.75)),
          graphic
            .withCrop(128, 0, 96, 96)
            .moveTo(137 * 1, 100),
          graphic
            .withCrop(128, 0, 96, 96)
            .moveTo(137 * 2, 100)
            .rotateTo(Radians(Math.PI / 4)),
          graphic
            .withCrop(128, 0, 96, 96)
            .moveTo(137 * 3, 100)
            .flipHorizontal(true)
            .flipVertical(true),
          graphic
            .withCrop(128, 0, 96, 96)
            .moveTo(137 * 1, 300)
            .modifyMaterial(_.withAlpha(0.5)),
          graphic
            .withCrop(128, 0, 96, 96)
            .moveTo(137 * 2, 300)
            .modifyMaterial(_.withTint(RGBA.Red)),
          graphic
            .withCrop(128, 0, 96, 96)
            .moveTo(137 * 3, 300)
            .scaleBy(2.0, 2.0)
        )
    )
