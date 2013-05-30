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
package org.jutge.joc.porra.entitystash.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.jutge.joc.porra.entitystash.box.AlivePlayerBox;
import org.jutge.joc.porra.entitystash.box.MessageBox;
import org.jutge.joc.porra.entitystash.box.RestrictionBox;
import org.jutge.joc.porra.entitystash.stash.EntityStash;
import org.jutge.joc.porra.entitystash.utils.EntityStashUtils;
import org.jutge.joc.porra.model.account.Account;
import org.jutge.joc.porra.model.bet.Bet;
import org.jutge.joc.porra.model.bet.RoundsBet;
import org.jutge.joc.porra.model.bet.WinnerBet;
import org.jutge.joc.porra.model.league.League;
import org.jutge.joc.porra.model.player.Player;
import org.jutge.joc.porra.model.player.PopularPlayer;
import org.jutge.joc.porra.service.AccountService;
import org.jutge.joc.porra.service.LeagueService;
import org.jutge.joc.porra.service.PlayerService;
import org.jutge.joc.porra.utils.AccountUtils;
import org.jutge.joc.porra.utils.BetUtils;
import org.jutge.joc.porra.utils.LeagueUtils;
import org.jutge.joc.porra.utils.PlayerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;


/**
 * Intermediary view service class
 * @author Llop
 */
@Service
public class EntityStashViewService {

	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@Autowired private LeagueService leagueService;
	@Autowired private PlayerService playerService;
	@Autowired private AccountService accountService;
	
	@Autowired private MessageSource messageSource;
	
	
	//----------------------------------------
	// league info json string
	//----------------------------------------
	
