# Part 4: Shader Animations

In the previous sections of these animation guides we've looked at hand coding animations, abstracting over them with signals, and composing complicated presentations using timelines.

In this section, in pursuit of performance, we going to come full circle back to hand coded animations, but this time, in the form of shaders.

This part of our tour isn't going to cover everything there is to know about shaders. Shaders in Indigo are typically written using our shader library, [Ultraviolet](https://github.com/PurpleKingdomGames/ultraviolet), which has it's own [documentation site](https://ultraviolet.indigoengine.io/), complete with many Indigo based examples.

As always the complete source code for this demo is available from the link on this page, if you want to see how the whole thing is put together. Therefore in this discussion, we'll be focusing on the reasons for using shader based animations, and the nuts and bolts of how one works.

## The need for speed

If timeline animations are designed for friendly usability at the expense of performance overhead, shaders are the opposite. Super efficient, massively parallel, and really powerful; Shaders are a low level way to describe the graphics you want to draw into a region of the screen. 

Whenever you draw literally anything with Indigo - whether you know it or not - a shader is doing the work. So if you need complex graphics, animations, and performance: Shaders might be the answer.

It must also be said that while they can be complicated and contain a fair bit of maths, shader programming is very good fun once you get into it!

## Shaders, hand coding, and signals

Shaders are written in procedural code. Scala if you're using Ultraviolet, otherwise you'll be using a language called GLSL.

Despite that, philosophically, programming a shader is more like coding with signals than it is the hand coded solution we saw in part 1.

A fragment shader is not unlike a pure function that runs for every pixel of an image, and decides what color it is. It has to be pure, since it will be run in parallel, and like a pure function (or indeed a signal), it operates on some initial parameters (such as the running time) to generate its output, but can otherwise be considered stateless.*

> (* You can add your own input data to a shader using UBOs, and those will typically be based on game model state. However, shaders in Indigo _do not_ have a way to generate, store, look up, or share their own state.)
