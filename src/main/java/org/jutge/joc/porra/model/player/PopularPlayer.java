/*
 * =============================================================================
 *
 *   Copyright (c) 2013, The porra-joc-jutge team (http://porra.jutge.org)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */
package org.jutge.joc.porra.model.player;


/**
 * Popular player POJO
 * @author Llop
 */
public class PopularPlayer {
	
	private String name;
	private String handle;
	private String popularity;
	
	
	public PopularPlayer() {
		super();
	}
	
	public PopularPlayer(final String name, final String handle, final String popularity) {
		super();
		this.name = name;
		this.handle = handle;
		this.popularity = popularity;
	}
	
	
	public String getName() {
		return name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}
	
	public String getHandle() {
		return handle;
	}
	
	public void setHandle(final String handle) {
		this.handle = handle;
	}
	
	public String getPopularity() {
		return popularity;
	}
	
	public void setPopularity(final String popularity) {
		this.popularity = popularity;
	}
	
}
