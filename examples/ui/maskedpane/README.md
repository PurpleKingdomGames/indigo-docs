# UI Components: Masked Pane

This example shows how to set up a `MaskedPane` using Indigo's general UI system.

## What is a 'Masked Pane'

Pane, as in a pane of glass. A rectangular transparent sheet you can see through, but not a 'window' all by itself.

Masking (also sometimes known as clipping) is used to show content within a defined area, and hide any content outside of that area. An example in familiar GUI terms: If you had a window (with scrolling disabled) and you resized it to be smaller than the content, you'd expect the content outside it not to show.

A masked pane is in fact the same as a scroll pane, without the scrolling.

Neither masked panes nor scroll panes have the sort of layout control you get with component groups and lists, but you can always place one of those inside the pane.
