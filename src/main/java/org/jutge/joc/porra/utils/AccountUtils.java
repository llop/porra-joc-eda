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

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.jutge.joc.porra.model.account.Account;
import org.jutge.joc.porra.model.account.AccountStatus;
import org.jutge.joc.porra.model.bet.Bet;
import org.jutge.joc.porra.model.bet.WinnerBet;
import org.jutge.joc.porra.model.league.League;
import org.jutge.joc.porra.model.player.Player;
import org.springframework.security.crypto.bcrypt.BCrypt;


/**
 * Helper class for accounts
 * @author Llop
 */
public class AccountUtils {
	
	
	
	public static final Comparator<Pair<Double, Account>> RICHEST_ACCOUNT_COMP = new Comparator<Pair<Double, Account>>() {
		@Override public int compare(final Pair<Double, Account> a, final Pair<Double, Account> b) {
			final Double aValue = a.getLeft();
			final Double bValue = b.getLeft();
	        return aValue > bValue ? -1 : (aValue == bValue ? 0 : 1);
	    }
	};
	
	
	public final static Double INITIAL_KUDOS = 1000d;
	public final static Integer MAX_ACTIVE_BETS = 20;
	public final static Integer MAX_RICHEST_PLAYERS = 20;


	//----------------------------
	// account token maker
	//----------------------------
	
	public static final String generateActivationToken() {
		return BCrypt.gensalt(16).replace("/", "7").replace(".", "d");
	}
	
	
	//----------------------------
	// account status utility
	//----------------------------
	
	public static final boolean isApproved(final Account account) {
		return account != null && AccountStatus.STATUS_APPROVED.name().equals(account.getStatus());
	}
	public static final boolean isDisabled(final Account account) {
		return account != null && AccountStatus.STATUS_DISABLED.name().equals(account.getStatus());
	}
	public static final boolean isPending(final Account account) {
		return account != null && AccountStatus.STATUS_PENDING.name().equals(account.getStatus());
	}
	public static final boolean isPendingNopassword(final Account account) {
		return account != null && AccountStatus.STATUS_PENDING_NOPASSWORD.name().equals(account.getStatus());
	}

	
	//----------------------------
	// account expiry
	//----------------------------
	
	public static final boolean shouldExpireAccount(final Account account) {
		return AccountUtils.isPendingNopassword(account) && AccountUtils.hasExpired(account);
	}
	
	private static final boolean hasExpired(final Account account) {
		final Date accountCreatedOn = account.getCreatedOn();
		final Date expiresOn = new Date(accountCreatedOn.getTime() + 21600000);  // 6-7 hours i think
		return new Date().after(expiresOn);
	}

	
	//----------------------------
	// account bets
	//----------------------------
	
	public static final Map<String, WinnerBet> getWinnerBets(final Account account) {
		final Map<String, WinnerBet> winnerBets = new HashMap<>();
		final List<Bet> bets = account.getBets();
		for (final Bet bet : bets) {
			if (BetUtils.isActive(bet) && bet instanceof WinnerBet) {
				winnerBets.put(bet.getPlayer(), (WinnerBet)bet);
			}
		}
		return winnerBets;
	}
	
	public static final boolean hasBetsOnPlayer(final Account account, final Player player) {
		if (player == null) return false;
		final String playerName = player.getName();
		if (playerName == null) return false;
		List<Bet> bets = account.getBets();
		if (bets == null) return false;
		for (Bet bet : bets) {
			if (BetUtils.isActive(bet) && playerName.equals(bet.getPlayer())) {
				return true;
			}
		}
		return false;
	}
	
	public static final boolean hasBetsOnLeague(final Account account, final League league) {
		if (league == null) return false;
		final String leagueName = league.getName();
		if (leagueName == null) return false;
		List<Bet> bets = account.getBets();
		if (bets == null) return false;
		for (Bet bet : bets) {
			
			if (BetUtils.isActive(bet) && leagueName.equals(bet.getLeague())) {
				return true;
			}
		}
		return false;
	}
	
	
	private AccountUtils() {}
	
	
}
