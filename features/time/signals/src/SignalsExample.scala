package indigoexamples

import indigo.*
import generated.Config
import generated.Assets

import scala.scalajs.js.annotation.*

/** ## Signals in Action
  *
  * Let's set up a contrived set of signal functions and println the results. In our example
  * depending on the time, we're going to make a list of cats or a list of dogs, and vary the length
  * of the list. This is not a sensible example, and clearly you could do this in other ways, but
  * it's a nice way to illustrate how signals and signal functions work in the familar terriroty of
  * making lists.
  *
  * Signals for animation will be explored in the guides.
  *
  * > Please note that this demo does not show anything visual on the screen, please see the JS
  * console in your browser for the output.
  */
@JSExportTopLevel("IndigoGame")
object SignalsExample extends IndigoSandbox[Unit, Boolean]:

  val config: GameConfig =
    Config.config.noResize

  val assets: Set[AssetType] =
    Assets.assets.assetSet

  val fonts: Set[FontInfo]        = Set()
  val animations: Set[Animation]  = Set()
  val shaders: Set[ShaderProgram] = Set()

  def setup(
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[Unit]] =
    Outcome(
      Startup.Success(())
    )

  def initialModel(startupData: Unit): Outcome[Boolean] =
    Outcome(false)

  /** Pulse is a type of signal. Based on the time, it will produce an on/off boolean like:
    */
  // ```
  //      ____    ____    ____
  //  ___|   |___|   |___|   |___
  // ```
  // ```scala
  val signal = Signal.Pulse(Seconds(1))
  // ```

  /** Taking our 'pulse' boolean value, we can choose a range of 1 to 5, or 1 to 10. */
  // ```scala
  val makeRange: SignalFunction[Boolean, List[Int]] =
    SignalFunction { p =>
      val num = if (p) 10 else 5
      (1 to num).toList
    }
  // ```

  /** Using the same boolean, we can also decide if we're talking about cats or dogs. */
  // ```scala
  val chooseCatsOrDogs: SignalFunction[Boolean, String] =
    SignalFunction(p => if (p) "dog" else "cat")
  // ```

  /** Given our lists of numbers and our cats and dogs, we need a way to make the final result. */
  // ```scala
  val howManyPets: SignalFunction[(List[Int], String), List[String]] =
    SignalFunction { case (l, str) =>
      l.map(_.toString + " " + str)
    }
  // ```

  /** Finally, we can compose our functions together using a couple of operators:
    *
    *   1. `&&&` / `and` - run in parallel and tuple the results
    *   2. `>>>` / `andThen` - compose the functions together from left to right
    */
  // ```scala
  val signalFunction = (makeRange &&& chooseCatsOrDogs) >>> howManyPets
  // ```

  /** To run the examples, we'll pipe the pulse signal into our signal function using the pipe `|>`
    * operator.
    */
  // ```scala
  def updateModel(
      context: Context[Unit],
      hasRunOnce: Boolean
  ): GlobalEvent => Outcome[Boolean] =
    case FrameTick if !hasRunOnce =>

      val allTheDogs = (signal |> signalFunction).at(Seconds.zero)
      println(allTheDogs)
      // List("1 dog", "2 dog", "3 dog", "4 dog", "5 dog", "6 dog", "7 dog", "8 dog", "9 dog", "10 dog")

      val allTheCats = (signal |> signalFunction).at(Seconds(1))
      println(allTheCats)
      // List("1 cat", "2 cat", "3 cat", "4 cat", "5 cat")

      Outcome(true)

    case _ =>
      Outcome(hasRunOnce)
  // ```

  def present(
      context: Context[Unit],
      hasRunOnce: Boolean
  ): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment.empty
    )
