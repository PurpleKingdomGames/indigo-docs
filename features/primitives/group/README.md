# Group

Groups are a handy little mechanism to treat a collection of scene nodes as a single entity for the purposes of arranging things on the screen.

For example, if you have a character with replaceable / swappable head wear - perhaps an exciting collection of hats - then what you'd like to do is place the character and the current hat relative to one another, and then move them both together, as one.

> It should be noted that although groups are not visible entities, they are not zero cost in terms of rendering performance. Use groups, by all means, but make sure every group you add has value and pack as much stuff into it as possible to amortize the cost of use.
