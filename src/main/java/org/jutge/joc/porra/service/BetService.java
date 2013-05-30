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
package org.jutge.joc.porra.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jutge.joc.porra.entitystash.aspect.EntityStashAspect;
import org.jutge.joc.porra.entitystash.box.MessageBox;
import org.jutge.joc.porra.entitystash.stash.EntityStash;
import org.jutge.joc.porra.exception.BetException;
import org.jutge.joc.porra.exception.RoundsBetException;
import org.jutge.joc.porra.exception.WinnerBetException;
import org.jutge.joc.porra.model.account.Account;
import org.jutge.joc.porra.model.account.AccountRole;
import org.jutge.joc.porra.model.account.AccountStatus;
import org.jutge.joc.porra.model.bet.Bet;
import org.jutge.joc.porra.model.bet.BetStatus;
import org.jutge.joc.porra.model.bet.BetType;
import org.jutge.joc.porra.model.bet.RoundsBet;
import org.jutge.joc.porra.model.bet.WinnerBet;
import org.jutge.joc.porra.model.league.League;
import org.jutge.joc.porra.model.player.Player;
import org.jutge.joc.porra.model.player.PlayerStatus;
import org.jutge.joc.porra.model.player.PopularPlayer;
import org.jutge.joc.porra.repository.AccountRepository;
import org.jutge.joc.porra.repository.BetRepository;
import org.jutge.joc.porra.repository.LeagueRepository;
import org.jutge.joc.porra.repository.PlayerRepository;
import org.jutge.joc.porra.utils.AccountUtils;
import org.jutge.joc.porra.utils.BetUtils;
import org.jutge.joc.porra.utils.LeagueUtils;
import org.jutge.joc.porra.utils.PlayerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;


/**
 * Handles bet-related logic
 * @author Llop
 */
@Service
public class BetService {
	
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	
	@Autowired private BetRepository betRepo;
	@Autowired private LeagueRepository leagueRepo;
	@Autowired private PlayerRepository playerRepo;
	@Autowired private AccountRepository accountRepo;

	@Autowired private MessageSource messageSource;
	
	
	public void newWinnerBet(final WinnerBet winnerBet, final EntityStash entityStash) throws WinnerBetException {
		this.logger.info("BetService.newWinnerBet");
		this.validateBet(winnerBet, entityStash, false);
		final Account account = entityStash.getAccount();
		final Player player = entityStash.getPlayer(winnerBet.getPlayer());
		final League league = entityStash.getLeague(winnerBet.getLeague());
		account.getBets().add(0, winnerBet);
		final Map<String, WinnerBet> winnerBets = AccountUtils.getWinnerBets(account);
		player.setBetMakers(player.getBetMakers() + 1);
		player.setActiveBets(player.getActiveBets() + 1);
		league.setActiveBets(league.getActiveBets() + 1);
		account.setActiveBets(account.getActiveBets() + 1);
		if (winnerBets.size() == 1) {
			league.setBetMakers(league.getBetMakers() + 1);
			league.setKudosAtStake(league.getKudosAtStake() + account.getKudos());
		}
		// update popular players just in case
		final Collection<Player> leaguePlayers = entityStash.getLeaguePlayers(league);
		final List<PopularPlayer> popularPlayers = LeagueUtils.getPopularPlayers(leaguePlayers, league);
		league.setPopularPlayers(popularPlayers);
		// save involved entities
		try {
			final Collection<WinnerBet> bets = winnerBets.values();
			this.betRepo.save(bets);
			this.leagueRepo.save(league);
			this.playerRepo.save(player);
			this.accountRepo.save(account);
		} catch (Exception exception) {
			this.logger.error(exception.getMessage());
		}
	}
	
