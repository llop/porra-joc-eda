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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jutge.joc.porra.model.player.Player;
import org.jutge.joc.porra.model.player.PlayerStatus;


/**
 * Helper class for players
 * @author Llop
 */
public class PlayerUtils {
	

	private final static Pattern PATTERN = Pattern.compile("[a-z]+");

		
	
	//----------------------------
	// utility
	//----------------------------

	public static final boolean isAlive(final Player player) {
		return player != null && PlayerStatus.ALIVE.name().equals(player.getStatus());
	}
	
	public final static boolean isWildcardPlayer(final Player player) {
		if (player == null) return false;
		return PlayerUtils.isWildcardPlayer(player.getName());
	}
	
	public final static boolean isWildcardPlayer(final String playerName) {
		if (playerName == null) return false;
		return "jpetit".equalsIgnoreCase(playerName) || "jocs".equalsIgnoreCase(playerName);
	}
	
	public static final Map<String, Player> toMap(final List<Player> players) {
		final Map<String, Player> playersMap = new HashMap<>();
		for (Player player : players) {
			if (player != null) {
				final String playerName = player.getName();
				if (playerName != null) {
					playersMap.put(playerName, player);
				}
			}
		}
		return playersMap;
	}
	
	public final static String readableName(final Player player) {
		if (player == null) return null;
		final String playerName = player.getName();
		return PlayerUtils.readableName(playerName);
	}
	
	public final static String readableName(final String playerName) {
		if (playerName == null) return null;
		final String cleanPlayerName = playerName.trim().replace(".", " ");
		final Matcher matcher = PATTERN.matcher(cleanPlayerName);
		final StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			final String replacement = StringUtils.capitalize(matcher.group());
			matcher.appendReplacement(buffer, replacement);
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}
	
	
	private PlayerUtils() {}
	
	
}