	@SuppressWarnings("rawtypes")
	public String getLeagueInfo(final League league, final Locale locale) {
		this.logger.info("EntityStashViewService.getLeaguesInfo");
		final List leagueInfo = this.getLeagueInfoInternal(league, locale);
		return EntityStashUtils.toJsonString(leagueInfo);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getLeagueInfoInternal(final League league, final Locale locale) {
		final List leagueInfoList = new ArrayList();
		// last round
		final Integer round = league.getRound();
		String message = this.messageSource.getMessage("info.lastRound", null, locale);
		List<String> args = new ArrayList<>();
		args.add(round.toString());
		MessageBox messageBox = new MessageBox("round", message, args);
		leagueInfoList.add(messageBox);
		// survivors
		final Integer survivors = league.getAlivePlayers().intValue();
		message = this.messageSource.getMessage("info.survivors", null, locale);
		args = new ArrayList<>();
		args.add(survivors.toString());
		messageBox = new MessageBox("survivors", message, args);
		leagueInfoList.add(messageBox);
		// killed
		final Integer killed = league.getKilledPlayers().intValue();
		message = this.messageSource.getMessage("info.killed", null, locale);
		args = new ArrayList<>();
		args.add(killed.toString());
		messageBox = new MessageBox("killed", message, args);
		leagueInfoList.add(messageBox);
		return leagueInfoList;
	}
	
	
	//----------------------------------------
	// league stats json string
	//----------------------------------------
	
	@SuppressWarnings("rawtypes")
	public String getLeagueStats(final League league, final Locale locale) {
		this.logger.info("EntityStashViewService.getLeagueStats");
		final List leagueStats = this.getLeagueStatsInternal(league, locale);
		return EntityStashUtils.toJsonString(leagueStats);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getLeagueStatsInternal(final League league, final Locale locale) {
		final List leagueInfoList = new ArrayList();
		// total kudos won
		final Integer kudosWon = league.getKudosWon().intValue();
		String message = this.messageSource.getMessage("info.totalKudosWon", null, locale);
		List<String> args = new ArrayList<>();
		args.add(kudosWon.toString());
		MessageBox messageBox = new MessageBox("totalKudosWon", message, args);
		leagueInfoList.add(messageBox);
		// total kudos in-game
		final Integer kudosAtStake = league.getKudosAtStake().intValue();
		message = this.messageSource.getMessage("info.totalKudosAtStake", null, locale);
		args = new ArrayList<>();
		args.add(kudosAtStake.toString());
		messageBox = new MessageBox("totalKudosAtStake", message, args);
		leagueInfoList.add(messageBox);
		// average kudos per bet
		if (league.getActiveBets() > 0) {
			final Integer averageBetKudos = (int)(league.getKudosAtStake() / league.getActiveBets());
			message = this.messageSource.getMessage("info.averageBetKudos", null, locale);
			args = new ArrayList<>();
			args.add(averageBetKudos.toString());
			messageBox = new MessageBox("averageBetKudos", message, args);
			leagueInfoList.add(messageBox);
		}
		// user bet success rate
		if (league.getFinishedBets() > 0) {
			final Double wonBetsPercentage = 100d * league.getBetsWon() / league.getFinishedBets();
			message = this.messageSource.getMessage("info.wonBetsPercentage", null, locale);
			args = new ArrayList<>();
			args.add(BetUtils.formatDouble(wonBetsPercentage));
			messageBox = new MessageBox("wonBetsPercentage", message, args);
			leagueInfoList.add(messageBox);
		}
		return leagueInfoList;
	}

	
	//----------------------------------------
	// popular players json string
	//----------------------------------------
	
	public String getLeaguePopularPlayers(final League league, final Locale locale) {
		this.logger.info("EntityStashViewService.getLeaguePopularPlayers");
		final List<PopularPlayer> popularPlayers = league.getPopularPlayers();
		return EntityStashUtils.toJsonString(popularPlayers);
	}
	


	//----------------------------------------
	// richest accounts json string
	//----------------------------------------
	
	@SuppressWarnings("rawtypes")
	public String getRichestAccounts(final EntityStash entityStash, final Locale locale) {
		this.logger.info("EntityStashViewService.getRichestAccounts");
		final List richestAccounts = this.getRichestAccountsInternal(entityStash, locale);
		return EntityStashUtils.toJsonString(richestAccounts);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getRichestAccountsInternal(final EntityStash entityStash, final Locale locale) {
		final League league = entityStash.getLeague("FIB");
		if (league.getOver()) {
			final List<Pair<Double, Account>> richestAccounts = new ArrayList<>();
			final List<Account> accounts = this.accountService.getApprovedAccounts();
			for (final Account account : accounts) {
				richestAccounts.add(Pair.of(account.getKudos(), account));
			}
			Collections.sort(richestAccounts, AccountUtils.RICHEST_ACCOUNT_COMP);
			final List richestAccountsFinal = new ArrayList();
			int count = 0;
			final Iterator<Pair<Double, Account>> iter = richestAccounts.iterator();
			while (count < AccountUtils.MAX_RICHEST_PLAYERS && iter.hasNext()) {
				final Pair<Double, Account> pair = iter.next();
				final Account account = pair.getRight();
				richestAccountsFinal.add(Pair.of(account.getName(), BetUtils.formatDouble(account.getKudos())));
				++count;
			}
			return richestAccountsFinal;
		}
		return null;
	}

	
	//----------------------------
	// alive players json string
	//----------------------------
	
	@SuppressWarnings("rawtypes")
	public String getAlivePlayers(final List<Player> players) {
		this.logger.info("EntityStashViewService.getAlivePlayers");
		final List alivePlayers = this.getAlivePlayersInternal(players);
		return EntityStashUtils.toJsonString(alivePlayers);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getAlivePlayersInternal(final List<Player> players) {
		final List alivePlayersList = new ArrayList();
		for (final Player player : players) {
			if (PlayerUtils.isAlive(player) && !PlayerUtils.isWildcardPlayer(player)) {
				final String playerName = player.getName();
				final String playerReadableName = PlayerUtils.readableName(player);
				final String playerHandle = player.getHandle();
				final AlivePlayerBox alivePlayer = new AlivePlayerBox(playerName, playerReadableName, playerHandle);
				alivePlayersList.add(alivePlayer);
			}
		}
		Collections.sort(alivePlayersList, EntityStashUtils.ALIVE_PLAYERS_COMP);
		return alivePlayersList;
	}
	
	
	//----------------------------------------
	// league bet restrictions json string
	//----------------------------------------
	
	@SuppressWarnings("rawtypes")
	public String getLeagueBetRestrictions(final League league, final Locale locale) {
		this.logger.info("EntityStashViewService.getLeagueBetRestrictions");
		final List leagueBetRestrictions = this.getLeagueBetRestrictionsInternal(league, locale);
		return EntityStashUtils.toJsonString(leagueBetRestrictions);
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getLeagueBetRestrictionsInternal(final League league, final Locale locale) {
		final List leagueRestrictionsList = new ArrayList();
		// max rounds for bet
		final Integer maxRoundsLeft = LeagueUtils.getLastRound(league) - league.getRoundTemp();
		final String maxRoundsLeftStr = maxRoundsLeft.toString();
		final String errorMessage = this.messageSource.getMessage("exception.maxRoundsReached", null, locale);
		final List<String> errorArgs = new ArrayList<>();
		errorArgs.add(maxRoundsLeftStr);
		RestrictionBox restrictionBox = new RestrictionBox("maxRoundsLeft", maxRoundsLeftStr, errorMessage, errorArgs);
		leagueRestrictionsList.add(restrictionBox);
		// suggested endure rounds
		final Integer defaultRounds = maxRoundsLeft > BetUtils.SUGGESTED_MIN_ENDURE_ROUNDS ? BetUtils.SUGGESTED_MIN_ENDURE_ROUNDS : maxRoundsLeft;
		restrictionBox = new RestrictionBox("defaultRounds", defaultRounds.toString(), "", new ArrayList<String>());
		leagueRestrictionsList.add(restrictionBox);
		return leagueRestrictionsList;
	}
	
	
	//----------------------------------------
	// account bet restrictions json string
	//----------------------------------------
	
	@SuppressWarnings("rawtypes")
	public String getAccountBetRestrictions(final Account account, final Locale locale) {
		this.logger.info("EntityStashViewService.getAccountBetRestrictions");
		final List accountBetRestrictions = this.getAccountBetRestrictionsInternal(account, locale);
		return EntityStashUtils.toJsonString(accountBetRestrictions);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getAccountBetRestrictionsInternal(final Account account, final Locale locale) {
		final List accountDataList = new ArrayList();
		// max kudos for bet
		final Double accountKudos = account.getKudos();
		final Integer kudosLeftInt = BetUtils.toInteger(accountKudos);
		final String kudosLeftIntStr = kudosLeftInt.toString();
		String errorMessage = this.messageSource.getMessage("exception.notEnoughKudos", null, locale);
		List<String> errorArgs = new ArrayList<>();
		errorArgs.add(kudosLeftIntStr);
		RestrictionBox restrictionBox = new RestrictionBox("kudosLeft", kudosLeftIntStr, errorMessage, errorArgs);
		accountDataList.add(restrictionBox);
		// max bets left
		final Integer maxActiveBets = AccountUtils.MAX_ACTIVE_BETS;
		final String maxActiveBetsStr = maxActiveBets.toString();
		errorMessage = this.messageSource.getMessage("exception.maxActiveBetsReached", null, locale);
		errorArgs = new ArrayList<>();
		errorArgs.add(maxActiveBetsStr);
		restrictionBox = new RestrictionBox("maxActiveBets", maxActiveBetsStr, errorMessage, errorArgs);
		accountDataList.add(restrictionBox);
		// suggested bet kudos
		final Integer defaultKudos = kudosLeftInt > BetUtils.SUGGESTED_MIN_BET ? BetUtils.SUGGESTED_MIN_BET : kudosLeftInt;
		restrictionBox = new RestrictionBox("defaultKudos", defaultKudos.toString(), "", new ArrayList<String>());
		accountDataList.add(restrictionBox);
		return accountDataList;
	}
	
	
	//----------------------------------------
	// account info json string
	//----------------------------------------
	
	@SuppressWarnings("rawtypes")
	public String getAccountInfo(final Account account, final Locale locale) {
		this.logger.info("EntityStashViewService.getAccountInfo");
		final List accountInfo = this.getAccountInfoInternal(account, locale);
		return EntityStashUtils.toJsonString(accountInfo);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getAccountInfoInternal(final Account account, final Locale locale) {
		final List accountInfoList = new ArrayList();
		// welcome message
		final String accountName = account.getName();
		String message = this.messageSource.getMessage("view.welcome", null, locale);
		List<String> args = new ArrayList<>();
		args.add(accountName);
		MessageBox messageBox = new MessageBox("accountWelcome", message, args);
		accountInfoList.add(messageBox);
		// active bets and kudos
		final Integer accountActiveBets = account.getActiveBets();
		final Double accountKudos = account.getKudos();
		message = this.messageSource.getMessage("account.activeBetsAndKudos", null, locale);
		args = new ArrayList<>();
		args.add(accountActiveBets.toString());
		args.add(BetUtils.formatDouble(accountKudos));
		messageBox = new MessageBox("activeBetsAndKudos", message, args);
		accountInfoList.add(messageBox);
		return accountInfoList;
	}
	
	
	//----------------------------------------
	// account bets json string
	//----------------------------------------
	
	@SuppressWarnings("rawtypes")
	public String getAccountBets(final Account account, final EntityStash entityStash, final Locale locale) {
		this.logger.info("EntityStashViewService.getAccountBets");
		final List accountBets = this.getAccountBetsInternal(account, entityStash, locale);
		return EntityStashUtils.toJsonString(accountBets);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getAccountBetsInternal(final Account account, final EntityStash entityStash, final Locale locale) {
		final List accountBetsList = new ArrayList();
		final List<Bet> accountBets = account.getBets();
		for (final Bet bet : accountBets) {
			final Boolean isActiveBet = BetUtils.isActive(bet);
			final Integer betKudos = bet.getKudos().intValue();
			final String betPlayer = bet.getPlayer();
			final String readableBetPlayer = PlayerUtils.readableName(betPlayer);
			final String betLeague = bet.getLeague();
			final String status = bet.getStatus();
			final String type = bet.getType();
			final List messageBoxes = new ArrayList();
			// forecast message
			if (bet instanceof RoundsBet) {
				final RoundsBet roundsBet = (RoundsBet)bet;
				final Integer enduresRounds = roundsBet.getEnduresRounds();
				final Integer betOnRound = roundsBet.getBetOnRound();
				String message = this.messageSource.getMessage("bet.rounds.forecast", null, locale);
				List<String> args = new ArrayList<>();
				args.add(betKudos.toString());
				args.add(readableBetPlayer);
				args.add(enduresRounds.toString());
				args.add(betOnRound.toString());
				MessageBox messageBox = new MessageBox("betForecast", message, args);
				messageBoxes.add(messageBox);
				// balance value
				Double balance = bet.getBalance();
				if (isActiveBet) {
					final Player player = entityStash.getPlayer(betPlayer);
					final League league = entityStash.getLeague(betLeague);
					final Double betRate = BetUtils.calculateRate(bet, player, league);
					final Double betPrize = BetUtils.calculateBetPrize(bet, betRate);
					balance = BetUtils.calculateBetBenefits(bet, betPrize);
				}
				// balance message
				final String messageCode = isActiveBet ? "bet.pending.balance" : BetUtils.isWon(bet) ? "bet.win.balance" : "bet.lost.balance";
				message = this.messageSource.getMessage(messageCode, null, locale);
				args = new ArrayList<>();
				args.add(BetUtils.formatDouble(balance));
				messageBox = new MessageBox("betBalance", message, args);
				messageBoxes.add(messageBox);
			} else if (bet instanceof WinnerBet) {
				String message = this.messageSource.getMessage("bet.winner.forecast", null, locale);
				List<String> args = new ArrayList<>();
				args.add(readableBetPlayer);
				MessageBox messageBox = new MessageBox("betForecast", message, args);
				messageBoxes.add(messageBox);
				// balance value
				Double balance = bet.getBalance();
				if (isActiveBet) {
					balance = bet.getKudos();
				}
				// balance message
				final String messageCode = isActiveBet ? "bet.winner.pending.balance" : BetUtils.isWon(bet) ? "bet.win.balance" : "bet.lost.balance";
				message = this.messageSource.getMessage(messageCode, null, locale);
				args = new ArrayList<>();
				args.add(BetUtils.formatDouble(balance));
				messageBox = new MessageBox("betBalance", message, args);
				messageBoxes.add(messageBox);
			}
			// create and fill map
			final Map betDataMap = new HashMap();
			betDataMap.put("kudos", betKudos.toString());
			betDataMap.put("player", betPlayer);
			betDataMap.put("league", betLeague);
			betDataMap.put("status", status);
			betDataMap.put("type", type);
			betDataMap.put("messages", messageBoxes);
			accountBetsList.add(betDataMap);
		}
		return accountBetsList;
	}
	

}
