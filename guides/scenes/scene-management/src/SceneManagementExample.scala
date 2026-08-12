package indigoexamples

/** ## How to manage game scenes
  *
  * In this example we'll set up a simple game with two scenes, where clicking anywhere moves from
  * one scene to the next.
  */

import indigo.*
import indigo.scenes.*

import generated.*

/** Scene's can either use the main game model and view model, or they can have their own. Custom
  * models are made up of data stored in the main models. In this case, we'll define a simple model
  * for each.
  */
// ```scala
final case class SceneModelA(value: String)
final case class SceneModelB(value: String)
// ```

final case class BootData()
object BootData:
  val empty: BootData =
    BootData()

final case class StartUpData(messageA: String, messageB: String)
object StartUpData:
  val initial: StartUpData =
    StartUpData("Scene A!", "Scene B?")

final case class Model(sceneA: SceneModelA, sceneB: SceneModelB)
object Model:
  def initial(startupData: StartUpData): Model =
    Model(
      sceneA = SceneModelA(startupData.messageA),
      sceneB = SceneModelB(startupData.messageB)
    )

final case class ViewModel()
object ViewModel:
  val initial: ViewModel =
    ViewModel()

/** Scenes are simple objects then extend the Scene type.
  *
  * Scenes are another instance of the TEA patterm themselves follow a very pattern to the main game
  * functions, defining their own `updateModel`, `updateViewModel`, and `present` and so on.
  */
// ```scala
object SceneA extends Scene[Model]:
// ```

  val name: SceneName = SceneName("A")

  /** The way that scene's get their own models and view models is via 'lenses'.
    *
    * First We tell the scene what the type of it's own model and view models are. We then define
    * lenses that tell the scene how to 'get' and 'set' those models from the main game models.
    *
    * Scene models can be a piece of data that lives in a field on the main model, as in this
    * example. They can also be ephemeral data types aggregated from various main model data points
    * and pulled together each frame, as needed.
    */

  // ```scala
  type SceneModel = SceneModelA

  val modelLens: Lens[Model, SceneModelA] =
    Lens(
      model => model.sceneA,
      (model, newMessage) => model.copy(sceneA = newMessage)
    )

  // ```

  val eventFilters: EventFilters = EventFilters.Permissive

  val subSystems: Set[SubSystem[Model]] = Set()

  /** There are a number of `SceneEvent`s that you can experiment with, here we listen to the
    * `SceneChange` event and use the `JumpTo` event to go to the scene we want, but there are
    * others such as `Next` and `Previous` to explore.
    */
  // ```scala
  def updateModel(
      context: SceneContext,
      sceneModel: SceneModelA
  ): GlobalEvent => Outcome[SceneModelA] =
    case SceneEvent.SceneChange(from, to, at) =>
      println(s"A: Changed scene from '${from}' to '${to}' at running time: ${at}")
      Outcome(sceneModel)

    case MouseEvent.Click(_) =>
      Outcome(sceneModel)
        .addGlobalEvents(SceneEvent.JumpTo(SceneB.name))

    case _ =>
      Outcome(sceneModel)
  // ```

  def present(
      context: SceneContext,
      sceneModel: SceneModelA
  ): Outcome[SceneUpdateFragment] =
    val text: Text[Material.Bitmap] =
      Text(
        sceneModel.value,
        20,
        20,
        DefaultFont.fontKey,
        Assets.assets.generated.DefaultFontMaterial
      )

    Outcome(
      SceneUpdateFragment(
        LayerKey("demo") -> Layer.Content(text)
      )
    )

object SceneB extends Scene[Model]:

  val name: SceneName = SceneName("B")

  type SceneModel = SceneModelB

  val modelLens: Lens[Model, SceneModelB] =
    Lens(
      model => model.sceneB,
      (model, newMessage) => model.copy(sceneB = newMessage)
    )

  val eventFilters: EventFilters = EventFilters.Permissive

  val subSystems: Set[SubSystem[Model]] = Set()

  def updateModel(
      context: SceneContext,
      sceneModel: SceneModelB
  ): GlobalEvent => Outcome[SceneModelB] =
    case SceneEvent.SceneChange(from, to, at) =>
      println(s"B: Changed scene from '${from}' to '${to}' at running time: ${at}")
      Outcome(sceneModel)

    case MouseEvent.Click(_) =>
      Outcome(sceneModel)
        .addGlobalEvents(SceneEvent.First)

    case _ =>
      Outcome(sceneModel)

  def present(
      context: SceneContext,
      sceneModel: SceneModelB
  ): Outcome[SceneUpdateFragment] =
    val text: Text[Material.Bitmap] =
      Text(
        sceneModel.value,
        20,
        20,
        DefaultFont.fontKey,
        Assets.assets.generated.DefaultFontMaterial
      )

    Outcome(
      SceneUpdateFragment(
        LayerKey("demo") -> Layer.Content(text)
      )
    )

/** The main game functions of an `IndigoGame` entry point are largely the same as you'd find in an
  * `IndigoGame`, but there are two things to note:
  *
  *   1. New methods called `scenes` and `initialScene` are declared. Scene's must be declared in
  *      order in the scenes `NonEmptyBatch`, and there must be at least one scene. The initial
  *      scene is optional, if `None` then the first scene loaded will be the first one in the
  *      scene's list.
  *   2. The usual functions like `updateModel` are declared as normal, but you can now thing of
  *      them as 'global' functions. The scene's implementation of `updateModel` will only be called
  *      when that scene is running, but `updateModel` in the main game runs all the time. This is
  *      helpful for dealing with any events that are not specific to any one scene, or presenting
  *      graphics that should appear all the time.
  */
class SceneManagementExample() extends Game[BootData, StartUpData, Model]:

  def gameId: GameId = GameId("SceneManagementExample")

  // ```scala
  def scenes(bootData: BootData): NonEmptyBatch[Scene[Model]] =
    NonEmptyBatch(SceneA, SceneB)

  def initialScene(bootData: BootData): Option[SceneName] = Option(SceneA.name)
  // ```

  def eventFilters: EventFilters =
    EventFilters.Permissive

  def boot(flags: Map[String, String]): Outcome[BootResult[BootData, Model]] =
    Outcome(
      BootResult(
        Config.config
          .withClearColor(RGBA.fromHexString("0xAA3399")),
        BootData.empty
      )
        .withFonts(DefaultFont.fontInfo)
        .withAssets(Assets.assets.assetSetRelative ++ Assets.assets.generated.assetSetRelative)
    )

  def setup(
      bootData: BootData,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[StartUpData]] =
    Outcome(Startup.Success(StartUpData.initial))

  def initialModel(startupData: StartUpData): Outcome[Model] =
    Outcome(Model.initial(startupData))

  // ```scala
  def updateModel(context: Context, model: Model): GlobalEvent => Outcome[Model] =
    case _ => Outcome(model)
  // ```

  def present(
      context: Context,
      model: Model
  ): Outcome[SceneUpdateFragment] =
    Outcome(SceneUpdateFragment.empty)
