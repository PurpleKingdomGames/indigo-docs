# UI Components: Radio buttons

There is no radio button UI component, so how does this work? This example is _one possible_ formulation of how you can build 'compound' components out of collections of UI primitives.

This example uses a `ComponentList` as a container for a vertical list of rows of radio buttons. The radio buttons themselves are made up of a `ComponentGroup` that contains a `Switch` and a `Label`.

In terms of layouts, all of the above will work with no special wiring, but traditional radio button groups are essentially a series of on/off switches where only one may be in the 'on' state at any one time. How will we replicate that?

The `Switch` component has a method on it called `withAutoToggle`, which can change the switch's state based on the reference data. So as usual, what we need to do is store some state (could be anything, just an `Int` here) in the model, supply the state in the `UIContext` instance as part of it's reference data (which will be propagated to all components in the hierarchy), and then use that value to auto-toggle the switch.

All that remains is to alter the model state, and we do that by emitting a `ChangeValue` event `onSwitch`, picking up the event in the model update, and performing the requested change.

For good measure, we also emit a `Log` event with a radio button is selected.
