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
 * Manages the top-level UI Elements
 */
public class UIMap {
	private static Hashtable<String, UIObject> uimap = new Hashtable<String, UIObject>();
	
	public static UIObject ui(String... path) {
		UIObject out = get(path[0]);
		for(int i = 1; i < path.length; i++) {
			out = out.ui(path[i]);
		}
		return out;
	}

	public static UIObject get(String name) {
		return uimap.get(name);
	}

	public static UIObject put(String name, UIObject value) {
		return uimap.put(name, value);
	}
}
