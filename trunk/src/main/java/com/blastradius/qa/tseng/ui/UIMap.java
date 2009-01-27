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
package com.blastradius.qa.tseng.ui;

import java.util.Hashtable;

/**
 * Manages the top-level UI Elements
 * 
 * @author thomas.johnson@blastradius.com
 */
public class UIMap {
	private static Hashtable<String, UITemplate> uiMap = new Hashtable<String, UITemplate>();
	
	/**
	 * Retrieves a top-level object from the UI map.
	 * @param name
	 * @return
	 */
	public static UIElement ui(String name) {
		return new UIElement(null, uiMap.get(name));
	}
	
	/**
	 * Adds a new object type to the UI Map, registering it with the given name.
	 * 
	 * @param name Name for the object
	 * @param template
	 */
	public static void registerTemplate(String name, UITemplate template) {
		uiMap.put(name, template);
	}
	
	/**
	 * Retrieves a raw UIElementTemplate from the UI map, typically for use
	 * in building other templates.
	 * 
	 * This method should not to be confused with the ui method, which
	 * returns a UIElement for more general use inside hierarchies.
	 * 
	 * @param name
	 * @return
	 */
	public static UITemplate getTemplate(String name) {
		return uiMap.get(name);
	}
	
	/**
	 * Removes a UIElementTemplate from the UI map.
	 * @param name
	 * @return
	 */
	public static UITemplate removeTemplate(String name) {
		return uiMap.remove(name);
	}
}
