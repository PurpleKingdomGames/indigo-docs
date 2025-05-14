# UI Components: Windows

In this example, we look at how to set up and manage a window using the Window Manager sub-system.

The window manager takes away a lot of the complexity of managing a known set of windows. Allowing you to perform actions like opening, closing, focusing, and moving them around, using a simple set of events.

Windows themselves are little more than containers for a component (which could itself be a group of more components) that provide the space on the screen for the components to live in. However that is all that they are. They provide no UI functionality on their own beyond optionally drawing a background, and any controls you might expect to see, such as a close button for example, need to be built and added manually.

If you expect to have a lot of windows that look the same, you can build an abstraction over the window to provide common functionality, and example of this can be found in the [roguelike-starterkit](https://github.com/PurpleKingdomGames/roguelike-starterkit), which provides simple Terminal / ASCII style windows. This UI system is supposed to provide the bedrock for you to build upon, not provide out-of-box ready to use UIs.