	public void deleteWinnerBet(final String playerName, final EntityStash entityStash) throws WinnerBetException {
		this.logger.info("BetService.deleteWinnerBet");
		final Account account = entityStash.getAccount();
		final Map<String, WinnerBet> winnerBets = AccountUtils.getWinnerBets(account);
		if (!winnerBets.containsKey(playerName)) {
			final Locale locale = LocaleContextHolder.getLocale();
			final String message = messageSource.getMessage("exception.noWinnerBetOnPlayer", null, locale);
			final MessageBox messageBox = new MessageBox("noWinnerBetOnPlayer", message, new ArrayList<String>());
			final List<MessageBox> errorMessages = new ArrayList<>();
			errorMessages.add(messageBox);
			throw new WinnerBetException(errorMessages);
		}
		final Player player = entityStash.getPlayer(playerName);
		final League league = entityStash.getLeague(player.getLeague());
		final Boolean playerLocked = player.getLocked();
		if (playerLocked != null && playerLocked) {
			final Locale locale = LocaleContextHolder.getLocale();
			final String message = messageSource.getMessage("exception.playerLocked", null, locale);
			final MessageBox messageBox = new MessageBox("playerLocked", message, new ArrayList<String>());
			final List<MessageBox> errorMessages = new ArrayList<>();
			errorMessages.add(messageBox);
			throw new WinnerBetException(errorMessages);
		}
		final WinnerBet betToRemove = winnerBets.get(playerName);
		final List<Bet> accountBets = account.getBets();
		accountBets.remove(betToRemove);
		winnerBets.remove(playerName);
		player.setBetMakers(player.getBetMakers() - 1);
		player.setActiveBets(player.getActiveBets() - 1);
		league.setActiveBets(league.getActiveBets() - 1);
		account.setActiveBets(account.getActiveBets() - 1);
		if (winnerBets.isEmpty()) {
			league.setBetMakers(league.getBetMakers() - 1);
			league.setKudosAtStake(league.getKudosAtStake() - account.getKudos());
		} else {
			final int numWinners = winnerBets.size();
			final Double kudos = account.getKudos();
			final Double winnerKudos = kudos / numWinners;
			final Collection<WinnerBet> bets = winnerBets.values();
			for (final WinnerBet bet : bets) {
				bet.setKudos(winnerKudos);
			}
		}
		// update popular players just in case
		final Collection<Player> leaguePlayers = entityStash.getLeaguePlayers(league);
		final List<PopularPlayer> popularPlayers = LeagueUtils.getPopularPlayers(leaguePlayers, league);
		league.setPopularPlayers(popularPlayers);
		// save involved entities
		try {
			this.betRepo.delete(betToRemove);
			if (!winnerBets.isEmpty()) {
				final Collection<WinnerBet> bets = winnerBets.values();
				this.betRepo.save(bets);
			}
			this.leagueRepo.save(league);
			this.playerRepo.save(player);
			this.accountRepo.save(account);
		} catch (Exception exception) {
			this.logger.error(exception.getMessage());
		}
	}
	
	public void consumeFinalRound(final List<String> playerNames, final EntityStash entityStash, final String roundType) {
		final League league = entityStash.getLeague(EntityStashAspect.DEFAULT_LEAGUE_NAME);
		// count users betting on this final round (4-player match, ie. semis or final)
		int totalBetMakers = 0;
		final List<Player> players = new ArrayList<>();
		for (final String playerName : playerNames) {
			final Player player = entityStash.getPlayer(playerName);
			totalBetMakers += player.getBetMakers();
			players.add(player);
		}
		final Map<String, Account> accountsToUpdate = new HashMap<>();
		// handle winner case first
		final Player winner = players.get(0);
		final String winnerName = playerNames.get(0);
		final List<Bet> playerBets = this.betRepo.findByPlayerAndTypeAndStatus(winnerName, BetType.WINNER.name(), BetStatus.PENDING.name());
		for (final Bet bet : playerBets) {
			final String accountName = bet.getAccount();
			Account account = accountsToUpdate.get(accountName);
			if (account == null) {
				account = this.accountRepo.findOne(accountName);
				accountsToUpdate.put(accountName, account);
			}
			BetUtils.wireBetIntoList(bet, account.getBets());
			// win
			final double factor = 1d - (winner.getBetMakers() / (double)totalBetMakers);
			final double betKudos = bet.getKudos();
			final double prize = betKudos + (betKudos * factor);
			account.setKudos(account.getKudos() + prize);
			league.setKudosAtStake(league.getKudosAtStake() + prize);
			// if final, finish bet off
			if ("FINAL".equals(roundType)) {
				account.setActiveBets(account.getActiveBets() - 1);
				league.setKudosAtStake(league.getKudosAtStake() - account.getKudos());
				league.setKudosWon(league.getKudosWon() + prize);
				league.setActiveBets(league.getActiveBets() - 1);
				winner.setActiveBets(winner.getActiveBets() - 1);
				winner.setBetMakers(winner.getBetMakers() - 1);
				bet.setStatus(BetStatus.WIN.name());
				bet.setBalance(prize);
				this.betRepo.save(bet);
			}
			league.setBetsWon(league.getBetsWon() + 1);
			league.setFinishedBets(league.getFinishedBets() + 1);
		}
		this.playerRepo.save(winner);
		// handle losers
		for (int i = 1; i < 4; ++i) {
			final Player loser = players.get(i);
			final String loserName = playerNames.get(i);
			final List<Bet> loserBets = this.betRepo.findByPlayerAndTypeAndStatus(loserName, BetType.WINNER.name(), BetStatus.PENDING.name());
			for (final Bet bet : loserBets) {
				final String accountName = bet.getAccount();
				Account account = accountsToUpdate.get(accountName);
				if (account == null) {
					account = this.accountRepo.findOne(accountName);
					accountsToUpdate.put(accountName, account);
				}
				BetUtils.wireBetIntoList(bet, account.getBets());
				// lose
				final double betKudos = bet.getKudos();
				account.setKudos(account.getKudos() - betKudos);
				account.setActiveBets(account.getActiveBets() - 1);
				league.setKudosAtStake(league.getKudosAtStake() - betKudos);
				league.setKudosLost(league.getKudosWon() - betKudos);
				loser.setActiveBets(loser.getActiveBets() - 1);
				loser.setBetMakers(loser.getBetMakers() - 1);
				bet.setStatus(BetStatus.LOSS.name());
				bet.setBalance(betKudos);
				this.betRepo.save(bet);
				league.setBetsLost(league.getBetsLost() + 1);
				league.setFinishedBets(league.getFinishedBets() + 1);
			}
			loser.setStatus(PlayerStatus.KILLED.name());
			if ("FINAL".equals(roundType)) {
				loser.setRoundKilled(93);
			} else {
				loser.setRoundKilled(92);
			}
			league.setAlivePlayers(league.getAlivePlayers() - 1);
			league.setKilledPlayers(league.getKilledPlayers() + 1);
			this.playerRepo.save(loser);
		}
		// updated accounts have remaining winner bets be recalculated 
		final Collection<Account> accounts = accountsToUpdate.values();
		for (final Account account : accounts) {
			final Map<String, WinnerBet> winnerBets = AccountUtils.getWinnerBets(account);
			if (winnerBets.isEmpty()) {
				league.setBetMakers(league.getBetMakers() - 1);
			} else {
				final Double kudos = account.getKudos();
				final Integer numWinners = winnerBets.size();
				final Double winnerKudos = kudos / numWinners;
				final Collection<WinnerBet> bets = winnerBets.values();
				for (final WinnerBet bet : bets) {
					bet.setKudos(winnerKudos);
					this.betRepo.save(bet);
				}
			}
			this.accountRepo.save(account);
		}
		if ("FINAL".equals(roundType)) {
			league.setRoundTemp(93);
		} else {
			league.setRoundTemp(92);
		}
		// update popular players just in case
		final Collection<Player> leaguePlayers = entityStash.getLeaguePlayers(league);
		final List<PopularPlayer> popularPlayers = LeagueUtils.getPopularPlayers(leaguePlayers, league);
		league.setPopularPlayers(popularPlayers);
		this.leagueRepo.save(league);
	}
	
