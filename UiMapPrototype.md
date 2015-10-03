# Test Design with UI Map (prototype) #
The 0.2 release will feature UI Map functionality, allowing for separation of structure and code. The following prototype is largely based upon the example [here](TsengUsage#Test_Design.md), but features more realistic XPath Locators that would genuinely clutter the code.

```
package com.example.tests

import static com.blastradius.qa.tseng.Globals.s; // The Selenium object, already initialized
import com.blastradius.qa.tseng.ui.UIMap;
import com.blastradius.qa.tseng.ui.UIMap.ui; // Reuse the "ui" method
import static org.testng.Assert.*; // Convenience methods

public class MyTestUiMap {
   @Test(description="Open the homepage and verify its title")
   public void simpleHomepageCheck() {
      s.open("/");
      assertEquals(s.getTitle(), "Welcome to My Homepage", "Homepage title is incorrect");
   }

   @Test(description="Log In with new user", dependsOnMethods="simpleHomePageCheck")
   @Parameters("username", "password")
   public void basicLoginTest(@Optional(value="john.doe") String user, @Optional(value="12345") String pass) {
      s.open("/");

      s.type(ui("login box").ui("username"), user); // Static import of UImap.ui()

      UIElement l = UIMap.ui("login box"); // Store the object, just for brevity

      s.type(l.ui("password"), pass);
      s.click(l.ui("login button"));
      assertEquals(s.getText(l.ui("success text")),
            "Welcome, " + user,
            "User login failed, or welcome message not displayed");
   }
}
```

ui-map.xml (Long locators are wrapped for layout reasons):
```
<ui-map>
  <ui name="login box" locator="//div[@name='loginpopup']">
    <description>The login popup box and its associated children</description>
    <elements>
      <link
        name="login trigger"
        locator="//div[@name='loginpopup']/a[text()='Login']" />
      <textfield
        name="username"
        locator="//div[@name='loginpopup']//form[name='user.login.credentials']
                 //input[@name='user.login.credentials.name']" />
      <textfield
        name="password"
        locator="//div[@name='loginpopup']//form[name='user.login.credentials']
                 //input[@name='user.login.credentials.password']" />
      <button name="login button">
        <description>A simple login button, demonstrating a less compact,
                     but more flexible form of UIElement use. This allows
                     the user to use both single (') and double (") quotes
                     for exceptionally complex XPath expressions</description>
        <locator>//div[@name="loginpopup"]//form[name="user.login.credentials"]
                 //input[@type="submit"][@value='login']"</locator>
    </elements>
  </ui>
</ui-map>
```

# UI Map Hierarchies #
A candidate feature for a 0.3 release is a UI Map that supports hierarchical specification of locators. This functionality is difficult to implement for the general case of varied types of locators because most of Selenium's locator strategies do not provide functionality for searching within an element.

As a stopgap, it may be possible implement an XPath-specific strategy, as there appears to be some direct support inside the included JavaScript XPath libraries. In the example below, locators are built with their parents.

```
<ui-map>
  <ui name="login box" locator="//div[@name='loginpopup']">
    <description>The login popup box and its associated children</description>
    <elements>
      <link name="login trigger" locator="a[text()='Login']" />
      <form name="credentials" locator="//form[@name='user.login.credentials']">
        <elements>
          <textfield name="username" locator="//input[@name='user.login.credentials.name']" />
          <textfield name="password" locator="//input[@name='user.login.credentials.password']" />
          <button name="login button">
            <description>A simple login button, demonstrating a less compact,
                         but more flexible form of UIElement use. This allows
                         the user to use both single (') and double (") quotes
                         for exceptionally complex XPath expressions</description>
            <locator>//input[@type="submit"][@value='login']"</locator>
          </button>
        </elements>
      </form>
    </elements>
  </ui>
</ui-map>
```


# UI Map Actions #
The 0.3 release should allow users to perform named actions from within the XML. The primary purpose is to allow end-users to fill fields and click links without having to deal with Java. Test planners may also create functional stubs early in the design phase.

```
<ui-map>
  <ui name="login box" locator="...">
    <elements>
      <link name="loginBoxTrigger" locator="..." />
      <textfield name="user" locator="..." />
      <textfield name="password" locator="..." />
      <button name="go" locator="..." />
    </elements>
    <actions>
      <action name="login">
        <description>Fills in the Username and Password fields, then hits submit</description>
        <parameters>
          <parameter name="username" default="j.doe" />
          <parameter name="password" default="12345" />
        </parameters>
        <procedure>
          <do target="loginBoxTrigger" action="click" />
          <set target="login" parameter="username" />
          <set target="password" parameter="password" />
          <do target="go" action="click" />
        </procedure>
      </action>
    </actions>
  </ui>
</ui-map>
```

```
@Test(description="SimpleLogin", dataProvider="logins")
public void login(String user, String pass) {
   Hashtable<String, String> params = new Hashtable<String, String>();
   params.put("username", user);
   params.put("password", pass);
   params.put("address", "1 Infinite Loop");

   ui("login").call("login", p);

   // Transparently creates the above, but doesn't allow for default properties
   ui("login").call("login", "username", user, "password", pass);

   // Check for success
   assertEquals(ui("login").get(), "Login Successful");
}
```

It is worth noting that the language presented here is extremely verbose, and borders on being ugly and unreadable. It almost wants to become a friendlier language (Groovy?) or a Java APT plugin. It is expected that this part of the UI Map functionality should be used sparingly, for cases in which it is extremely desirable for keeping logic out of the tests and supporting classes.

# Javascript Version #
Given its highly flexible syntax and dynamic nature, JavaScript may be the language of choice for a futurey UI Map implementation. It allows for extremely succinct definitions of UI elements with minimal overhead required to implement inheritance.

```
var login = new UI({
   locator: "...",
   username: new UI(textfield, {locator: "..."}),
   password: new UI(textfield, {locator: "..."}),
   loginButton: new UI(button, {locator: "..."}),
   login: function(user, pass) {
      username.type(user);
      password.type(user);
      loginButton.click();
   }
});
```

It may be possible to further simplify this design by allowing the UI constructor to handle the details of creating new objects and reusing functionality:
```
var login = new UI({
   locator: "...",
   username: { type: textfield, locator: "..." },
   password: { type: textfield, locator: "..." },
   submit: { type: button, locator: "..." },
   login: function(user, pass) {
      username.type(user);
      password.type(pass);
      submit.click();
   }
});
```