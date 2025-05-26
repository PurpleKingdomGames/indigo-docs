# Clip

Clips are simple, stateless animation primitives designed to play a sequence of frames of animation from a sprite sheet. Sprite sheet's exported from tools like Aseprite, for example.

Clips are much more light weight than Sprites, but are also less powerful in two particular ways:

1. They're stateless, so they just play based on the running time, you have less control over playback.
2. All the frames must be the same size and ordered linearly.

Nonetheless, this makes clips ideal for many animation use cases, and should be used in preference to sprites wherever possible.
