package indigoexamples

import indigo.*
import indigoexamples.generated.Config
import indigoexamples.generated.Assets

import scala.scalajs.js.annotation.JSExportTopLevel

/** ## How to use a Sprite
  *
  * Unlike Clips, which can be used directly in the SceneUpdateFragment, Sprites require a little
  * more wiring a set up. This is because they are stateful and the state needs to be stored and
  * referenced.
  */
@JSExportTopLevel("IndigoGame")
object SpriteExample extends IndigoDemo[Unit, Unit, Unit, Unit]:

  /** This first thing we need is a definition of the animation. That is, data that tells Indigo
    * where each frame lives spactially on the sprite sheet / texture.
    *
    * The animation data can be made manually, as in this example, but Indigo also supports building
    * the data automatically based on data from Aseprite (see other examples).
    *
    * Along with the frame information, the animation also needs a key so that we can look it up.
    *
    * The example below represents a single animation, but you can add many different animations
    * cycles and switch to them as and when required. For example, one Animation instance could hold
    * all the cycles for a character to allow them to be idle, jump, walk, and crawl.
    */
  // ```scala
  val flagAnimationKey: AnimationKey = AnimationKey("flag-animation")

  val flagAnimation: Animation =
    val frameDuration = Millis(100)
    val frameSize     = Rectangle(64, 128)

    Animation(
      flagAnimationKey,
      Frame(frameSize.moveTo(64 * 0, 0), frameDuration),
      Frame(frameSize.moveTo(64 * 1, 0), frameDuration),
      Frame(frameSize.moveTo(64 * 2, 0), frameDuration),
      Frame(frameSize.moveTo(64 * 3, 0), frameDuration),
      Frame(frameSize.moveTo(64 * 4, 0), frameDuration),
      Frame(frameSize.moveTo(64 * 5, 0), frameDuration),
      Frame(frameSize.moveTo(64 * 6, 0), frameDuration),
      Frame(frameSize.moveTo(64 * 7, 0), frameDuration),
      Frame(frameSize.moveTo(64 * 8, 0), frameDuration)
    )
  // ```

  val eventFilters: EventFilters =
    EventFilters.Permissive

  /** In order to use the animation, we must register them either at boot time, or if the assets are
    * dynamically loaded then on the start up object during the setup function.
    */
  // ```scala
  def boot(flags: Map[String, String]): Outcome[BootResult[Unit, Unit]] =
    Outcome {
      val config =
        Config.config
          .withMagnification(2)
          .noResize

      BootResult
        .noData(config)
        .withAssets(Assets.assets.assetSet)
        .withAnimations(flagAnimation)
    }
  // ```

  def setup(
      bootInfo: Unit,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[Unit]] =
    Outcome(Startup.Success(()))

  def initialModel(startupData: Unit): Outcome[Unit] =
    Outcome(())

  def initialViewModel(startupData: Unit, model: Unit): Outcome[Unit] =
    Outcome(())

  def updateModel(context: Context[Unit], model: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(model)

  def updateViewModel(
      context: Context[Unit],
      model: Unit,
      viewModel: Unit
  ): GlobalEvent => Outcome[Unit] =
    _ => Outcome(viewModel)

  /** The AnimationKey is needed to look up the Animation data, but we also need a way to look up
    * the state for this particular instance. The animation state holds details such as which frame
    * of the animation this sprite is currently on. This is done with a BindingKey.
    *
    * > Important! You can have many sprites share the same binding key, and that means, they all
    * share the same state, and all play the same animation at the same time. Update one and you
    * update all.
    */
  // ```scala
  val mySprite = BindingKey("my-flag-sprite")
  // ```

  /** Finally, we can use our sprite, and tell it to play the animation or jump to a particular
    * frame and so on.
    *
    * Note that calling the animation control functions, `play` for example, is declarative and
    * tells Indigo that this animation should be played. It does not perform a side effect and
    * change any state directly.
    */
  // ```scala
  def present(
      context: Context[Unit],
      model: Unit,
      viewModel: Unit
  ): Outcome[SceneUpdateFragment] =
    val sprite =
      Sprite(mySprite, 0, 0, flagAnimationKey, Assets.assets.FlagMaterial).play()

    Outcome(SceneUpdateFragment(sprite))
  // ```
