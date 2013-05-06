/*
 * =============================================================================
 *
 *   Copyright (c) 2013, The porra-joc-eda team (http://www.porra-joc-eda.tk)
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
package net.llop.porraeda.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Utility stuff for bets
 * @author Llop
 */
public class BetUtils {

	public static final String MASTA_HOUSE = "MASTER";
	public static final String DA_HOUSE = "daHouse";
	
	public static final String NO_PLAYER = "---";
	
	public static final Double INITIAL_CREDIT = 1000d;
	public static final Integer SUGGESTED_MIN_BET = 100;
	public static final Integer SUGGESTED_MIN_ENDURE_ROUNDS = 10;
	public static final Integer MAX_USER_BETS = 20;
	public static final Integer FINAL_PLAYERS = 16;
	
	private final static DecimalFormat FORMATTER = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));
	
	public final static String formatDouble(final Double value) {
		return FORMATTER.format(value);
	}
	
	public final static Integer toInteger(final Double value) {
		return (int)Math.floor(value);
	}

	public final static String sanitizePlayerName(final String player) {
		return player.trim().replace(".", " ");
	}
	
	private BetUtils() {}

}
