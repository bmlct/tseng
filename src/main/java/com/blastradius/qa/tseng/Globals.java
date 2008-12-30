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

import static org.testng.Assert.assertTrue;

import org.mortbay.jetty.Server;
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
	
	/** Selenium Driver responsible for controlling the browser */
	public static Selenium s;
	
	/** Hostname of Selenium Server (selenium.server.hostname) */
	private static final String DEFAULT_SELENIUM_SERVER_HOST = "localhost";
	public static String seleniumServerHost = DEFAULT_SELENIUM_SERVER_HOST;
	
	/** Port on which Selenium Server is being run (selenium.server.port) */
	private static final int DEFAULT_SELENIUM_SERVER_PORT = 4444;
	private static final String DEFAULT_SELENIUM_SERVER_PORT_STRING = "4444";
	public static int seleniumServerPort = DEFAULT_SELENIUM_SERVER_PORT;

	/** The path to the Root of the Site being tested (selenium.site) */
	private static final String DEFAULT_SITE_UNDER_TEST = "http://localhost";
	public static String siteUnderTest = DEFAULT_SITE_UNDER_TEST;
	
	/** Browser string identifying test browser (selenium.browser) */
	private static final String DEFAULT_BROWSER_STRING="*chrome";
	public static String browserString = DEFAULT_BROWSER_STRING;
	
	// --- Parameter Initialization --- //
	@Parameters("selenium.server.hostname")
	@BeforeSuite(groups="selenium.variables",
			description="Identify host that is running Selenium server")
	public void initServerHost(@Optional(value=DEFAULT_SELENIUM_SERVER_HOST) String serverHost) {
		seleniumServerHost = serverHost;
	}
	
	@Parameters("selenium.server.port")
	@BeforeSuite(groups="selenium.variables",
			description="Identify port that Selenium server is listening on")
	public void initServerPort(@Optional(value=DEFAULT_SELENIUM_SERVER_PORT_STRING) String serverPort) {
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
	
	@Parameters("selenium.site")
	@BeforeSuite(groups="selenium.variables",
			description="Identify site under test")
	public void initSiteUnderTest(@Optional(value=DEFAULT_SITE_UNDER_TEST) String site) {
		siteUnderTest = site;
	}
	
	@Parameters("selenium.browser")
	@BeforeSuite(groups="selenium.variables",
			description="Identify browser to use")
	public void initBrowser(@Optional(value=DEFAULT_BROWSER_STRING) String browser) {
		browserString = browser;
	}
	
	// --- Selenium Server and Client setup --- //
	@BeforeSuite(groups="selenium.connection",
			dependsOnGroups="selenium.variables",
			description="Set up the local Selenium server if a remote is not specified")
	public void initServer() {
		if(seleniumServerHost == null || seleniumServerHost.equals("") || seleniumServerHost.equalsIgnoreCase(DEFAULT_SELENIUM_SERVER_HOST)) {
			try {
				RemoteControlConfiguration config = new RemoteControlConfiguration();
				config.setPort(seleniumServerPort);

				internalServer = new SeleniumServer(config);
				internalServer.start();
				
				final long start = System.currentTimeMillis();
				final long timeout = 60000; // 30 seconds
				final long deadline = start + timeout;
				
				Server jetty = internalServer.getServer();
				while(System.currentTimeMillis() < deadline && !jetty.isStarted()) {
					try {
						Thread.sleep(timeout);
					} catch(InterruptedException e) { /* Swallowed */ }
				}
				assertTrue(jetty.isStarted(), "Private instance of Selenium Server must start within " + timeout + "ms");
			} catch (Exception e) {
				throw new RuntimeException("Failed to start Selenium Server", e);
			}
		}
	}
	
	@BeforeSuite(groups="selenium.connection",
			dependsOnMethods="initServer",
			description="Start a new Selenium client and open the site root")
	public void initClient() {
		s = new DefaultSelenium(seleniumServerHost, seleniumServerPort, browserString, siteUnderTest);
		s.open("");
	}
	
	// --- Teardown methods --- //
	@AfterSuite(groups="selenium.connection",
			description="Disconnect the Selenium driver")
	public void teardownClient() {
		if(s != null) {
			s.close();
			s.stop();
		}
	}
	
	@AfterSuite(groups="selenium.connection",
			dependsOnMethods="teardownClient",
			description="Shut down local Selenium server if necessary")
	public void teardownServer() {
		if(internalServer != null) {
			internalServer.stop();
			internalServer = null;
		}
	}
}
