# Making games testable

On of the goals of Indigo is to make games much more testable, and by that we mean in standard Scala unit test suites.

Of course, we can only do so much, and the plausibility of this falls to the game developer and how they write their code. All we can do is provide the tools and see what you do with them.

## Referential transparency

If you want to be able to test a single frame of a game, a whole frame, then you need one thing: Referential transparency.

> An expression is called referentially transparent if it can be replaced with its corresponding value without changing the program's behavior. ~ John C. Mitchell (2002). Concepts in Programming Languages, [via the wikipedia page on referential transparency](https://en.wikipedia.org/wiki/Referential_transparency).

**It must be noted that referential transparency on the JVM or in JS are never absolute for various reasons, so we're working with a "best endeavors" approach.**

Referential transparency allows you to ask for the next frame of a game, and compare it to the expected frame definition, confident that they will always always be equivalent, provided your expected value is correct. Which means that if you have referentially transparent frames, then you can test them! Example in made up pseudo code:

```scala
// pseudo code!

val gameTime = GameTime.is(Seconds(123))

val actual: (Model, View) =
  MyGame.calculateNextFrame(gameTime)

val expected: (Model, View) =
  (expectedModel, expectedView)

assert(actual == expected)
```

The above would only hold true if there are no side effects. The problem is that games are random, time sensitive, and usually use mutable state for better performance - all of which are normally side effecting issues.

Some of the key things that Indigo gives you:

1. Known time - each frame's logic gets one time value regardless of how long it takes to process the frame.
2. Pseudo randomness - seeded from the game's running time, but you can always find out what "random" values were used provided you use a propagated `dice` instance.
3. Immutability - the state and all inputs to a frame are immutable, leading to consistent results.
4. Side effect free, declarative APIs - since your state is immutable you must describe what you'd like to happen next, rather than being able to directly action it now. This all but eliminates race conditions.
5. Predictable scene composition - `SceneUpdateFragment`s are combined very simply allowing you to test the view description in an ordinary unit test.

## "Your whole game as a single, pure, stateless function."

The default interfaces you are presented with as part of Indigo's framework offer a range of functions and values that you need to decide how to implement, but that's all just there to improve the user experience.

Beneath the APIs of the entry points is a _single function_ that looks a bit like this:

```scala
import indigo.*

final case class Model(count: Int)
final case class ViewModel(position: Point)

def run(
      model: Model,
      viewModel: ViewModel,
      gameTime: GameTime,
      globalEvents: Batch[GlobalEvent],
      inputState: InputState,
      dice: Dice,
      boundaryLocator: BoundaryLocator
  ): Outcome[(Model, ViewModel, SceneUpdateFragment)] = ???
```

The point of this function is purity: What you get out, should be a result of what you put in and nothing else.

> Scala is an impure functional programming language, so you are not restricted to writing games that obey these notions of purity and referential transparency in the name of, say, performance - but you should start there.

##  Frame inputs are largely immutable and predictable

It will come as no surprise to Scala functional programmers, but the aim is that as many of the inputs to the run function above are immutable and side effect free. You can access them and read from them but you can't change them. This eliminates a whole class of errors around race conditions during frame evaluation.

The notable exception is the context services. `Context` is some generally useful data and tools available to you on each function on each frame. There are several different types of context you will come across, but they're all a variation on the same theme, are are split into three sections:

1. Services
2. Frame
3. Everything else

`Frame` contains data like the game's running time, and also tools like `Dice` which is a cheap pseudo random number generator. Everything in `Frame` is immutable and predictable and side effect free.

`Service` are NOT side effect free. These are any tools that need to have long running state or call the browser APIs to do their work. Boundary look ups, screenshots, and a real `Random` instance for when `Dice` just won't cut it, for example.

'Everyting else' _should_ be immutable safe data.

## The view is data, not execution

The final thing to note is that the call to the `present` function doesn't draw anything. Instead, it produces a description of that you _want_ to be drawn. The implication is that, as long as you trust the renderer, you can test the presentation description in order to validate that your code is producing the correct output.
