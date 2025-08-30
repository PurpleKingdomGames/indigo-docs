package indigoexamples

/** ## How to manage game scenes
  *
  * In this example we'll set up a simple game with two scenes, where clicking anywhere moves from
  * one scene to the next.
  */

import indigo.*
import indigo.scenes.*

import generated.*

import scala.scalajs.js.annotation.*

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
object SceneA extends Scene[StartUpData, Model, ViewModel]:
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
  type SceneModel     = SceneModelA
  type SceneViewModel = ViewModel

  val modelLens: Lens[Model, SceneModelA] =
    Lens(
      model => model.sceneA,
      (model, newMessage) => model.copy(sceneA = newMessage)
    )

  val viewModelLens: Lens[ViewModel, ViewModel] =
    Lens.keepLatest
  // ```

  val eventFilters: EventFilters = EventFilters.Permissive

  val subSystems: Set[SubSystem[Model]] = Set()

  /** There are a number of `SceneEvent`s that you can experiment with, here we listen to the
    * `SceneChange` event and use the `JumpTo` event to go to the scene we want, but there are
    * others such as `Next` and `Previous` to explore.
    */
  // ```scala
  def updateModel(
      context: SceneContext[StartUpData],
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

  def updateViewModel(
      context: SceneContext[StartUpData],
      sceneModel: SceneModelA,
      sceneViewModel: ViewModel
  ): GlobalEvent => Outcome[ViewModel] =
    _ => Outcome(sceneViewModel)

  def present(
      context: SceneContext[StartUpData],
      sceneModel: SceneModelA,
      sceneViewModel: ViewModel
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
      SceneUpdateFragment(text)
    )

object SceneB extends Scene[StartUpData, Model, ViewModel]:

  val name: SceneName = SceneName("B")

  type SceneModel     = SceneModelB
  type SceneViewModel = ViewModel

  val modelLens: Lens[Model, SceneModelB] =
    Lens(
      model => model.sceneB,
      (model, newMessage) => model.copy(sceneB = newMessage)
    )

  val viewModelLens: Lens[ViewModel, ViewModel] =
    Lens.keepLatest

  val eventFilters: EventFilters = EventFilters.Permissive

  val subSystems: Set[SubSystem[Model]] = Set()

  def updateModel(
      context: SceneContext[StartUpData],
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

  def updateViewModel(
      context: SceneContext[StartUpData],
      sceneModel: SceneModelB,
      sceneViewModel: ViewModel
  ): GlobalEvent => Outcome[ViewModel] =
    _ => Outcome(sceneViewModel)

  def present(
      context: SceneContext[StartUpData],
      sceneModel: SceneModelB,
      sceneViewModel: ViewModel
  ): Outcome[SceneUpdateFragment] =
    val text: Text[Material.Bitmap] =
      Text(
        sceneModel.value,
        20,
        20,
        DefaultFont.fontKey,
        Assets.assets.generated.DefaultFontMaterial
      )

    Outcome(SceneUpdateFragment(text))

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
@JSExportTopLevel("IndigoGame")
object SceneManagementExample extends IndigoGame[BootData, StartUpData, Model, ViewModel]:

  // ```scala
  def scenes(bootData: BootData): NonEmptyBatch[Scene[StartUpData, Model, ViewModel]] =
    NonEmptyBatch(SceneA, SceneB)

  def initialScene(bootData: BootData): Option[SceneName] = Option(SceneA.name)
  // ```

  def eventFilters: EventFilters =
    EventFilters.Permissive

  def boot(flags: Map[String, String]): Outcome[BootResult[BootData, Model]] =
    Outcome(
      BootResult(
        Config.config.noResize
          .withClearColor(RGBA.fromHexString("0xAA3399")),
        BootData.empty
      )
        .withFonts(DefaultFont.fontInfo)
        .withAssets(Assets.assets.assetSet ++ Assets.assets.generated.assetSet)
    )

  def setup(
      bootData: BootData,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[StartUpData]] =
    Outcome(Startup.Success(StartUpData.initial))

  def initialModel(startupData: StartUpData): Outcome[Model] =
    Outcome(Model.initial(startupData))

  def initialViewModel(startupData: StartUpData, model: Model): Outcome[ViewModel] =
    Outcome(ViewModel.initial)

  // ```scala
  def updateModel(context: Context[StartUpData], model: Model): GlobalEvent => Outcome[Model] =
    case _ => Outcome(model)
  // ```

  def updateViewModel(
      context: Context[StartUpData],
      model: Model,
      viewModel: ViewModel
  ): GlobalEvent => Outcome[ViewModel] =
    case _ => Outcome(viewModel)

  def present(
      context: Context[StartUpData],
      model: Model,
      viewModel: ViewModel
  ): Outcome[SceneUpdateFragment] =
    Outcome(SceneUpdateFragment.empty)
