ProScene.droid
========

# Description

**ProScene** is a Android [ProScene](http://nakednous.github.io/projects/proscene/) port.

**ProScene.droi** is a library to implement easily the control of interactive 2D and 3D scenes in Android with [Processing](http://processing.org).

# Key features

* Inherits the features of Proscene
* Default interactivity to your *Processing* scenes through the Touchscreen and Android keyboard that simply does what you expect.
* Handy set of complete documented examples that illustrates the use of the package.
* Released under the terms of the (GPL-v3)[http://www.gnu.org/copyleft/gpl.html].

# Usage

All library features requires a `DroidScene` object (which is the main package class) to be instantiated (usually within your sketch setup method). There are three ways to do that:

1. **Direct instantiation**. In this case you should instantiate your own Scene object at the `PApplet.setup()` function.
2. **Inheritance**. In this case, once you declare a `DroidScene` derived class, you should implement `proscenium()` which defines the objects in your scene. Just make sure to define the `PApplet.draw()` method, even if it's empty.
3. **External draw handler registration**. You can even declare an external drawing method and then register it at the Scene with `addDrawHandler(Object, String)`. That method should return `void` and have one single `DroidScene` parameter. This strategy may be useful when you have the same drawing code shared among multiple viewers.

See the examples **BasicUse**, **AlternativeUse**, and **StandardCamera** for an illustration of these techniques. To get start using the library and learn its main features, have a look at the complete set of well documented examples that come along with it.


