# Destination #

TSeNG aims to provide a stable base for building large-scale automated regression tests with Selenium and TestNG. It is modeled after "[Ringo](http://www.youtube.com/watch?v=hWQdCdH77NA)" as described at GTAC 2007. This must be well-documented, tested, and easy to deploy.

# Schedule #

## tseng-0.1: Core ##
Released on 2009-01-02

This version focuses on providing a reusable Selenium/TestNG base. It is the boilerplate code involved in nearly every project that involves TestNG and Selenium. Even on its own, this functionality is a big win, as it means jumping straight to test writing and gives testers a common framework to work from.

### Deliverables ###
  * (done) Maven Project
  * (done) Example minimalist TestNG Suite providing variables to Selenium Globals class
  * (done) JAR packaging for TSeNG
  * (done) Unit tests of core functionality (Selenium init, variable reading, usage of global Selenium object)
  * (done) Proof of concept for future Unit Tests (Add context to local Jetty server)

## tseng-0.2: UI Map ##
Estimated Release: 2009-01-17

The UI Map provides an abstraction for objects and hierarchies on the page, pulling page-dependent locators out of code and into XML resources. This lowers the barriers of entry to test maintenance and modification by allowing tests to become decoupled from page design.

### Deliverables ###
  * UI Object Superclass
  * Generic Read/Write interface
  * UI Object Repository
  * Programatically defined UI Objects
  * UI Objects defined in XML UI Map
  * (?) UI Object XPath inheritance (concatenation, from root)

## tseng- 0.3: Structured Data Input ##
This release focuses on simplifying the process of providing data for data driven tests. Data should be input from CSV files and Hierarchical XML files, and facilities should be provided for selecting subsets of the input data.

Functionality should also be provided for generating Properties objects with Key/Value pairs for each piece of data in the row. XML files could build the row with a Depth-first search, with the option of overriding higher-level elements.

Ringo also provides facilities for automatically filling in forms (or other UI elements) with selected items from the data map. This functionality may be dropped or moved to a later release due to the complexity associated with tying the UI Map to the Data Map. This implementation may be simplified by modifying TestNG's DataProvider implementation to support parameterization, allowing for test methods to specify the data file that they should be pulling from.

### Deliverables ###
  * CSV data provider
  * Structured data provider
  * Hashmap data provider
  * Augment UI map to support referencing Data Map
  * Automatic form filler (uses UI to Data map)
  * (?) Extend TestNG data provider functionality

## tseng- 0.4: UI Object functions ##
Complex UI Objects should provide a means for describing their functionality. For example, a complex Form could provide a Submit method that takes care of populating data and clicking the "Confirm" link along the way.

This functionality must be usable from the UI Map, as the Java side already has access to any convenience methods that are designed.

## tseng- 0.5: Decorators and Decorator Chaining ##
Augmentation of the UI Map to allow augmentation of a UI Object's behavior. This is primarily useful for implementing load conditions and wait states. The common use case is for a field that is verified when the field loses focus -- The blur event can be called after contents have been typed in.

## tseng- 0.6: Load Conditions ##
For Ajax-heavy sites, it is necessary to determine if particular page components are loaded. This release augments the UI Map with checks for state.

## tseng- 0.7: Paging ##
Add support for uniformly accessing data that may be spread over multiple pages. This may be more appropriate as a post-1.0 feature.

## Reporting ##
Design of reporting functionality for better integration with Maven's Surefire reports. This may be more appropriate as an enhancement for Surefire, or for inclusion in a post-1.0 release.

## tseng- 1.0: Launchworthy ##
The 1.0 release constitutes full Ringo-like functionality, or at least a useful subset. It may be beneficial to declare 1.0 after UI Map and Read/Write abstraction, as this is the most useful feature set.