	private void newRoundsBet(final RoundsBet bet, final EntityStash entityStash) throws RoundsBetException {
		this.validateBet(bet, entityStash, true);
		final Account account = entityStash.getAccount();
		final Player player = entityStash.getPlayer(bet.getPlayer());
		final League league = entityStash.getLeague(bet.getLeague());
		// update bet makers counters
		if (!AccountUtils.hasBetsOnPlayer(account, player)) {
			player.setBetMakers(player.getBetMakers() + 1);
		}
		if (!AccountUtils.hasBetsOnLeague(account, league)) {
			league.setBetMakers(league.getBetMakers() + 1);
		}
		// update active bets counters
		player.setActiveBets(player.getActiveBets() + 1);
		league.setActiveBets(league.getActiveBets() + 1);
		account.setActiveBets(account.getActiveBets() + 1);
		// update kudos stuff
		league.setKudosAtStake(league.getKudosAtStake() + bet.getKudos());
		account.setKudos(account.getKudos() - bet.getKudos());
		// insert newly created bet into account and player
		account.getBets().add(0, bet);
		// update popular players just in case
		final Collection<Player> leaguePlayers = entityStash.getLeaguePlayers(league);
		final List<PopularPlayer> popularPlayers = LeagueUtils.getPopularPlayers(leaguePlayers, league);
		league.setPopularPlayers(popularPlayers);
		// save involved entities
		try {
			this.betRepo.insert(bet);
			this.leagueRepo.save(league);
			this.playerRepo.save(player);
			this.accountRepo.save(account);
		} catch (Exception exception) {
			this.logger.error(exception.getMessage());
		}
	}
	
	/**
	 * Create a new rounds bet. Assume account, league, player, kudos, and endures rounds attributes have been set.
	 * @param account account that makes the bet
	 * @param roundsBet the bet itself
	 * @param entityBox entity box
	 */
	public void createBet(final Bet bet, final EntityStash entityStash) throws BetException {
		this.logger.info("BetService.createBet");
		if (bet instanceof WinnerBet) {
			this.newWinnerBet((WinnerBet)bet, entityStash);
		} else if (bet instanceof RoundsBet) {
			this.newRoundsBet((RoundsBet)bet, entityStash);
		}
	}
	
