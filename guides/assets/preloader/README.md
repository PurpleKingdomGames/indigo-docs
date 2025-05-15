# Preloader / Loading Screens

Ideally, all your games assets would be loaded instantly at start up and be immediately available. Unfortunately in the real world, particularly for web games, these assets often take time to arrive from somewhere and can suffer with things like poor network latency.

We need a nice user experience so that we don't just show and black screen and have our game look broken while the games assets load. We need a way to build a loading screen, also known as a 'preloader'.

Indigo's asset loading happens in two ways, somewhat mimicking the way Flash used to work.

1. Assets declared during the boot sequence are loaded immediately, and the game does not start until they arrive.
2. The asset bundle loader can run during the game at any time in order to download and make more assets available to the game.

In both cases, the loading process ultimately invokes another call to Indigo's `setup` function before carrying on with the game.

The idea then is:

1. Use the boot sequence asset loading mechanism to load the absolute minimum assets - just enough to be able to give the user visual feedback while we load everything else.
2. Use the asset loader to subsequently load everything all the other assets our game needs.

## Example provenance

This example is based on a stripped back version of the preloader found in the Cursed pirate demo, and so uses an additional trick: The data for the pirate animation is embedded into the game code using Indigo's generators, see the build file for details.
