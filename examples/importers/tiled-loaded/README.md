# Loading Tiled maps at runtime

Tiled is a piece of software used to design grid based levels, there are others, and all are easily supported provided they export some readable data format. _Limited_ basic support for the Tiled format is built into Indigo.

In this example, we can see how to load tiled data at runtime as a JSON asset, and parse it into a usable map that can be rendered with the accompanying image asset.

Loading data like this as runtime has the advantage of lowering the initial payload size of your game. The drawback of this approach is the added game logic complexity needed to do the load - and if you are using the asset loader, to wait for the loading to happen, deal with any errors that might occur and store the data in your model / view model some where.

The other approach is to bake the pre-loaded tiled data into your game at build / compile time using a custom generator.
