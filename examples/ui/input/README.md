# UI Components: Label

This example shows how to set up a `Input` using Indigo's general UI system.

## Known issue in v0.18.0

An [issue](https://github.com/PurpleKingdomGames/indigo/issues/819) has been raised against this component. This component was developed in an environment with fixed width fonts, and the current implementation is inadequate for working with `TextBox`s. You should be fine with `Text` instances or any other means of rendering text where the char widths are consistent and known.

Hopefully we'll have this resolved soon. This is a spectacular example of how your test cases only cover the things they test!