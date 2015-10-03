# Goals #
TSeNG provides an abstraction layer for the elements and actions of a site under test, allowing for decoupling test design and site structure.

# Features #
The 0.1 feature set (available now) includes a TestNG test class that handles pulling values from a Test Suite file, using these values to set up a Selenium instance, and providing access to the freshly created Selenium instance. This is all Maven-ready, allowing testers to start writing their tests within minutes of a "mvn install" command.

The 0.2 feature set (currently in trunk) adds functionality for defining a UI Map that increases code readability, enables test definition before implementation, and simplifies maintainability for rapidly-changing sites. This core functionality has been implemented and is now available in the SVN trunk. Release is slated for Feb-March 2009, pending integration of the UI Map loader into the global setup.

The 0.3 feature set (still in planning, release date unknown) will provide functionality for managing data input and feeding it to UI elements.

# Origins #
TSeNG is based off of Google's Ringo tool, as described in [Building a Framework around Selenium](http://www.youtube.com/watch?v=hWQdCdH77NA) at GTAC 2007. TSeNG's ultimate goal is to replicate (or improve upon) Ringo's Abstractions, Conditions, IO interface, and Data Templates.