	public double getBetRate(final Bet bet, final EntityStash entityStash) throws BetException {
		this.logger.info("BetService.getRoundsBetRate");
		this.validateBet(bet, entityStash, false);
		final Account account = entityStash.getAccount();
		final Player player = entityStash.getPlayer(bet.getPlayer());
		final League league = entityStash.getLeague(bet.getLeague());
		return BetUtils.calculateFutureBetRate(account, bet, player, league);
	}
	
	private void validateBet(final Bet bet, final EntityStash entityStash, boolean applyRestrictions) throws BetException {
		if (bet instanceof RoundsBet) {
			this.validateRoundsBet((RoundsBet)bet, entityStash, applyRestrictions);
		} else if (bet instanceof WinnerBet) {
			this.validateWinnerBet((WinnerBet)bet, entityStash);
		}
	}

	@SuppressWarnings("rawtypes")
	private void validateWinnerBet(final WinnerBet winnerBet, final EntityStash entityStash) throws WinnerBetException {
		final List errorMessages = this.validateWinnerBetParamsAndCompleteEntity(winnerBet, entityStash);
		if (!errorMessages.isEmpty()) {
			throw new WinnerBetException(errorMessages);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private List validateWinnerBetParamsAndCompleteEntity(final WinnerBet winnerBet, final EntityStash entityStash) throws WinnerBetException {
		final Locale locale = LocaleContextHolder.getLocale();
		winnerBet.setStatus(BetStatus.PENDING.name());
		winnerBet.setBalance(0d);
		final Account account = entityStash.getAccount();
		final Map<String, WinnerBet> winnerBets = AccountUtils.getWinnerBets(account);
		final String playerName = winnerBet.getPlayer();
		final Player player = entityStash.getPlayer(playerName);
		final List<MessageBox> errorMessages = new ArrayList<>();
		final Boolean playerLocked = player.getLocked();
		if (playerLocked != null && playerLocked) {
			final String message = messageSource.getMessage("exception.playerLocked", null, locale);
			final MessageBox messageBox = new MessageBox("playerLocked", message, new ArrayList<String>());
			errorMessages.add(messageBox);
		}
		if (winnerBets.containsKey(playerName)) {
			final String message = messageSource.getMessage("exception.duplicateWinner", null, locale);
			final MessageBox messageBox = new MessageBox("duplicateWinner", message, new ArrayList<String>());
			errorMessages.add(messageBox);
		}
		final Integer numWinners = winnerBets.size() + 1;
		if (numWinners > BetUtils.MAX_WINNERS) {
			final String message = messageSource.getMessage("exception.maxWinnersReached", null, locale);
			final MessageBox messageBox = new MessageBox("maxWinnersReached", message, new ArrayList<String>());
			errorMessages.add(messageBox);
		}
		winnerBets.put(playerName, winnerBet);
		final Double kudos = account.getKudos();
		final Double winnerKudos = kudos / numWinners;
		final Collection<WinnerBet> bets = winnerBets.values();
		for (final WinnerBet bet : bets) {
			bet.setKudos(winnerKudos);
		}
		return errorMessages;
	}
	/**
	 * Assume account, league, player, kudos, and endures rounds attributes have been set.
	 * @param account
	 * @param roundsBet
	 * @param entityStash
	 * @param locale
	 */
	@SuppressWarnings("rawtypes")
	private void validateRoundsBet(final RoundsBet roundsBet, final EntityStash entityStash, boolean applyRestrictions) throws RoundsBetException {
		final Account account = entityStash.getAccount();
		final Player player = entityStash.getPlayer(roundsBet.getPlayer());
		final League league = entityStash.getLeague(roundsBet.getLeague());
		// validate and fill entity gaps along the way
		final List errorMessages = this.validateRoundsBetParamsAndCompleteEntity(account, roundsBet, league, player, applyRestrictions);
		if (!errorMessages.isEmpty()) {
			throw new RoundsBetException(errorMessages);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List validateRoundsBetParamsAndCompleteEntity(final Account account, final RoundsBet roundsBet, final League league, final Player player, boolean applyRestrictions) {
		final Locale locale = LocaleContextHolder.getLocale();
		roundsBet.setStatus(BetStatus.PENDING.name());
		roundsBet.setBalance(0d);
		List errorMessages = new ArrayList();
		if (league == null) {
			final String message = this.messageSource.getMessage("exception.invalidLeague", null, locale);
			final MessageBox messageBox = new MessageBox("invalidLeague", message, new ArrayList<String>());
			errorMessages.add(messageBox);
		}
		roundsBet.setBetOnRound(league.getRoundTemp());
		roundsBet.setPlayersAlive(league.getAlivePlayers() - (league.getRoundTemp() - league.getRound()));
		// player has to: be alive, not be a wildcard, and belong to the league
		if (!PlayerUtils.isAlive(player) || PlayerUtils.isWildcardPlayer(player) || !league.getName().equals(player.getLeague())) {
			final String message = this.messageSource.getMessage("exception.invalidPlayer", null, locale);
			final MessageBox messageBox = new MessageBox("invalidPlayer", message, new ArrayList<String>());
			errorMessages.add(messageBox);
		}
		final double betKudos = roundsBet.getKudos();
		if (betKudos < 1) {
			final String message = this.messageSource.getMessage("exception.invalidKudos", null, locale);
			final MessageBox messageBox = new MessageBox("invalidKudos", message, new ArrayList<String>());
			errorMessages.add(messageBox);
		}
		final Integer accountKudos = BetUtils.toInteger(account.getKudos());
		if (betKudos > accountKudos) {
			final String message = this.messageSource.getMessage("exception.notEnoughKudos", null, locale);
			final List<String> args = new ArrayList<>();
			args.add(accountKudos.toString());
			final MessageBox messageBox = new MessageBox("notEnoughKudos", message, args);
			errorMessages.add(messageBox);
		}
		if (applyRestrictions && account.getActiveBets() >= AccountUtils.MAX_ACTIVE_BETS) {
			final String message = this.messageSource.getMessage("exception.maxActiveBetsReached", null, locale);
			final List<String> args = new ArrayList<>();
			args.add(AccountUtils.MAX_ACTIVE_BETS.toString());
			final MessageBox messageBox = new MessageBox("maxActiveBetsReached", message, args);
			errorMessages.add(messageBox);
		}
		final int finalRound = roundsBet.getBetOnRound() + roundsBet.getEnduresRounds();
		if (finalRound <= league.getRoundTemp()) {
			final String message = this.messageSource.getMessage("exception.invalidRounds", null, locale);
			final MessageBox messageBox = new MessageBox("invalidRounds", message, new ArrayList<String>());
			errorMessages.add(messageBox);
		}
		if (finalRound > LeagueUtils.getLastRound(league)) {
			final Integer maxRoundsLeft = LeagueUtils.getLastRound(league) - league.getRoundTemp();
			final String message = this.messageSource.getMessage("exception.maxRoundsReached", null, locale);
			final List<String> args = new ArrayList<>();
			args.add(maxRoundsLeft.toString());
			final MessageBox messageBox = new MessageBox("maxRoundsReached", message, args);
			errorMessages.add(messageBox);
		}
		return errorMessages;
	}
	
	public boolean initDB(final String jsonString) {
		/**
		RoundsBet rbet = new RoundsBet();
		rbet.setBalance(0d);
		rbet.setBetOnRound(69);
		rbet.setEnduresRounds(10);
		rbet.setKudos(100d);
		rbet.setLeague("FIB");
		rbet.setPlayer("laura.chacon");
		rbet.setPlayersAlive(34);
		rbet.setStatus(BetStatus.PENDING.name());
		this.betRepo.insert(rbet);
		*/
		
		// reset player bets
		final List<Account> accounts = this.accountRepo.findAll();
		for (final Account account : accounts) {
			account.setBets(new ArrayList<Bet>());
			account.setActiveBets(0);
			account.setKudos(1000d);
			this.accountRepo.save(account);
		}
		
		try {
			// Lliga fib
			final String lligaFib = "FIB";
			final League league = new League();
			league.setName(lligaFib);
			league.setActiveBets(0);
			league.setBetMakers(0);
			league.setBetsLost(0);
			league.setBetsWon(0);
			league.setFinalPlayers(16);
			league.setFinishedBets(0);
			league.setKudosAtStake(0d);
			league.setKudosLost(0d);
			league.setKudosWon(0d);
			league.setPopularPlayers(new ArrayList<PopularPlayer>());
			
			int alivePlayers = 0;
			int killedPlayers = 0;
			List<Player> players = new ArrayList<>();
			// Parse json string
			final ObjectMapper mapper = new ObjectMapper();
			final JsonNode jsonData = mapper.readTree(jsonString);
			final Integer round = jsonData.get("ronda_actual").asInt();
			final JsonNode jsonPlayers = jsonData.get("usuaris");
			for (int i = 0; i < jsonPlayers.size(); i++) {
				final JsonNode jsonPlayer = jsonPlayers.get(i);
				final String jsonPlayerName = jsonPlayer.get("nom").asText();
				if (PlayerUtils.isWildcardPlayer(jsonPlayerName)) {
					continue;
				}
				final int jsonPlayerPlays = jsonPlayer.get("juga").asInt();
				final String jsonPlayerHandle = jsonPlayer.get("representant").asText();
				// new player
				final Player player = new Player();
				player.setName(jsonPlayerName);
				player.setHandle(jsonPlayerHandle);
				player.setLeague(lligaFib);
				player.setActiveBets(0);
				player.setBetMakers(0);
				if (jsonPlayerPlays == 0) {
					player.setStatus(PlayerStatus.KILLED.name());
					final int jsonPlayerDead = jsonPlayer.get("mort").asInt();
					player.setRoundKilled(jsonPlayerDead);
					++killedPlayers;
				} else {
					player.setStatus(PlayerStatus.ALIVE.name());
					player.setRoundKilled(0);
					++alivePlayers;
				}
				players.add(player);
			}
			
			// desar lliga
			league.setRound(round);
			league.setRoundTemp(round);
			league.setAlivePlayers(alivePlayers);
			league.setKilledPlayers(killedPlayers);
			this.leagueRepo.insert(league);
			// desar jugadors
			this.playerRepo.insert(players);
		} catch (JsonProcessingException exception) {
			this.logger.error(exception.getMessage());
			return false;
		} catch (IOException exception) {
			this.logger.error(exception.getMessage());
			return false;
		}
		
		return true;
	}

	/**
	 * Update bet related stuff
	 * @param jsonString - data from jutge.org
	 */
	public boolean updateEnduranceBets(final String jsonString, final EntityStash entityStash) {
		this.logger.info("BetService.updateEnduranceBets");
		try {
			// Parse json string
			final ObjectMapper mapper = new ObjectMapper();
			final JsonNode jsonData = mapper.readTree(jsonString);
			// 1 league (default to FIB)
			final League league = entityStash.getLeague(EntityStashAspect.DEFAULT_LEAGUE_NAME);
			// round advances before players start fighting, so in order to update bets properly we must wait for some to die too
			final Integer round = jsonData.get("ronda_actual").asInt();
			final boolean roundAdvanced = round > league.getRound();
			// Update round in roundTemp immediately to minimize race-condition dishonest bets
			// The idea is to disallow (or rather, to minimize) bets for a round while it is taking place
			if (roundAdvanced) {
				league.setRoundTemp(round);
				this.leagueRepo.save(league);
			}
			// players list
			boolean someoneDied = false;
			boolean handleUpdated = false;
			final JsonNode jsonPlayers = jsonData.get("usuaris");
			for (int i = 0; i < jsonPlayers.size(); i++) {
				// json fields
				final JsonNode jsonPlayer = jsonPlayers.get(i);
				final String jsonPlayerName = jsonPlayer.get("nom").asText();
				if (PlayerUtils.isWildcardPlayer(jsonPlayerName)) {
					continue;
				}
				final int jsonPlayerPlays = jsonPlayer.get("juga").asInt();
				final String jsonPlayerHandle = jsonPlayer.get("representant").asText();
				final Player player = entityStash.getPlayer(jsonPlayerName);
				if (player == null) {
					continue;
				}
				// signals a kill
				if (PlayerUtils.isAlive(player) && jsonPlayerPlays == 0) {
					someoneDied = true;
				}
				// player handles get updated immediately (most times contestants change handle and no rounds are taking place)
				if (!"null".equals(jsonPlayerHandle) && !player.getHandle().equals(jsonPlayerHandle)) {
					handleUpdated = true;
					player.setHandle(jsonPlayerHandle);
					this.playerRepo.save(player);
				}
			}
			// A killed player signals the end of a newer round
			final boolean newRoundComplete = roundAdvanced && someoneDied;
			if (newRoundComplete) {
				// although round advances before games start, and rounds take a few minutes to complete,
				// it is theoretically possible to have more than 1 round within a minute (expected time between updates)
				// need to handle one round at a time so as not to break playerBetMakers / leagueBetMakers ratio, which is used to calculate bet rate for rounds bets
				for (int roundTemp = league.getRound() + 1; roundTemp <= round; ++roundTemp) {
					// a temp counter for league bet makers -league's value can't be changed yet as prize calculation relies on it being constant for each particular round
					int leagueBetMakers = league.getBetMakers();
					for (int i = 0; i < jsonPlayers.size(); i++) {
						// json fields
						final JsonNode jsonPlayer = jsonPlayers.get(i);
						final String jsonPlayerName = jsonPlayer.get("nom").asText();
						if (PlayerUtils.isWildcardPlayer(jsonPlayerName)) {
							continue;
						}
						// get corresponding Player object and check for differences
						final Player player = entityStash.getPlayer(jsonPlayerName);
						if (player == null) {
							continue;
						}
						final int jsonPlayerPlays = jsonPlayer.get("juga").asInt();
						final int jsonPlayerDead = jsonPlayer.get("mort").asInt();
						final boolean killed = PlayerUtils.isAlive(player) && jsonPlayerPlays == 0;
						// player could die next iteration and still be alive
						final boolean stillAlive = killed ? jsonPlayerDead > roundTemp : true;
						// temp counter for player bet makers -again, needs to be constant throughout all bets (concerning this player) that are consumed in this round, for prize calculation
						int playerBetMakers = player.getBetMakers();
						// kill player? has to have been killed at some point and not be alive in this round iteration
						boolean updatePlayer = killed && !stillAlive;
						if (updatePlayer) {
							player.setStatus(PlayerStatus.KILLED.name());
							player.setRoundKilled(jsonPlayerDead);
							league.setAlivePlayers(league.getAlivePlayers() - 1);
							league.setKilledPlayers(league.getKilledPlayers() + 1);
						}
						// update pending bets on this player?
						final List<Bet> playerBets = this.betRepo.findByPlayer(jsonPlayerName);
						final List<Bet> activeBets = BetUtils.getActiveBets(playerBets);
						for (Bet bet : activeBets) {
							if (bet instanceof RoundsBet) {
								RoundsBet roundsBet = (RoundsBet)bet;
								// Rounds bets are consumed when: 1) player dies, 2) rounds estimate expires
								final boolean roundsUp = roundsBet.getBetOnRound() + roundsBet.getEnduresRounds() <= roundTemp;
								if (!stillAlive || roundsUp) {
									final double betKudos = bet.getKudos();
									// We are handling player bet. To update account bet simultaneously, wire player bet into account's bet list (both player and account are gonna get updated)
									final Account account = this.accountRepo.findOne(bet.getAccount());
									final List<Bet> accountBets = account.getBets();
									final int acSize = accountBets.size();
									for (int j = 0; j < acSize; ++j) {
										final Bet accountBet = accountBets.get(j);
										if (accountBet.equals(bet)) {
											accountBets.set(j, bet);
											break;
										}
									}
									// update bet counters
									account.setActiveBets(account.getActiveBets() - 1);
									player.setActiveBets(player.getActiveBets() - 1);
									league.setActiveBets(league.getActiveBets() - 1);
									league.setFinishedBets(league.getFinishedBets() + 1);
									// handle bet result -calculate away!
									if (stillAlive) {
										// bet maker wins
										final double betRate = BetUtils.calculateRate(roundsBet, player, league);
										final double betPrize = BetUtils.calculateBetPrize(roundsBet, betRate);
										final double betBenefits = BetUtils.calculateBetBenefits(roundsBet, betPrize);
										// update kudos variables for account, league, and bet
										account.setKudos(account.getKudos() + betPrize);
										league.setKudosWon(league.getKudosWon() + betBenefits);
										bet.setBalance(betBenefits);
										// update league bets-won counter and bet status
										league.setBetsWon(league.getBetsWon() + 1);
										bet.setStatus(BetStatus.WIN.name());
									} else {
										// bet maker loses
										bet.setStatus(BetStatus.LOSS.name());
										bet.setBalance(betKudos);
										league.setKudosLost(league.getKudosLost() + betKudos);
										league.setBetsLost(league.getBetsLost() + 1);
									}
									// less kudos at stake now
									league.setKudosAtStake(league.getKudosAtStake() - betKudos);
									// update bet maker counters
									if (!AccountUtils.hasBetsOnPlayer(account, player)) {
										--playerBetMakers;
									}
									if (!AccountUtils.hasBetsOnLeague(account, league)) {
										--leagueBetMakers;
									}
									// 
									this.accountRepo.save(account);
									this.betRepo.save(bet);
									// and signal update
									updatePlayer = true;
								}
							}
						}
						// save player if need be (either they got killed, or they made someone richer)
						if (updatePlayer) {
							player.setBetMakers(playerBetMakers);
							this.playerRepo.save(player);
						}
					}
					// update league bet makers count now, so it's ready for next round
					league.setBetMakers(leagueBetMakers);
				}
				// update league if someone changed handle (just needs to update popularPlayers), or a new round finished (round and other stuff has changed)
				final boolean updateLeague = handleUpdated || newRoundComplete; 
				if (updateLeague) {
					if (newRoundComplete) {
						league.setRoundTemp(round);
						league.setRound(round);
					}
					final List<Player> leaguePlayers = entityStash.getLeaguePlayers(league);
					final List<PopularPlayer> popularPlayers = LeagueUtils.getPopularPlayers(leaguePlayers, league);
					league.setPopularPlayers(popularPlayers);
					this.leagueRepo.save(league);
				}
			}
		} catch (JsonProcessingException exception) {
			this.logger.error(exception.getMessage());
			return false;
		} catch (IOException exception) {
			this.logger.error(exception.getMessage());
			return false;
		}
		return true;
	}
	
	public void migrate(final String text, final EntityStash entityStash) throws Exception {
		final League league = entityStash.getLeague("FIB");
		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode jsonData = mapper.readTree(text);
	
		double totalKudosWon = 0;
		double totalKudosLost = 0;
		double totalKudosAtStake = 0;
		
		int betMakers = 0;
		int activeBets = 0;
		int totalBetsWon = 0;
		int totalBetsLost = 0;
		int finishedBets = 0;
		
		for (int i = 0; i < jsonData.size(); i++) {
			final JsonNode jsonPlayer = jsonData.get(i);
			final JsonNode jsonPass = jsonPlayer.get("password");
			if (jsonPass != null) {
				int accountActiveBets = 0;
				final String pass = jsonPass.asText();
				final String salt = jsonPlayer.get("salt").asText();
				final String activationToken = jsonPlayer.get("activationToken").asText();
				final String name = jsonPlayer.get("username").asText();
				final String email = jsonPlayer.get("email").asText();
				//System.out.println(name + " " + email + " " + salt + " " + pass);
				
	
				final List<Bet> bets = new ArrayList<>();
				final JsonNode jsonBill = jsonPlayer.get("bill");
				Double kudos = jsonBill.get("kudos").asDouble();
				
				boolean newBetMaker = false;
				final JsonNode jsonBets = jsonBill.get("bets");
				final Set<String> pSet = new HashSet<>();
				for (int j = 0; j < jsonBets.size(); j++) {
					final RoundsBet rBet = new RoundsBet();
					final JsonNode jsonBet = jsonBets.get(j);
					String player =  jsonBet.get("player").asText();
					player = player.toLowerCase().replace(" ", ".");
					if (PlayerUtils.isWildcardPlayer(player)) {
						continue;
					}
					final int betOnRound = jsonBet.get("betOnRound").asInt();
					final int enduresRounds = jsonBet.get("enduresRounds").asInt();
					final int betKudos = jsonBet.get("kudos").asInt();
					final boolean betActive = jsonBet.get("active").asBoolean();
					double balance = 0d;
					if (betActive) {
						newBetMaker = true;
						activeBets++;
						accountActiveBets++;
						totalKudosAtStake += betKudos;
						rBet.setStatus(BetStatus.PENDING.name());
						
						final Player playa = entityStash.getPlayer(player);
						if (playa == null) {
							this.logger.error(player);
						}
						playa.setActiveBets(playa.getActiveBets() + 1);
						if (pSet.add(player)) {
							playa.setBetMakers(playa.getBetMakers() + 1);
						}
						this.playerRepo.save(playa);
						
					} else {
						finishedBets++;
						balance = jsonBet.get("balance").asDouble();
						final boolean betWon = jsonBet.get("won").asBoolean();
						if (betWon) {
							balance -= betKudos;
							totalKudosWon += balance;
							++totalBetsWon;
							rBet.setStatus(BetStatus.WIN.name());
						} else {
							totalKudosLost += balance;
							++totalBetsLost;
							rBet.setStatus(BetStatus.LOSS.name());
						}
					}
					
					rBet.setAccount(name);
					rBet.setLeague("FIB");
					rBet.setPlayer(player);
					rBet.setBalance(balance);
					rBet.setBetOnRound(betOnRound);
					rBet.setType(BetType.ROUNDS.name());
					rBet.setEnduresRounds(enduresRounds);
					rBet.setKudos((double)betKudos);
					rBet.setPlayersAlive(107 - betOnRound);
					bets.add(rBet);
					this.betRepo.insert(rBet);
				}
				if (newBetMaker) {
					betMakers++;
				}
				
				final Account account = new Account();
				account.setName(name);
				account.setEmail(email);
				account.setHashedPass(pass);
				account.setActivationToken(activationToken);
				account.setSalt(salt);
				account.setStatus(AccountStatus.STATUS_APPROVED.name());
				account.setRoles(new ArrayList<String>());
				account.addRole(AccountRole.ROLE_USER.name());
				account.setCreatedOn(new Date());
				account.setActiveBets(accountActiveBets);
				account.setBets(bets);
				account.setKudos(kudos);
				this.accountRepo.insert(account);
			}
		}
		
		league.setKudosWon(totalKudosWon);
		league.setKudosLost(totalKudosLost);
		league.setKudosAtStake(totalKudosAtStake);
		league.setBetMakers(betMakers);
		league.setActiveBets(activeBets);
		league.setBetsWon(totalBetsWon);
		league.setBetsLost(totalBetsLost);
		league.setFinishedBets(finishedBets);
		this.leagueRepo.save(league);
	}

}
