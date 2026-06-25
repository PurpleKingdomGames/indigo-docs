package indigoexamples

import indigo.*
import indigo.syntax.*
import indigoextras.ui.*
import indigoextras.ui.syntax.*

import generated.*

object CustomComponents:

  val text =
    Text(
      "",
      DefaultFont.fontKey,
      Assets.assets.generated.DefaultFontMaterial
    )

  val component: ComponentList[Int] =
    ComponentList(Dimensions(200, 150)) { (_: UIContext[Int]) =>
      (1 to 3).toBatch.map { i =>
        ComponentId("radio-" + i) ->
          ComponentGroup(BoundsMode.fixed(200, 30))
            .withLayout(ComponentLayout.Horizontal(Padding.right(10)))
            .add(
              Switch[Int](BoundsType.fixed[Int](20, 20))(
                (ctx, switch) =>
                  Outcome(
                    Layer(
                      Shape
                        .Circle(
                          switch.bounds.unsafeToRectangle.toIncircle,
                          Fill.Color(RGBA.Green.mix(RGBA.Black)),
                          Stroke(1, RGBA.Green)
                        )
                        .moveTo(ctx.parent.coords.unsafeToPoint + Point(10))
                    )
                  ),
                (ctx, switch) =>
                  Outcome(
                    Layer(
                      Shape
                        .Circle(
                          switch.bounds.unsafeToRectangle.toIncircle,
                          Fill.Color(RGBA.Red.mix(RGBA.Black)),
                          Stroke(1, RGBA.Red)
                        )
                        .moveTo(ctx.parent.coords.unsafeToPoint + Point(10))
                    )
                  )
              )
                .onSwitch { (_, _) =>
                  Batch(
                    Log("Selected: " + i),
                    ChangeValue(i)
                  )
                }
                .withAutoToggle { (ctx, _) =>
                  if ctx.reference == i then Option(SwitchState.On) else Option(SwitchState.Off)
                }
            )
            .add(
              Label[Int](
                "Radio " + i,
                (_, _) => Bounds(0, 0, 150, 18)
              ) { case (ctx, label) =>
                Outcome(
                  Layer(
                    text
                      .withText(label.text(ctx))
                      .moveTo(ctx.parent.coords.unsafeToPoint)
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

class RadioButtonsExample() extends Game[Unit, Unit, Model]:

  def gameId: GameId = GameId("RadioButtonsExample")

  def boot(flags: Map[String, String]): Outcome[BootResult[Unit, Model]] =
    Outcome(
      BootResult(Config.config, ())
        .withAssets(
          Assets.assets.assetSetRelative ++
            Assets.assets.generated.assetSetRelative
        )
        .withFonts(DefaultFont.fontInfo)
    )

  def initialScene(bootData: Unit): Option[SceneName] = None

  def scenes(bootData: Unit): NonEmptyBatch[Scene[Model]] =
    NonEmptyBatch(Scene.empty)

  def eventFilters: EventFilters = EventFilters.Permissive

  def setup(bootData: Unit, assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Model] =
    Outcome(Model.initial)

  def updateModel(context: Context, model: Model): GlobalEvent => Outcome[Model] =
    case Log(message) =>
      println(message)
      Outcome(model)

    case ChangeValue(value) =>
      Outcome(model.copy(num = value))

    case e =>
      val ctx = UIContext(context, 1)
        .moveParentBy(Coords(50, 50))
        .copy(reference = model.num)

      model.component.update(ctx)(e).map { c =>
        model.copy(component = c)
      }

  def present(context: Context, model: Model): Outcome[SceneUpdateFragment] =
    val ctx = UIContext(context, 1)
      .moveParentBy(Coords(50, 50))
      .copy(reference = model.num)

    model.component
      .present(ctx)
      .map(l => SceneUpdateFragment(l))
