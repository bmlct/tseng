This document assumes basic familiarity with both Selenium and TestNG.

# Overview #
  1. Download the Jar file
  1. Add the Jar to your test's classpath
  1. Create a TestNG test suite that runs com.blastradius.qa.tseng.Globals
  1. Set up Test Site variables
  1. Start writing tests
  1. Run the tests

# Test Suite Design #
Test Suites are written in the normal TestNG style, with the additional requirement that TSeNG's Globals class is included in the test. Additionally, TSeNG requires a few variables to define the site under test.

```
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">

<suite>
  <parameter name="selenium.server.site" value="http://www.google.com" />
  <parameter name="selenium.browser" value="*chrome" />

  <test name="00 TSeNG Initialization">
    <classes><class name="com.blastradius.qa.tseng.Globals" /></classes>
  </test>

  <test name="Your test classes here">
     <classes><!-- Your test classes and packages here --></classes>
  </test>
</suite>
```

# Test Design #
Tests are most cleanly expressed with Java 5 Annotations and Static Imports. A normal test class will statically import the Selenium object (Just "s" to ease test writing) from the TSeNG Globals class, and the JUnit-style assertions from TestNG.

```
package com.example.tests

import static com.blastradius.qa.tseng.Globals.s; // The Selenium object, already initialized
import static org.testng.Assert.*; // Convenience methods

public class MyTest1 {
   @Test(description="Open the homepage and verify its title")
   public void simpleHomepageCheck() {
      s.open("/");
      assertEquals(s.getTitle(), "Welcome to My Homepage", "Homepage title is incorrect");
   }

   @Test(description="Log In with new user", dependsOnMethods="simpleHomePageCheck")
   @Parameters("username", "password")
   public void basicLoginTest(@Optional(value="john.doe") String user, @Optional(value="12345") String pass) {
      s.open("/");
      s.type("name=loginField", user);
      s.type("name=passwordField", pass);
      s.click("name=loginButton");
      assertEquals(s.getText("//form[@id='loginForm']/span[@class='loginstatus']"),
            "Welcome, " + user,
            "User login failed, or welcome message not displayed");
   }
}
```