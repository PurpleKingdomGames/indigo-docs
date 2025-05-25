# Indigo vs Elm

Indigo adopts the spirit of the Elm Architecture's model-update-view separation and unidirectional data flow, but diverges in several practical and philosophical ways to better suit game development, and Scala’s capabilities.

## 1. Frame based

- **Elm** is event-driven: updates happen in response to user events or commands.
- **Indigo** is 'frame-driven': It still works on events, but there is _always_ a `FrameTick` event, meaning that `update` and `present` are called at least once every frame (e.g. 60 FPS), more like a traditional game loop.

This is because while web applications can often be in an idle state awaiting user input, most games are always doing something, such as moving NPCs around or rendering background animations.

## 2. Separated `updateModel` and `updateViewModel`

- **Elm** has a single `update` function.  
- **Indigo** splits updates into:
  1. `updateModel`: updates game logic.
  1. `updateViewModel`: updates rendering-related state (e.g. animation, effects).

This improves separation of concerns and avoids mixing game state with presentation.

## 3. Global events for message passing

- **Elm** uses messages (Msg) to model everything from UI events to time updates, and since `Msg`s are user defined (as an ADT, typically), they can be exhaustively checked.
- **Indigo** uses `GlobalEvent` (an extendable `trait`) as the unified mechanism for message passing between custom processes, engine systems, and world events. This means messages cannot be exhaustively checked without some work by the game developer, but does allow for more flexibility.

## 4. Outcome instead of Cmd & Sub

- **Elm** uses `Cmd` and `Sub` to model side effects.  
- **Indigo** uses `Outcome`, which holds both updated state changes and captures events, but _not_ side effects.

Web frameworks (for which Elm was designed) are surprisingly I/O heavy, but our expectation is that, in generally, this isn't the case with games. Certainly you might need networking and input from the player (mouse, keyboard, etc), but these are all expected and built-in by default via the events system. More important, is capturing the state and the inevitable events in an elegant and ordered way, as `Outcome` does.

If you have a need to make a game with a lot of I/O for some reason, you can always embed your Indigo game into a Tyrian app, and let Tyrian do the heavy lifting since it follows the classic Elm approach.

## 5. Context for per-frame information

Indigo introduces the notion of frame `Context` (not found in Elm) which provides access to regularly used, per-frame data and services including but not limited to:

- `Context.Frame` - referentially transparent information
  1. Elapsed and running time  
  1. Input state (keyboard, mouse, gamepad)  
  1. Access to pseudo random number generation (`Dice`)
  1. View port information
- `Context.Services` - stateful services and helpers (use with care, these will make testing more complicated)
  1. Real (i.e. stateful) random number generation
  1. A visual object size / boundary calculator
  1. Screen capture
- Additional information such as startup data.

This avoids needing to wire such data through your own model, keeping your game logic pure and focused.

There are some variations of the `Context` type, for example `SubSystemContext` and `SceneContext` that provide additional information suited to their relevant domains, but they always come with the standard frame data and services.
