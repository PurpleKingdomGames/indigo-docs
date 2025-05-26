# Signals & Signal Functions

## Motivation

We want pure, referentially transparent, testable, procedural values that change over time, AKA, 'animations' (but we can 'animate' any value).

## Background

The goal of Indigo is to make programming games (as opposed to making them...), easier to reason about and easier to test by leveraging the good ideas that come with functional programming.

One of those good ideas that Indigo borrows is the notion of Signals and Signal Functions. Signals for animation were first proposed in the Functional Reactive ANimation (FRAN) system, but are most readily seen in [Yampa](https://github.com/ivanperez-keera/Yampa).

Indigo makes use of Signals too, though not to the same extent as FRAN or Yampa. The main difference is that Signals in Indigo are stateless and therefore somewhat limited. Nonetheless, Signals in Indigo are still interesting and useful, and provide a core building block for other systems in Indigo.

## Signals

At it's core, a signal is a very simple thing. Consider this function signature:

```scala
val f: A => B
```

What this function signature says is that when provided some value of type `A`, it _will_ produce some value of type `B`. In concrete terms, if we fix the types to known primitives, the follow example says that when given a `String`, it will return an `Int`:

```scala
val f: String => Int
```

A `Signal` of `A` is nothing more than a function that when given the current running time in `Seconds`, _will_ produce some value of type `A`.

```scala
val f: Seconds => A
```

## Signal Functions

`SignalFunction`s allow you to compose functions that operate on signals.

Signal functions are combinators, and a combinator is a function that takes a function as an argument and returns another function, like this:

```scala
// A function that takes as an argument a function that takes an
// Int and returns a String, and then returns a function that
// takes and Int and returns a Boolean
val f: (Int => String) => (Int => Boolean)
```

A Signal function takes a `Signal[A]` and returns a `Signal[B]`.

```scala
SignalFunction(f: Signal[A] => Signal[B])
```

which is really a combinator:

```scala
val f: (Seconds => A) => (Seconds => B)
```
