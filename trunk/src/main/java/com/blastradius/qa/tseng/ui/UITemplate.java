/* Copyright 2008
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
 * Describes a UI object in terms of its locator and a set of
 * named children. The UIObject only describes itself and the UI Elements
 * beneath it, which allows for easy component reuse.
 * 
 * Association with parent objects (along with more complex decoration
 * functionality) is provided by the UIObjectReference class. 
 * 
 * @author thomas.johnson@blastradius.com
 */
public class UITemplate {
	private final String locator;
	
	private final Hashtable<String, UITemplate> children = new Hashtable<String, UITemplate>();
	
	/**
	 * Creates a UI object represented by a Selenium locator, relative to
	 * an arbitrary parent object
	 * 
	 * @param locator Selenium Locator that identifies this object relative to a parent
	 */
	public UITemplate(String locator) {
		this.locator = locator;
	}
	
	/**
	 * Clone constructor that creates a new object with the same children (shallow clone)
	 * and a different locator.
	 * @param original
	 * @param locator
	 */
	public UITemplate(UITemplate original, String locator) {
		this.locator = locator;
		children.putAll(original.children);
	}
	
	/**
	 * Returns the named child object
	 * @param name
	 * @return
	 */
	public UITemplate getChild(String name) {
		return children.get(name);
	}
	
	/**
	 * Adds a child UIObject corresponding to the given name.
	 * @param name
	 * @param child
	 */
	public void addChild(String name, UITemplate child) {
		children.put(name, child);
	}
	
	public void removeChild(String name) {
		children.remove(name);
	}
	
	public String getLocator() {
		return locator;
	}
}
