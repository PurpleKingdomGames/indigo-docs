# Aseprite Embedded

Indigo has limited support for the Aseprite JSON object data format. You can either load the JSON at runtime, or as in this example, use the Indigo generators to embed the preprocessed code directly into your code base.

Embedding provides better type safety and simplifies use, as the expense of a larger initial JS payload. The recommendation is to embed data that you need immediately (say for a loading screen), and delay loading of everything else.
