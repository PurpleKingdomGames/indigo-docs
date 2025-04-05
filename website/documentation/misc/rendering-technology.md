# Rendering Technology

At the time of writing, there are five options for rendering 2D graphics into a browser page, if you are so inclined:

1. HTML + CSS + SVG
2. Canvas APIs (2D)
3. WebGL 1.0
4. WebGL 2.0
5. WebGPU

## Indigo uses WebGL 2.0

Indigo's primary rendering technology is WebGL 2.0, the most modern 3D rendering api available to the majority of browsers.

By default, Indigo will attempt to detect whether WebGL 2.0 is supported, and if not, will fall back to a WebGL 1.0 renderer. You can also force it to use one or the other.

The WebGL 1.0 renderer has been reduced to the bare minimum so that all maintenance efforts can go into the WebGL 2.0 version. The anticipated use of this renderer is to act as nothing more than a simple fall back, so you can message your players that there has been some sort of problem rendering with WebGL 2.0. That said, the WebGL 1.0 renderer does work and being so simple is very fast! You could use it as your primary renderer if you don't need anything fancy!

Renderer detection can be done by listening for the `RendererDetails` event.

## Ultraviolet and GLSL

Shaders for WebGL 1.0 / 2.0 are written using a language called GLSL, versions 100 and 300 respectively. You can use GLSL shaders with Indigo, however it is recommended that you use our library, [Ultraviolet](https://github.com/PurpleKingdomGames/ultraviolet), which allows you to write your shaders directly in Scala.