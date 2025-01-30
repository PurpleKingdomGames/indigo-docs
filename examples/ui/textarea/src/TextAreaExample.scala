package indigoexamples

import indigo.*
import indigo.syntax.*
import indigoextras.ui.*
import indigoextras.ui.syntax.*
import indigo.shared.subsystems.SubSystemContext.*

import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*

object CustomComponents:

  val component: TextArea[Unit] =
    TextArea[Unit]("This is just,\nsome text.", (_, _) => Bounds(0, 0, 150, 20)) {
      (coords, lines, dimensions) =>
        Outcome(
          Layer(
            lines.zipWithIndex.toBatch.map { case (line, i) =>
              TextBox(line)
                .withColor(RGBA.White)
                .moveTo(coords.unsafeToPoint)
                .withSize(dimensions.unsafeToSize)
                .withFontSize(20.pixels)
                .moveTo(coords.unsafeToPoint)
                .moveBy(0, 20 * i)
            }
          )
        )
    }

final case class Model(component: TextArea[Unit])
object Model:

  val initial: Model =
    Model(CustomComponents.component)

@JSExportTopLevel("IndigoGame")
object TextAreaExample extends IndigoSandbox[Unit, Model]:

  val config: GameConfig =
    Config.config.noResize

  val assets: Set[AssetType] =
    Assets.assets.assetSet

  val fonts: Set[FontInfo]        = Set()
  val animations: Set[Animation]  = Set()
  val shaders: Set[ShaderProgram] = Set()

  def setup(assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Model] =
    Outcome(Model.initial)

  def updateModel(context: Context[Unit], model: Model): GlobalEvent => Outcome[Model] =
    case e =>
      val ctx = UIContext(context.forSubSystems, Size(1), 1)
        .moveBoundsBy(Coords(50, 50))

      model.component.update(ctx)(e).map { c =>
        model.copy(component = c)
      }

  def present(context: Context[Unit], model: Model): Outcome[SceneUpdateFragment] =
    val ctx = UIContext(context.forSubSystems, Size(1), 1)
      .moveBoundsBy(Coords(50, 50))

    model.component
      .present(ctx)
      .map(l => SceneUpdateFragment(l))
