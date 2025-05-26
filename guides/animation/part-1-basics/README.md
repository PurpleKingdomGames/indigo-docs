# Part 1: Animation Fundamentals

There are essentially two forms of animation in Indigo.

1. Animations you have imported from a third party tool.
2. Animations you have described in code, known as procedural animations.

## Importing animations

At the time of writing, the only built-in support for importing animations, is from [Aseprite](https://www.aseprite.org/) (an excellent pixel art editor and animation tool) into Indigo as a Sprite or a Clip. For working examples of this, please see the relevant section on Sprites and Clips.

Aseprite animations can either be embedded using Indigo's built-in generators, or loaded from JSON data at runtime. Contributions to support other formats are welcome!

## Procedural Animations

This series of guides explores on how to describe your animations in code.

To get started, we're going to look at the fundamentals.

## Movies vs Video Games

**Question:** What do the earliest animations, from physical zoetropes to early animations on cellular film have in common with the latest and greatest 3D animated movie extravaganza?

The answer is: A fixed frame rate, say 24 frames per second (the standard for theatrical animation screenings at one time).

That means that if you want to animate something moving from one side of the screen to the other, at constant speed in a duration of 1 second, you need to move it exactly 1/24th of the total distance on every frame, for 24 frames.

Sounds good! Doing that in code is as simple as doing this every frame:

```scala
val increment = totalDistance / 24

sprite.moveBy(increment, 0)
```

The problem with this solution is that video games are not movies, and they DO NOT have a consistent frame rate. Each frame will take slightly less or more time to process and render than the last frame. Using the method above will result in jerky and jittery animation.

## Frame time deltas to the rescue

Luckily, there is an easy solution, all we need to do is multiple the desired number of units (i.e. distance) per second (in our case, the total distance) by the amount of time that has elapse since the last frame was processed.

You may recall from school that `speed = distance / time`, well all we're going to do is re-arrange that to `distance = speed * time` where distance is how far we need to move our sprite, speed is the desired distance to move every second, and time is therefore the frame delta. Which gives us something like:

```scala
sprite.moveBy(totalDistance * frameDelta, 0)
```
