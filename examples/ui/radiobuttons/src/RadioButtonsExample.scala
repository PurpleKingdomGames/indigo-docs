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

  val component: ComponentList[Int] =
    ComponentList(Dimensions(200, 150)) { (_: Int) =>
      (1 to 3).toBatch.map { i =>
        ComponentId("radio-" + i) ->
          ComponentGroup(BoundsMode.fixed(200, 30))
            .withLayout(ComponentLayout.Horizontal(Padding.right(10)))
            .add(
              Switch[Int, Int](BoundsType.fixed(20, 20))(
                (coords, bounds, _) =>
                  Outcome(
                    Layer(
                      Shape
                        .Circle(
                          bounds.unsafeToRectangle.toIncircle,
                          Fill.Color(RGBA.Green.mix(RGBA.Black)),
                          Stroke(1, RGBA.Green)
                        )
                        .moveTo(coords.unsafeToPoint + Point(10))
                    )
                  ),
                (coords, bounds, _) =>
                  Outcome(
                    Layer(
                      Shape
                        .Circle(
                          bounds.unsafeToRectangle.toIncircle,
                          Fill.Color(RGBA.Red.mix(RGBA.Black)),
                          Stroke(1, RGBA.Red)
                        )
                        .moveTo(coords.unsafeToPoint + Point(10))
                    )
                  )
              )
                .onSwitch { value =>
                  Batch(
                    Log("Selected: " + i),
                    ChangeValue(i)
                  )
                }
                .withAutoToggle { (_, ref) =>
                  if ref == i then Option(SwitchState.On) else Option(SwitchState.Off)
                }
            )
            .add(
              Label[Int](
                "Radio " + i,
                (_, label) => Bounds(0, 0, 150, 18)
              ) { case (offset, label, dimensions) =>
                Outcome(
                  Layer(
                    TextBox(label)
                      .withColor(RGBA.White)
                      .moveTo(offset.unsafeToPoint)
                      .withSize(dimensions.unsafeToSize)
                      .withFontFamily(FontFamily.monospace)
                      .withFontSize(16.pixels)
                  )
                )
              }
            )
      }
    }

final case class Log(message: String)    extends GlobalEvent
final case class ChangeValue(value: Int) extends GlobalEvent

final case class Model(num: Int, component: ComponentList[Int])
object Model:

  val initial: Model =
    Model(
      0,
      CustomComponents.component
    )

@JSExportTopLevel("IndigoGame")
object RadioButtonsExample extends IndigoSandbox[Unit, Model]:

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
    case Log(message) =>
      println(message)
      Outcome(model)

    case ChangeValue(value) =>
      Outcome(model.copy(num = value))

    case e =>
      val ctx = UIContext(context.forSubSystems, Size(1), 1)
        .moveBoundsBy(Coords(50, 50))
        .copy(reference = model.num)

      model.component.update(ctx)(e).map { c =>
        model.copy(component = c)
      }

  def present(context: Context[Unit], model: Model): Outcome[SceneUpdateFragment] =
    val ctx = UIContext(context.forSubSystems, Size(1), 1)
      .moveBoundsBy(Coords(50, 50))
      .copy(reference = model.num)

    model.component
      .present(ctx)
      .map(l => SceneUpdateFragment(l))
