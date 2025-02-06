# Text

This example shows how to include text in Indigo.

There are in fact a few methods:

1. Using the `Text` primitive shown here.
2. Using the `TextBox` primitive (not recommended, likely to be removed)
3. Using the [Roguelike-Starterkit](https://github.com/PurpleKingdomGames/roguelike-starterkit) for terminal style text and graphics.

Indigo does not yet feature 'real' font support, i.e. text rendered on your graphics hardware.

The example presented here is fairly straight-forward, however you need to also look at the `build.sc` file in the root of this project, under the `text` sub-module in order to see how the font generator works.

`Text` in Indigo is basically an image of glyphs and a `FontInfo` instance that contains the information about where each glyph is on the sheet. You can make that by hand, or you can use the generator Indigo's plugins provide in order to make that for you out of a font file.

Once you have the asset and info available, you have to register it. This allows you to use a `Text` primitive which references the asset and font info.

`Text` primitives work much like other primitives, they have to some special features for handling your text, but they render everything using the usual set of materials.