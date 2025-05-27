# Part 2: Using Signals

In Part 1, we coded up a simple movement animation and instantly ran into complexity and state management issues. However, arguably the biggest problem in part 1, was that this stateful method of coding was messy and didn't look like it would scale very well.

Lets start over.

All we want to do is smoothly move a certain distance along the x axis, over a certain duration, say 10 seconds.

Observation: That means that how far through our animation we are, is actually a function of time, i.e. at time 0 seconds we're at the beginning of our animation, at 5 seconds we're in the middle, and at 10 seconds we've reach the end. That could be frames in a animation, position along a trajectory, time through a simulation - anything.

We should be able to decide where we are in an animation given some starting conditions and the current time.
We should be able to capture that in a pure function.
...and in that case, we should be able to _unit test animations_.

## Animating with Signals & Signal Functions

If you've explored the documentation on `Signal`s already, you'll know that a signal is just a function that, for a given time, will produce a value, like this:

```scala
def f: Seconds => A
```

> Please note that the examples that follow define all the code in the present function purely to help the flow of the document. Normally you wouldn't do that.
