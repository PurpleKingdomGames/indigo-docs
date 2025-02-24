# UI Components: Scroll Pane

This example shows how to set up a `ScrollPane` using Indigo's general UI system.

## What is a 'Scroll Pane'

Scroll panes have a lot in common with masked panes, but they should be familiar to anyone who has used a windowing system.

Consider viewing a web page in your browser. If the page is too long for the window to show, then you are presented with scroll bars to allow you to reach the content at the bottom of the page.

In this UI System, that scrolling functionality is _not_ built into the notion of windows, it is a standalone component called a `ScrollPane`. The original use case _is_ to providing scrolling capabilities to windows, however.

## Limitations / future enhancements

1. `ScrollPane` instances currently provide vertical scrolling only.
2. The draggable scroll button size is fixed, and does not resize proportionally to the content length and/or pane size.

## Reminder: Set the magnification

It's important to note that if you're using a magnification other than 1 (the default), you _must_ set the magnification to the correct value in the `UIContext`. Otherwise, the shader that handles the content masking / clipping will not mask off the correct area of the screen.
