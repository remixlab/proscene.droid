ProScene [![Version](https://img.shields.io/badge/proscene-v3.0.0--b4-brightgreen.svg)](https://github.com/remixlab/proscene/releases/download/latest/proscene.zip) [![Dependencies](https://img.shields.io/badge/dependencies-processing%203-orange.svg)](http://processing.org/) [![License](https://img.shields.io/badge/license-GPL%203-blue.svg)](http://www.gnu.org/licenses/gpl.html)
========

**Table of Contents**

- [Description](#user-content-description)
- [Key features](#user-content-key-features)
- [Origin of the name](#user-content-origin-of-the-name)
- [Usage](#user-content-usage)
- [Installation](#user-content-installation)
- [Acknowledgements](#user-content-acknowledgements)
- [Author, core developer and maintainer](#user-content-author-core-developer-and-maintainer)

# Description

**ProScene** (pronounced similar as the Czech word **"prosím"** which means **"please"**) is a free-software java library which provides classes to ease the creation of interactive 2D/3D scenes in [Processing](http://processing.org).

**ProScene** extensively uses **interactive frames**, i.e., coordinate systems that can be controlled with any [HID](http://en.wikipedia.org/wiki/Human_interface_device), allowing to easily setup an interactive 2D or 3D scene.

**ProScene** provides seemless integration with **Processing**: its API has been designed to fit that of **Processing** and its implementation has been optimized to work along side with it. It suppports all major **Processing** flavours: Desktop, JS, and Android.

**ProScene** support is led by the active and great Processing community at its [forum](http://forum.processing.org/search/proscene) where you can reach us. News and technical details are found at our [blog](http://otrolado.info).

# Key features

* *Tested* under Linux, Mac OSX and Windows, and properly works with the JAVA2D, P2D and P3D **Processing** renderers. No special dependencies or requirements needed (apart of course from [Processing-3.x](https://github.com/processing/processing/releases)).
* It supports all major **Processing** flavours: [Desktop](https://github.com/remixlab/proscene), [Android](https://github.com/remixlab/proscene.droid) and (soon) JS.
* API design that provides seemless integration with **Processing** (e.g., providing flexible animation and drawing mechanisms), and allows extensibility of its key features.
* Generic support to [Human Interface Devices (HIDs)](http://en.wikipedia.org/wiki/Human_interface_device), including not only the mouse and the keyboard, but advanced HID's such as a [touchscreen](http://en.wikipedia.org/wiki/Touchscreen), a [space navigator](http://en.wikipedia.org/wiki/3Dconnexion) or a [kinect](http://en.wikipedia.org/wiki/Kinect).
* Keyboard shortcuts and HID bindings customization.
* Hierarchical coordinate systems (frames), with functions to convert between them.
* Interactive frames (including the camera) which may be manipulated by any HID.
* Arcball, walkthrough and third person camera modes.
* Default interactivity to your **Processing** scenes through the mouse (or touchscreen) and keyboard that simply does what you expect.
* Visibility culling: Back-face and view-frustum culling.
* Keyframes.
* Animation framework.
* Object picking.
* Screen drawing, i.e., drawing of 2d primitives on top of another (2d or 3d) scene.
* Off-screen rendering mode support.
* 2D and 3D Interactive [mini-maps](https://en.wikipedia.org/wiki/Mini-map).
* Handy set of complete documented [examples](https://github.com/remixlab/proscene/tree/master/examples) that illustrates the use of the package.
* A complete [API reference documentation](http://remixlab.github.io/proscene-javadocs/).
* Active support and continuous discussions led by the [Processing community](http://forum.processing.org/two/search?Search=proscene).
* Last but not least, released as free software under the terms of the [GPL-v3](http://www.gnu.org/licenses/gpl.html).

# Origin of the name

*ProScene* not only means a *"pro-scene"*, but it is a two-phoneme word pronounced similar as the Czech word *"prosím"* (which means *"please"*), obtained by removing the middle phoneme (*"ce"*) of the word *pro-ce-ssing*. Thus, the name *"ProScene"* suggests the main goal of the package, which is to help you _shorten_ the creation of interactive 2D/3D scenes in **Processing**.

# Usage

All library features requires a `Scene` object (which is the main package class) to be instantiated (usually within your sketch setup method). There are three ways to do that:

1. **Direct instantiation**. In this case you should instantiate your own Scene object at the `PApplet.setup()` function.
2. **Inheritance**. In this case, once you declare a `Scene` derived class, you should implement `proscenium()` which defines the objects in your scene. Just make sure to define the `PApplet.draw()` method, even if it's empty.
3. **External draw handler registration**. You can even declare an external drawing method and then register it at the Scene with `addDrawHandler(Object, String)`. That method should return `void` and have one single `Scene` parameter. This strategy may be useful when you have the same drawing code shared among multiple viewers.

See the examples **BasicUse**, **AlternativeUse**, and **StandardCamera** for an illustration of these techniques. To get start using the library and learn
its main features, have a look at the complete set of well documented examples that come along with it. Other uses are also covered in the example set and
include (but are not limited to): drawing mechanisms, animation framework, and camera and keyboard customization. Advanced users may take full advantage of
the fully documented [API reference](http://remixlab.github.io/proscene-javadocs/) (which is also
included in the package file).

# Installation

Import/update it directly from your PDE. Otherwise download your release from [here](https://github.com/remixlab/proscene/releases) and extract it to your sketchbook `libraries` folder.

# Acknowledgements

Thanks to [Eduardo Moriana](http://edumo.net/) and [Miguel Parra](http://maparrar.github.io/) for their contributions with the [TUIO](http://www.tuio.org/)-based touch and kinect interfaces, respectively.
Thanks to experimental computational designer [Amnon Owed](https://twitter.com/AmnonOwed/media) for his collaboration with polishing the KeyFrameInterpolator sub-system.
Thanks to [Jacques Maire](http://www.xelyx.fr) for providing most of the examples found at the *contrib* section. Thanks to [Andres Colubri](http://codeanticode.wordpress.com/) for his continuous support and thorough insights.
Thanks to [Victor Forero](https://sites.google.com/site/proscenedroi/home) who is developing the [proscene Android port](https://github.com/remixlab/proscene.droid).
Finally, thanks to all **ProScene** users whose sketchs and library hacks always amaze us and inspire us.

# Author, core developer and maintainer

[Jean Pierre Charalambos](http://disi.unal.edu.co/profesores/pierre/), [National University of Colombia](http://www.unal.edu.co)
