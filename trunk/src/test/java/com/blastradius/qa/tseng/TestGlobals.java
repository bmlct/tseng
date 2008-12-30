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

import java.net.UnknownHostException;

import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;
import org.mortbay.util.InetAddrPort;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class TestGlobals {
	public static Server server;
	private static final String JETTY_BIND_ADDRESS = "localhost";
	private static final int JETTY_PORT = 4443;
	
	@BeforeSuite(description="Initialize a Jetty instance for tests to run against")
	public void startJetty() throws UnknownHostException {
		if(server != null) {
			server = new Server();
			
			SocketListener listener = new SocketListener(new InetAddrPort(JETTY_BIND_ADDRESS, JETTY_PORT));
			server.addListener(listener);
			
			try {
				server.start();
			} catch(Exception e) {
				throw new RuntimeException("Jetty Server failed to start", e);
			}
			
			final long start = System.currentTimeMillis();
			final long timeout = 30000;
			final long deadline = start + timeout;
			
			while(System.currentTimeMillis() < deadline && !server.isStarted()) {
				try {
					Thread.sleep(timeout);
				} catch(InterruptedException e) { /* Swallowed */ }
			}
			
			assertTrue(server.isStarted(), "Jetty test server must start within " + timeout + "ms");
		}
	}
	
	@AfterSuite(description="Shut down Jetty server if it is running")
	public void stopJetty() {
		if(server != null && server.isStarted()) {
			try {
				server.stop(false);
			} catch (InterruptedException e) {
				// Swallow exception - docs recommend discarding the server if this happens
			} finally {
				server = null;
			}
		}
	}
}
