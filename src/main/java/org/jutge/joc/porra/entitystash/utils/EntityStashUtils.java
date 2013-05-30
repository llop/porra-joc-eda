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
package org.jutge.joc.porra.entitystash.utils;

import java.util.Comparator;

import org.codehaus.jackson.map.ObjectMapper;
import org.jutge.joc.porra.entitystash.box.AlivePlayerBox;


/**
 * Helper stuff for entity stash infrastructure
 * @author Llop
 *
 */
public class EntityStashUtils {
	
	
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	
	public static final String ENTITY_STASH = "entityStash";

	public static final String LEAGUE_INFO = "leagueInfo";
	public static final String LEAGUE_STATS = "leagueStats";
	public static final String LEAGUE_POPULAR_PLAYERS = "leaguePopularPlayers";
	public static final String LEAGUE_ALIVE_PLAYERS = "leaguePlayers";
	

	public static final Comparator<AlivePlayerBox> ALIVE_PLAYERS_COMP = new Comparator<AlivePlayerBox>() {
		@Override public int compare(final AlivePlayerBox a, final AlivePlayerBox b) {
			return a.getPlayerName().compareTo(b.getPlayerName());
	    }
	};
	
	
	public static final String toJsonString(Object object) {
		try {
			return OBJECT_MAPPER.writeValueAsString(object);
		} catch (Exception exception) {}
		return null;
	}
	
	
	private EntityStashUtils() {}
	
	
}
