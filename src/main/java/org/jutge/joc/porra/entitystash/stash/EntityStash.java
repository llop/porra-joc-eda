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
package org.jutge.joc.porra.entitystash.stash;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jutge.joc.porra.model.account.Account;
import org.jutge.joc.porra.model.league.League;
import org.jutge.joc.porra.model.player.Player;

/**
 * This class is supposed to be a container for regularly accessed entities, so a minimum numer of queries to Mongo are performed throughout the request.
 * Do not subclass! why would you want to!?
 * @author Llop
 */
public class EntityStash {

	private Map<String, League> leaguesMap;
	private Map<String, Player> playersMap;
	private Map<String, List<Player>> playersByLeague;
	private Account account;

	
	public EntityStash() {
		super();
		this.leaguesMap = new HashMap<>();
		this.playersMap = new HashMap<>();
		this.playersByLeague = new HashMap<>();
	}
	
	public EntityStash(final List<League> leagues, final List<Player> players, final Account account) {
		this();
		this.init(leagues, players, account);
	}
	

	public void init(final List<League> leagues, final List<Player> players, final Account account) {
		// clear previous data
		this.leaguesMap.clear();
		this.playersMap.clear();
		this.playersByLeague.clear();
		// insert new values
		this.account = account;
		if (leagues != null) {
			for (final League league : leagues) {
				if (league != null && league.getName() != null) {
					final String leagueName = league.getName();
					this.leaguesMap.put(leagueName, league);
					this.playersByLeague.put(leagueName, new ArrayList<Player>());
				}
			}
		}
		if (players != null) {
			for (final Player player : players) {
				if (player != null && player.getName() != null) {
					this.playersMap.put(player.getName(), player);
					final List<Player> leaguePlayers = this.playersByLeague.get(player.getLeague());
					if (leaguePlayers != null) {
						leaguePlayers.add(player);
					}
				}
			}
		}
	}	
	
	
	public final Collection<League> getLeagues() {
		return this.leaguesMap.values();
	}
	
	public final League getLeague(final String leagueName) {
		return this.leaguesMap.get(leagueName);
	}
	
	public final List<Player> getLeaguePlayers(final League league) {
		return this.playersByLeague.get(league.getName());
	}
	
	public final Player getPlayer(final String playerName) {
		return this.playersMap.get(playerName);
	}
	
	public final Account getAccount() {
		return this.account;
	}
	
	
}
