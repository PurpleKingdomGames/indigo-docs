# Part 3: Timeline Animations

In the previous part of the animations series, we looked at encoding our animations as signals and signal functions, and concluded by identifying the need for an even higher level of abstraction for when things get really complicated.

Timelines allow you to describe complicated animations, with potentially many layers and things going on over time. They are essentially made up of `SignalFunction`s that are wrapped up in a nice DSL.

## Timeline basics

Each timeline can animate one type of thing, but they and their sub components are reusable and composable.

Timeline animations are build up of 'layers' (terminology clash alert: animation layers, not visible layers) which each animate one property. If you wanted to add another animated value then you would add another layer. The two layers would then be squashed together automatically to produce the end result.

Timeline layers are each their own sequence of 'time slots', such as  `startAfter` and `animate`.  Time slots form a back-to-back chain of things to do. In the example below, there are two slots in use, but you can have as many as you like.

## Animate everything!

Timelines do now know that they are for 'animation', and can 'animate' anything at all. We just need to change our idea of animation from something like 'moving a picture' to 'producing a value over time following a series of transformations'.

For example, another interesting use of animations is to cross-fade music by animating the volume of two tracks.
