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
package org.jutge.joc.porra.entitystash.box;


/**
 * Alive player POJO
 * @author Llop
 */
public class AlivePlayerBox {
	
	
	private String playerName;
	private String readableName;
	private String playerHandle;
	
	
	public AlivePlayerBox(final String playerName, final String readableName, final String playerHandle) {
		super();
		this.playerName = playerName;
		this.readableName = readableName;
		this.playerHandle = playerHandle;
	}
	

	public String getPlayerName() {
		return playerName;
	}

	public String getReadableName() {
		return readableName;
	}

	public String getPlayerHandle() {
		return playerHandle;
	}
	
	
}
