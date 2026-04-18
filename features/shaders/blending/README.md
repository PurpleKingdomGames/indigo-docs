# Blending layers together

![Example of blending](/img/howtos/blending.png "An example of blending two layers together")

Following on from the basic shaders example where we used a shader to render a scene entity, another use for shaders is for 'screen space effects' via 'blending'.

If you've ever used photo editing software with layers, then you're probably already familiar with the concept of blending. It's the process of telling the photo editing software (or game engine in our case) how to render or merge this layer _down_ onto the one below it.

There are two parts to the blending process. Something call the `BlendMode` (which we won't cover in this example as the usage is probably fairly niche, but it's explained well in other corners of the internet), and `BlendMaterial`s, which allow us to define a custom shader program to alter how the blending actually happens.

This example will perform a simple overlay style effect, but you can use blending to mimic light refraction, mask off areas, make fancy lighting effects, and create other 'screen space effects' where you create a visual effect that affects everything the play sees, such as growing ice when it's cold or applying water effects to all the visible water tiles.

The idea is simple, take layer A as the 'SRC' (source) input, and take layer B as the 'DST' (destination) input, and tell indigo how to use the src to _affect_ the dst. You can simply splat the src onto the dst, of course (beware transparency!). But things get more interesting when you consider that the src (layer) is really just data, and how you use that data is down to you creativity. As an example: The src _could_ be a undulating waves of noise (rendering use a custom shader onto the layer above), if you used that noise to _displace_ which pixel you read from the src, you could mimic viewing the src layer through rippling water...

See [Ultraviolets docs](https://ultraviolet.indigoengine.io/) for more information on Shaders and shader writing.