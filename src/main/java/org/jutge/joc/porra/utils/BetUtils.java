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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bson.types.ObjectId;
import org.jutge.joc.porra.model.account.Account;
import org.jutge.joc.porra.model.bet.Bet;
import org.jutge.joc.porra.model.bet.BetStatus;
import org.jutge.joc.porra.model.bet.RoundsBet;
import org.jutge.joc.porra.model.league.League;
import org.jutge.joc.porra.model.player.Player;


/**
 * Helper class for bets
 * @author Llop
 */
public class BetUtils {


	public static final int SUGGESTED_MIN_ENDURE_ROUNDS = 10;
	public static final int SUGGESTED_MIN_BET = 100;
	
	public final static Integer MAX_WINNERS = 4;
	
	
	
	//----------------------------
	// format
	//----------------------------
	
	private final static DecimalFormat FORMATTER = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));
	
	public final static String formatDouble(final Double value) {
		return FORMATTER.format(value);
	}
	
	public final static Integer toInteger(final Double value) {
		return (int)Math.floor(value);
	}
	
	
	//----------------------------
	// utility
	//----------------------------
	
	public static final boolean isActive(final Bet bet) {
		return bet != null && BetStatus.PENDING.name().equals(bet.getStatus());
	}
	
	public static final boolean isWon(final Bet bet) {
		return bet != null && BetStatus.WIN.name().equals(bet.getStatus());
	}
	
	public static final boolean isLost(final Bet bet) {
		return bet != null && BetStatus.LOSS.name().equals(bet.getStatus());
	}
	
	public static final void wireBetIntoList(final Bet bet, final List<Bet> bets) {
		if (bet == null) return;
		final ObjectId id = bet.getId();
		if (id == null) return;
		final int betsSize = bets.size();
		for (int i = 0; i < betsSize; ++i) {
			final Bet betTemp = bets.get(i);
			if (betTemp != null && id.equals(betTemp.getId())) {
				bets.set(i, bet);
				return;
			}
		}
	}
	
	public static final List<Bet> getActiveBets(List<Bet> bets) {
		List<Bet> activeBets = new ArrayList<>();
		for (Bet bet : bets) {
			if (BetUtils.isActive(bet)) {
				activeBets.add(bet);
			}
		}
		return activeBets;
	}
	
	
	//----------------------------
	// Calculations
	//----------------------------
	
	public static final double calculateBetPrize(final Bet bet, final double betRate) {
		return bet.getKudos() * betRate;
	}
	
	public static final double calculateBetBenefits(final Bet bet, final double betPrize) {
		return betPrize - bet.getKudos();
	}
	
	public static final double calculateRate(final Bet bet, final Player player, final League league) {
		final int playerBetMakers = player.getBetMakers();
		final int leagueBetMakers = league.getBetMakers();
		final double factor = (double)playerBetMakers / leagueBetMakers;
		return BetUtils.calculateRate(bet, playerBetMakers, leagueBetMakers, factor);
	}
	
	public static final double calculateFutureBetRate(final Account account, final Bet bet, final Player player, final League league) {
		int playerBetMakers = player.getBetMakers();
		if (!AccountUtils.hasBetsOnPlayer(account, player)) {
			++playerBetMakers;
		}
		int leagueBetMakers = league.getBetMakers();
		if (!AccountUtils.hasBetsOnLeague(account, league)) {
			++leagueBetMakers;
		}
		final double factor = (double)playerBetMakers / leagueBetMakers;
		return BetUtils.calculateRate(bet, playerBetMakers, leagueBetMakers, factor);
	}
	
	private static final double calculateRate(final Bet bet, final int playerBetMakers, final int leagueBetMakers, final double factor) {
		double odds = 1d;
		if (bet instanceof RoundsBet) {
			final RoundsBet roundsBet = (RoundsBet)bet;
			Integer playersAlive = roundsBet.getPlayersAlive();
			final Integer limit = playersAlive - roundsBet.getEnduresRounds();
			while (playersAlive > limit) {
				odds *= 1d - (1d / playersAlive);
				--playersAlive;
			}
		}
		odds += (1d - odds) * factor;
		return 1d / odds;
	}
	
	
	
	private BetUtils() {}
	
	
}
