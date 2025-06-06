# Performance

## What is performance?

**Lets get one thing clear:** Indigo is not the fastest 2D game engine out there. It might even be the slowest - who knows! Nonetheless, it is definitely _fast enough_, and games programmed well can expect to run at 60 frames per second.

Performance is relative. When people talk about game engine performance, they are usually talking about how many things it can do at once.

The way raw engine performance is typically achieved is by utilizing clever data structures, mutable data stores, reusing allocated memory to keep usage constant, insisting on never creating new objects, and utilizing multi-threading.

All of that _can_ lead to higher performance, but it takes careful management since your guarantees around runtime behavior are reduced, and bugs can occur.

In order to bring performance up, Indigo itself does all this too. Where Indigo and normal game engines diverge is that while these techniques are used by Indigo under the covers, it is strongly discouraged at the API level. Why is that?

What if you could build games with an expectation of no bugs? Games that you _know_ work before you play them because you can bring to bear an advanced type system, property based testing, TDD (without excessive mocking), and any other code quality tool you can think of?

Couldn't developer productivity be considered a type of performance too? The design aim of Indigo is to take an engine performance hit at the API level, in order to increase developer performance (easy to reason about, easy to test) and game production efficiency.

_Please note: You're still going to have to play your game at some point. No automated test suite in the world will tell you if you've made something fun or not!_

## Is it fast enough?

_TL;DR: Yes, but..._

Two slightly disappointing observations that we've made about 2D games during Indigo's development:

1. Most 2D games on the market don't actually have that many elements on screen at any one time - there's only so much screen real estate available for use when you have no real perspective or depth. (Naturally there are exceptions)
2. Most game logic is quite straight forward, and revolves around a few core mechanics. The complexity is in how the elements interact.

With that in mind then: Indigo is fast enough that you should not have to do anything clever or special to be able to hit a respectable frame rate for simple games.

That said, some games and game developers always have and always will push what can be done by the system and engine they run on to the edge of what is possible. If you do that with your game, you may need to roll up your sleeves and think of clever ways to solve problems efficiently, but that is a high quality problem to have.

### Start on the assumption that it will be fast enough

In general, you should start by assuming Indigo will be fast enough. Build your game using good functional programming practices, and write your tests.

You should only consider changing things if it really isn't fast enough when you play / performance test it. Remember that it's always easier to make clean, well written code go faster, than trying to improve the performance of something messy.

### One weird trick to grind Indigo to a halt

Try and render 1000 standard primitives of any type, better yet, put each one on its own layer. Performance will be bad!

Is 1000 a lot? It depends on what you're trying to render. If you want to render a standard roguelike map of 80x50 tiles - which is 4000 tiles - then it's not nearly enough! If you're rendering a little platformer then 1000 is probably ample.

The problem is not the requirement, it's the approach. Naively rendering 1000 things will make performance suffer, but choosing a different data structure or rendering primitive, such as clones, will allow you to put more things on the screen.

## Performance Tips: How to get more speed

Depending on what kind of thing you're doing, you can make Indigo do more in less time.

Indigo is single threaded and runs in the browser. JavaScript code execution these days is very fast, and your game logic draining CPU power is probably not a concern, nor is available memory.

**Your main enemy in the quest for performance is memory allocation and the subsequent garbage collection pauses.**

The near universal mantra of game developers is to never allocate memory, i.e. create new objects, during a frame. Ever. **Reuse, reuse, reuse.**

Unfortunately, Indigo is built on Scala offering up an immutable API, so we're going to be making new objects _all the time_.

Generally your performance will suffer the more things you add to the screen. Our goal is to get the same effect you get by adding lots of things, but by doing less work. Here are a few tips for squeezing out some extra juice.

### Measure twice, cut once

> Be Aware: Running profiling tooling is, itself, expensive! You're game might do 58 FPS normally and 47 under profiling. Try plugging in the FPS Counter `SubSystem` for another view, or finding your browsers built in FPS viewer if it has one.

Modern browsers have amazing profiling tools built into them these days. Get to know them by recording the performance of your running game, and then look for evidence of where you're creating things like GC pauses or large numbers of allocations, and try to track down the culprit code.

If you aren't improving the slowest bottleneck of your game code, you aren't improving anything.

### If you can't see it, don't draw it

