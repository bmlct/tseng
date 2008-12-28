/* Copyright 2008 Blast Radius
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.blastradius.qa.tseng;

import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.testng.annotations.*;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

/**
 * Manages and provides access to variables used throughout
 * the test, such as the Selenium object and browser string.
 *  
 * @author thomas.johson@blastradius.com
 */
public class Globals {
	/** In-process Selenium Server (preferred), used unless external server is specified */
	public static SeleniumServer internalServer;
	
	/** Hostname of Selenium Server */
	public static String seleniumServerHost;
	
	/** Port on which Selenium Server is being run */
	public static int seleniumServerPort;
	
	/** Selenium Driver responsible for controlling the browser */
	public static Selenium s;

	/** The path to the Root of the Site being tested */
	public static String siteUnderTest = "http://localhost/";
	
	/** Browser string identifying test browser */
	public static String browserString;
	
	// --- Parameter Initialization ---
	@BeforeSuite
	@Parameters("selenium.server.hostname")
	@Test(groups="selenium.variables",
			description="Identify host that is running Selenium server (default=localhost)")
	public void initServerHost(@Optional(value="localhost") String serverHost) {
		seleniumServerHost = serverHost;
	}
	
	@BeforeSuite
	@Parameters("selenium.server.port")
	@Test(groups="selenium.variables",
			description="Identify port that Selenium server is listening on (default=4444)")
	public void initServerPort(@Optional(value="4444") String serverPort) {
		try {
			int input = Integer.parseInt(serverPort, 10);
			if(input <= 0 || input > Math.pow(2, 16)) {
				throw new RuntimeException("Server port must be a valid TCP port number");
			} else {
				seleniumServerPort = input;
			}
		} catch(NumberFormatException nfe) {
			throw new RuntimeException("Server port must be an integer greater than zero", nfe);
		}
	}
	
	@BeforeSuite
	@Parameters("selenium.site")
	@Test(groups="selenium.variables",
			description="Identify site under test (default=http://localhost)")
	public void initSiteUnderTest(@Optional(value="http://localhost") String site) {
		siteUnderTest = site;
	}
	
	@BeforeSuite
	@Parameters("selenium.browser")
	@Test(groups="selenium.variables",
			description="Identify browser to use (default=*chrome)")
	public void initBrowser(@Optional(value="*chrome") String browser) {
		browserString = browser;
	}
	
	// --- Client/Server setup --- //
	@AfterGroups("selenium.variables")
	@BeforeSuite
	@Parameters("selenium.server.hostname")
	@Test(groups="selenium.connection",
			description="Set up the local Selenium server if a remote is not specified")
	public void initServer(String host) {
		if(host == null || host.equals("") || host.equalsIgnoreCase("localhost")) {
			try {
				RemoteControlConfiguration config = new RemoteControlConfiguration();
				config.setPort(seleniumServerPort);

				internalServer = new SeleniumServer(config);
				
				// Wait for server startup
			} catch (Exception e) {
				throw new RuntimeException("Failed to start Selenium Server", e);
			}
		}
	}
	
	@AfterMethod(dependsOnMethods="initServer")
	@BeforeSuite
	@Test(groups="selenium.connection",
			description="Start a new Selenium client and open the site root")
	public void initClient() {
		s = new DefaultSelenium(seleniumServerHost, seleniumServerPort, browserString, siteUnderTest);
		s.open("");
	}
	
	// --- Teardown methods --- //
	@AfterSuite
	@Test(groups="selenium.connection",
			description="Disconnect the client")
	public void teardownClient() {
		if(s != null) {
			s.close();
			s.stop();
		}
	}
	
	@AfterSuite
	@AfterMethod(dependsOnMethods="teardownClient")
	@Test(groups="selenium.connection",
			description="Shut down server if necessary")
	public void teardownServer() {
		if(internalServer != null) {
			internalServer.stop();
			internalServer = null;
		}
	}
}
