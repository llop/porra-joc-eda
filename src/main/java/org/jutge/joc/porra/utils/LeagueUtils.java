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
package org.jutge.joc.porra.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.jutge.joc.porra.model.league.League;
import org.jutge.joc.porra.model.player.Player;
import org.jutge.joc.porra.model.player.PopularPlayer;


/**
 * Helper class for leagues
 * @author Llop
 */
public class LeagueUtils {
	
	
	public final static Integer MAX_POPULAR_PLAYERS = 10;
	
	public static final Comparator<Pair<Integer, Player>> POP_PLAYERS_COMP = new Comparator<Pair<Integer, Player>>() {
		@Override public int compare(final Pair<Integer, Player> a, final Pair<Integer, Player> b) {
			final int aValue = a.getLeft();
			final int bValue = b.getLeft();
	        return aValue > bValue ? -1 : (aValue == bValue ? 0 : 1);
	    }
	};

	
	//----------------------------
	// utility
	//----------------------------
	
	public static final int getLastRound(final League league) {
		return league.getRound() + league.getAlivePlayers() - league.getFinalPlayers();
	}
	
	public static final List<PopularPlayer> getPopularPlayers(final Collection<Player> players, final League league) {
		final List<Pair<Integer, Player>> votersForPlayers = new ArrayList<>();
		for (final Player player : players) {
			if (PlayerUtils.isAlive(player) && !PlayerUtils.isWildcardPlayer(player)) {
				final int betMakers = player.getBetMakers(); 
				if (betMakers > 0) {
					votersForPlayers.add(Pair.of(betMakers, player));
				}
			}
		}
		Collections.sort(votersForPlayers, POP_PLAYERS_COMP);
		int counter = 0;
		// final Integer leagueBetMakers = league.getBetMakers();
		final List<PopularPlayer> popularPlayers = new ArrayList<>();
		final Iterator<Pair<Integer, Player>> iter = votersForPlayers.iterator();
		while (iter.hasNext() && counter < LeagueUtils.MAX_POPULAR_PLAYERS) {
			final Pair<Integer, Player> votersForPlayer = iter.next();
			final Player player = votersForPlayer.getRight();
			final Integer playerBetMakers = votersForPlayer.getLeft();
			// final Double popularity = 100d * playerBetMakers / leagueBetMakers;
			final PopularPlayer popularPlayer = new PopularPlayer(PlayerUtils.readableName(player), player.getHandle(), playerBetMakers.toString());  // BetUtils.formatDouble(popularity));
			popularPlayers.add(popularPlayer);
			++counter;
		}
		return popularPlayers;
	}
	
	
	private LeagueUtils() {}
	
	
}
