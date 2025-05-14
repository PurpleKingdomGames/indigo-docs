# A basic custom shader

Indigo comes with a number of rendering options and materials, but sometimes you need a custom effect, and this is where shaders come into play.

Indigo's rendering is done using WebGL 2.0, and the shader language of WebGL 2.0 is called GLSL, specifically version 300. While it is possible to write shaders for Indigo in GLSL 300 as a string (possibly loaded at compile time via Indigo's generators), Indigo itself relies on Ultraviolet, a Scala 3 to GLSL transpiler, to do the heavy lifting, and you can use it too for a fun and pleasant shader writing experience.

See [Ultraviolets docs](https://ultraviolet.indigoengine.io/) for more information on Shaders and shader writing.