# Bitmap material

Entities in Indigo look complicated, but ultimately they just represent a space on the screen that you can manipulate, nothing more.

To draw into that space, the engine uses something called 'shaders', but shaders are a quite involved, and usually you just want some simple behaviors like drawing a shape or filling the entities space with an image.

To make things easier, Indigo has various abstractions over shaders, to produce specific types of rendering effects. For image or texture based effects, the abstraction is called a `Material`.

The `Bitmap` material is the simplest material, and simply means 'render this texture/image as-is into the entity, according to the entities rules'.