Normally when you create a game element like a sprite in a conventional game engine, they hang around until you remove them. Indigo only draws what your `SceneUpdateFragment` describes **as a result of the `present` function**, nothing hangs around between frames. As such, Indigo assumes you know what you're doing and doesn't try to do any clever interpretation of your scene.

### Render batch size

>WebGL 2.0 only!

There is a batch size option in the main game config that you can tweak, it relates to the maximum number of renderable items that are bundled up before a draw call is issued. It's a trade off: Bigger batches mean more memory usage but fewer calls to the graphics card.

In general, fiddling with this value will only affect games with a lot of scene elements.

### Use Clones, CloneTiles, and Mutants

>WebGL 2.0 only!

`Clones`, `CloneTiles` and `Mutants` are Indigo's version of what is known as "instancing". Say you want to render lots (lots and lots and lots!) of things that are more or less identical, such as blades of grass or tiles in a tile map, you should consider using one of these types instead of normal primitives.

### Cache values

May seem obvious but some values are just expensive to work out. Object boundaries for example, particularly text boundaries or groups with many elements, can be quite expensive to calculate. If you (as the game programmer) know that a value is going to be used a lot but is never going to need to be recalculated, consider storing it and looking it up next time. In the example of boundaries, a good place to store it might be as part of the view model.

#### Reuse animations

One way to reduce the cost of animated elements is to reuse them!

**Example scenario:** You have a number of background elements, and it's acceptable for all of them to be on the same animation loop at the exact same frame position (e.g. you have a forest and 25% of the trees are identical), but you want to be able to interact with them individually (thus ruling out clones).

1. Establish a master sprite;
2. Give all of the sprites the same animation key;
3. Only update the animation of the master sprite;
4. All the others will be animated identically but with no additional event or animation processing cost.

#### Animate with Clips

If possible, swap Sprites for Clips which are much cheaper to run.

### Faster processing

In terms of view processing, memory allocations are typically the problem, but you can also experience CPU bottlenecks depending on the kind of game you're building.

#### Use appropriate data structures

In many cases, a simple array of things to process will do. However, just as an example, if you're processing something spacial like collision detection or available moves on large playing grid, you should look for data structures that can efficiently ignore / avoid processing irrelevant areas, such as a [BSP](https://en.wikipedia.org/wiki/Binary_space_partitioning) or [Quadtree](https://en.wikipedia.org/wiki/Quadtree) structures.

### Use `Batch`, not `List`

`List` in Scala.js is very slow compared to native JavaScript arrays, i.e. `js.Array`. `Batch` is a wrapper around `js.Array` that mimics some `List`-like functionality and also provides very fast concatenation, which is something we do a lot of in Indigo.

#### Use `UpdateList`

Split expensive calculation work over multiple frames.

**Scenario:**

You're writing a farming game and have a massive grid of crops to update. You can certainly reduce the drawing overhead using some of the techniques previously mentioned, but what about updating them all?

To help with the time element, you could use a `TimeVaryingValue` that will automatically advance the value of the crops for a given time. But what if updating all the elements is expensive - perhaps your calculation has to decide what's happening to the crop based on many environmental conditions?

If you wrap your crops up in an `UpdateList` you can specify an update pattern, for example you could update 25% of the crops this frame, and 25% on each of the next three subsequent frames until they're all done. As long as your calculation is time based, and the accuracy of when you need to know the crops are ready is acceptable to be within 4 frames of the actual completion time, you can quarter your per frame processing costs.

Note that you're still allocating for the whole grid in this scenario! Consider combining these with better data structures as discussed above for further gains.

## Don't fear mutability

Your last angle of attack is to use mutable data and imperative programming techniques.

Scala is an impure functional language and there is nothing wrong with taking advantage of that. You should always pull for purity and immutability first, but games by their very nature are always pushing the resource constraints of their system in one way or another, and finding game performance is about making trade-offs. _Sometimes_ the trade off is your purely functional sensibilities!

If you've measured and identified an area of your code that is causing a bottleneck, _sometimes_ the best solution is to roll up your sleeves and use a more imperative solution to do a bit of specific, localized, optimization. ***Never ever*** do this without profiling your code first, or you're probably wasting your time.

Remember, a function is pure and referentially transparent as long as for a given set of arguments you always get the same result - there are no limits on how your implementation makes that happen. Using a while loop and a var is not against the rules! Just write good tests, and use strong encapsulation.
