# Fill Types

`FillType`s tell Indigo how to fill the entity space when the texture and entity sizes are different.

The default behavior is `normal`, which just means render as much of the texture as you can. If the entity is too small, cut off the texture, if it's too large, just render transparent pixels outside the area.

`tile` tells Indigo to repeat the texture in all directions, while `stretch` unsurprisingly, stretches the texture to fill the space.

`nineslice` is a bit more complicated, but very handy for things like widow UIs and scaling backgrounds. You define a rectangle in the middle of the texture, and nine slice then chops the image into 9 squares where your rectangle is the center, as follows:

```
The texture is 64x64, so the center rectangle might be 32x32 at position 16x16.

material.nineSlice(Rectangle(16, 16, 32, 32))
  ________________
  |   |      |   |
  |___|______|___|
  |   |      |   |
  |   |      |   |
  |___|______|___|
  |   |      |   |
  |___|______|___|

```

When the entity scales up the corner sizes are preserved, but the sides and the middle scale, like this:

```
x       <--->       x
  _________________
  |   |       |   |
  |___|_______|___|
  |   |       |   |
^ |   |       |   | ^
| |   |  ↖ ↗  |   | |
| |   |  ↙ ↘  |   | |
˅ |   |       |   | ˅
  |___|_______|___|
  |   |       |   |
  |___|_______|___|
   
x       <---->       x

```