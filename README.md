### autograder-gwt

Copyright 2017-2018 Clarkson University.

Autograder is a customizable online homework utility.
This program is licensed under GNU General Purpose License version 3. See the file "COPYING" or visit <https://www.gnu.org/licenses> for
more information about GPLv3.

Authors: 
Christopher Murphy  murphycd@clarkson.edu
Dillon Clapp        clappdj@clarkson.edu
Ryan Wood           woodrj@clarkson.edu

Source code is available at <https://gitlab.com/ClarksonUniversity/autograder/autograder-gwt>.

Please contact Christopher Murphy (murphycd@clarkson.edu) or Ryan Wood (woodrj@clarkson.edu) with any questions.


### Setting up the Development Environment



We recommend following the [Getting Started with GWT Eclipse Tutorial](http://www.gwtproject.org/usingeclipse.html).

1. Install [_Eclipse IDE for Java Developers Neon 3_](http://www.eclipse.org/downloads/packages/release/Neon/3).

2. Then follow instructions to download and install the [GWT Eclipse Plugin](http://gwt-plugins.github.io/documentation/gwt-eclipse-plugin/Download.html).
  * _Include_ "JavaScript Debugger with Source Mapping Support" (important)
  * _Exclude_ GWT 2.X.X SDK, we will source the SDK elsewhere as it gets updated frequently.

3. In Eclipse, Help > Eclipse Marketplace, install Apache IvyDE

4. Clone this Git repository in Eclipse.

5. Download [GWT SDK 2.8.2](http://www.gwtproject.org/download.html) and point the GWT Plugin to it.
