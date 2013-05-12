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
package net.llop.porraeda.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.llop.porraeda.model.DaHouse;
import net.llop.porraeda.model.Stats;
import net.llop.porraeda.model.UserAccount;
import net.llop.porraeda.model.bet.BaseBet;
import net.llop.porraeda.model.bet.BetType;
import net.llop.porraeda.model.bet.Bill;
import net.llop.porraeda.model.bet.EnduranceBet;
import net.llop.porraeda.repository.DaHouseRepository;
import net.llop.porraeda.repository.StatsRepository;
import net.llop.porraeda.repository.UserAccountRepository;
import net.llop.porraeda.util.BetUtils;
import net.llop.porraeda.util.CastUtils;
import net.llop.porraeda.util.RacoUtils;

import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Handles bet related functionality.
 * @author Llop
 */
@Service public class BetService {
	
	
	// Thanks a ton to Jordi Petit! He whipped up the update data service backend for battle-royale.jutge.org in no time!
	private final static String JUTGE_NO_HANDLE = "null";
	private final static String JUTGE_CURRENT_ROUND = "ronda_actual";
	private final static String JUTGE_CONTESTANT = "representant";
	private final static String JUTGE_DEAD = "mort";
	private final static String JUTGE_PLAYS = "juga";
	private final static String JUTGE_NAME = "nom";
	private final static String JUTGE_USERS = "usuaris";
	
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired private DaHouseRepository daHouseRepository;
	@Autowired private StatsRepository statsRepository;
	@Autowired private UserAccountRepository userRepository;
	
	
	public DaHouse getDaMastaHouse() {
		this.logger.info("BetService.getDaMastaHouse");
		return this.daHouseRepository.findOne(BetUtils.MASTA_HOUSE);
	}
	
	public Stats getDaMastaHouseStats() {
		this.logger.info("BetService.getDaMastaHouseStats");
		return this.statsRepository.findOne(BetUtils.MASTA_HOUSE);
	}
	
	public void makeABet(final UserAccount userAccount, final BaseBet bet, final DaHouse daHouse, final Stats stats) {
		this.logger.info("BetService.makeABet");
		// update user bill
		final Bill bill = userAccount.getBill();
		this.incrementIfPlayerFirst(bill, bet, daHouse);
		bill.setKudos(bill.getKudos() - bet.getKudos());
		bill.setActiveBets(bill.getActiveBets() + 1);
		final List<BaseBet> bets = bill.getBets();
		bets.add(0, bet);
		this.userRepository.save(userAccount);
		// update da house stats
		final Double currentlyBetKudos = stats.getCurrentlyBetKudos() + bet.getKudos();
		final Long totalActiveBets = stats.getTotalActiveBets() + 1;
		final Double averageKudosForBet = currentlyBetKudos / totalActiveBets;
		stats.setCurrentlyBetKudos(currentlyBetKudos);
		stats.setTotalActiveBets(totalActiveBets);
		stats.setAverageKudosForBet(averageKudosForBet);
		updateMostVotedPlayersStats(stats, daHouse);
		this.statsRepository.save(stats);
	}

	@SuppressWarnings("rawtypes")
	private void updateMostVotedPlayersStats(final Stats stats, final DaHouse daHouse) {
		final List<Pair<Integer, String>> votersForPlayers = new ArrayList<>();
		final Set<Entry<String, Map>> players = daHouse.getPlayers().entrySet();
		for (final Entry<String, Map> entry : players) {
			final String playerName = entry.getKey();
			final Map playerData = entry.getValue();
			final Boolean isAlive = (Boolean)playerData.get(DaHouse.PLAYER_ALIVE);
			final Integer numVotersForPlayer = CastUtils.toInteger(playerData.get(DaHouse.BETS));
			if (isAlive && numVotersForPlayer > 0) {
				final Pair<Integer, String> votersForPlayer = Pair.of(numVotersForPlayer, playerName);
				votersForPlayers.add(votersForPlayer);
			}
		}
		Collections.sort(votersForPlayers);
		Collections.reverse(votersForPlayers);
		int counter = 0;
		final Integer totalBetMakers = daHouse.getBetMakers();
		final List<Pair<String, String>> mostVotedPlayers = new ArrayList<>();
		final Iterator<Pair<Integer, String>> iter = votersForPlayers.iterator();
		while (iter.hasNext() && counter < Stats.MAX_MOST_VOTED_PLAYERS) {
			final Pair<Integer, String> votersForPlayer = iter.next();
			final String playerName = votersForPlayer.getRight();
			final String playerHandle = (String)daHouse.getPlayers().get(playerName).get(DaHouse.HANDLE);
			final Integer numVotersForPlayer = votersForPlayer.getLeft();
			final Double percentage = 100d * numVotersForPlayer / totalBetMakers;
			final Pair<String, String> mostVotedPlayer = Pair.of(playerName + " (<strong>" + playerHandle + "</strong>)", BetUtils.formatDouble(percentage));
			mostVotedPlayers.add(mostVotedPlayer);
			counter++;
		}
		stats.setMostVotedPlayers(mostVotedPlayers);
	}
	
	public List<String> getNewsUpdates(final Stats stats) {
		final List<String> newsUpdates = new ArrayList<>();
		final String totalKudosWon = "Els usuaris han guanyat <span class='o-s-b'>" + stats.getTotalKudosWon().intValue() + "</span> kudos";
		newsUpdates.add(totalKudosWon);
		final String currentlyBetKudos = "Ara mateix hi ha <span class='o-s-b'>" + stats.getCurrentlyBetKudos().intValue() + "</span> kudos en joc";
		newsUpdates.add(currentlyBetKudos);
		if (stats.getTotalActiveBets() != 0) {
			final Double averageKudosPerBetDouble = stats.getCurrentlyBetKudos() / stats.getTotalActiveBets();
			final String averageKudosPerBet = "La mitja de kudos per aposta és de <span class='o-s-b'>" + averageKudosPerBetDouble.intValue() + "</span>";
			newsUpdates.add(averageKudosPerBet);
		}
		if (stats.getTotalFinishedBets() != 0) {
			final Double wonBetsPercentageDouble = 100d * stats.getTotalWonBets() / stats.getTotalFinishedBets();
			final String wonBetsPercentage = "Els usuaris han encertat el <span class='o-s-b'>" + BetUtils.formatDouble(wonBetsPercentageDouble) + "%</span> de les apostes";
			newsUpdates.add(wonBetsPercentage);
		}
		return newsUpdates;
	}
	
	private boolean hasBetsOnPlayer(final String playerName, final Bill bill) {
		final List<BaseBet> bets = bill.getBets();
		for (BaseBet betTemp : bets) {
			if (betTemp.getActive() && betTemp.getPlayer().equals(playerName)) {
				return true;
			}
		}
		return false;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void incrementIfPlayerFirst(final Bill bill, final BaseBet bet, final DaHouse daHouse) {
		// add 1 bet maker if this is their first bet
		if (bill.getActiveBets() == 0) daHouse.setBetMakers(daHouse.getBetMakers() + 1);
		// add 1 to player's bets if this is user's first bet on player
		final String playerName = BetUtils.sanitizePlayerName(bet.getPlayer());
		if (!this.hasBetsOnPlayer(playerName, bill)) {
			final Map map = daHouse.getPlayers().get(playerName);
			map.put(DaHouse.BETS, CastUtils.toInteger(map.get(DaHouse.BETS)) + 1);
			this.daHouseRepository.save(daHouse);
		}
	}
	
	public Double getCurrentBalance(final BaseBet bet, final DaHouse daHouse) {
		this.logger.info("BetService.getCurrentBalance");
		return this.getRate(bet, daHouse) * bet.getKudos();
	}
	
	public Double getRate(final BaseBet bet, final DaHouse daHouse) {
		this.logger.info("BetService.getRate");
		Double odds = 1d;
		Integer estudiantsApostant = daHouse.getBetMakers();
		String playerName = BetUtils.sanitizePlayerName(bet.getPlayer());
		Integer estudiantsApostenPelMateix = CastUtils.toInteger(daHouse.getPlayers().get(playerName).get(DaHouse.BETS));
		if (estudiantsApostant == 0) estudiantsApostant = 1;
		if (estudiantsApostenPelMateix == 0) estudiantsApostenPelMateix = 1;
		final Double factor = (double)estudiantsApostenPelMateix / estudiantsApostant;
		final String betType = bet.getType();
		if (BetType.AGUANTA.name().equals(betType)) {
			EnduranceBet enduranceBet = (EnduranceBet)bet;
			Integer n = daHouse.getContestants() - enduranceBet.getBetOnRound();
			Integer f = n - enduranceBet.getEnduresRounds();
			while (n > f) odds *= 1d - (1d / n--);
		} else if (BetType.SEMIS.name().equals(betType)) odds /= 4d;
		else if (BetType.FINAL.name().equals(betType)) odds /= 16d;
		odds += (1d - odds) * factor;
		return 1d / odds;
	}
	
	public Double getFutureBetRate(final UserAccount userAccount, final BaseBet bet, final DaHouse daHouse) {
		this.logger.info("BetService.getFutureBetRate");
		final Bill bill = userAccount.getBill();
		Double odds = 1d;
		Integer estudiantsApostant = daHouse.getBetMakers();
		if (bill.getActiveBets() == 0) ++estudiantsApostant;
		String playerName = BetUtils.sanitizePlayerName(bet.getPlayer());
		Integer estudiantsApostenPelMateix = CastUtils.toInteger(daHouse.getPlayers().get(playerName).get(DaHouse.BETS));
		if (!hasBetsOnPlayer(playerName, bill)) ++estudiantsApostenPelMateix;
		if (estudiantsApostant == 0) estudiantsApostant = 1;
		if (estudiantsApostenPelMateix == 0) estudiantsApostenPelMateix = 1;
		final Double factor = (double)estudiantsApostenPelMateix / estudiantsApostant;
		final String betType = bet.getType();
		if (BetType.AGUANTA.name().equals(betType)) {
			EnduranceBet enduranceBet = (EnduranceBet)bet;
			Integer n = daHouse.getContestants() - enduranceBet.getBetOnRound();
			Integer f = n - enduranceBet.getEnduresRounds();
			while (n > f) odds *= 1d - (1d / n--);
		} else if (BetType.SEMIS.name().equals(betType)) odds /= 4d;
		else if (BetType.FINAL.name().equals(betType)) odds /= 16d;
		odds += (1d - odds) * factor;
		return 1d / odds;
	}
	
	public boolean validatePlayer(final String name, final DaHouse daHouse) { 
		this.logger.info("BetService.validatePlayer");
		return this.playerExists(name, daHouse);
	}
	
	public boolean validatePlayerAlive(final String name, final DaHouse daHouse) {
		this.logger.info("BetService.validatePlayerAlive");
		// Validar presencia i format
		//if (!RacoUtils.validaNom(name)) return false;
		// boolean isValid = true;
		// Validar contr Datatable
		return this.isPlayerAliveAndHasHandle(name, daHouse);
		// Validar contra el Raco
//		try {
//			List<NameValuePair> params = new ArrayList<>();
//			params.add(new BasicNameValuePair("service", "https://raco.fib.upc.edu/comunitat/perfil.jsp?usuari=" + name.trim()));
//			params.add(new BasicNameValuePair("loginDirecte", "true"));
//			params.add(new BasicNameValuePair("username", System.getenv(RacoUtils.ENV_RACO_USR)));
//			params.add(new BasicNameValuePair("password", System.getenv(RacoUtils.ENV_RACO_PASS)));
//			HttpPost post = new HttpPost("https://raco.fib.upc.edu/cas/login");
//			post.setEntity(new UrlEncodedFormEntity(params));
//			DefaultHttpClient httpClient = new DefaultHttpClient();
//			httpClient.setRedirectStrategy(new DefaultRedirectStrategy() {                
//		        public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)  {
//		            boolean isRedirect = false;
//		            try { isRedirect = super.isRedirected(request, response, context); } 
//		            catch (ProtocolException e) { logger.error(e.getMessage()); }
//		            if (!isRedirect) {
//		                int responseCode = response.getStatusLine().getStatusCode();
//		                isRedirect = responseCode == 301 || responseCode == 302;
//		            }
//		            return isRedirect;
//		        }
//		    });
//			HttpResponse response = httpClient.execute(post);
//			HttpEntity entity = response.getEntity();
//			String responseBody = EntityUtils.toString(entity);
//			isValid = responseBody.indexOf(RacoUtils.GRAU_EDA) != -1;
//		} catch (Exception e) {
//			this.logger.error(e.getMessage());
//			isValid = false;
//		}
		// return isValid;
	}
	
	@SuppressWarnings("rawtypes")
	public List<String> getAlivePlayerNames(final DaHouse daHouse) {
		this.logger.info("BetService.getAlivePlayerNames");
		final List<String> alivePlayerNames = new ArrayList<String>();
		for (Entry<String, Map> entry : daHouse.getPlayers().entrySet()) {
			final Map value = entry.getValue();
			final Boolean isAlive = (Boolean)value.get(DaHouse.PLAYER_ALIVE);
			final String handle = (String)value.get(DaHouse.HANDLE);
			final Boolean hasHandle = !BetUtils.NO_PLAYER.equals(handle);
			if (isAlive && hasHandle) {
				final String alivePlayerName = entry.getKey() + "," + entry.getValue().get(DaHouse.HANDLE);
				alivePlayerNames.add(alivePlayerName);
			}
		}
		Collections.sort(alivePlayerNames);
		return alivePlayerNames;
	}
	
	private boolean playerExists(final String player, final DaHouse daHouse) {
		final String sanitizedPlayerName = BetUtils.sanitizePlayerName(player);
		return daHouse.getPlayers().containsKey(sanitizedPlayerName);
	}
	
	@SuppressWarnings("rawtypes")
	private boolean isPlayerAliveAndHasHandle(final String player, final DaHouse daHouse) {
		if (!this.playerExists(player, daHouse)) return false;
		final String sanitizedPlayerName = BetUtils.sanitizePlayerName(player);
		final Map<String, Map> players = daHouse.getPlayers();
		final Map playerData = players.get(sanitizedPlayerName);
		final Boolean isAlive = (Boolean)playerData.get(DaHouse.PLAYER_ALIVE);
		final String handle = (String)playerData.get(DaHouse.HANDLE);
		return isAlive && !BetUtils.NO_PLAYER.equals(handle);
	}
	
	/**
	 * Crazy method to update the database with new data from battle-royale.jutge.org
	 * It's gonna get invoked on a minutely basis by a scheduled cron job (a daemon thread)
	 * @param jsonData
	 * @param daHouse
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean updateDaHouse(final String jsonData, final DaHouse daHouse, final Stats stats) {
		this.logger.info("BetService.updateDaHouse");
		try {
			// Parse JSON
			final ObjectMapper mapper = new ObjectMapper();
			final JsonNode actualObj = mapper.readTree(jsonData);
			final Integer rondaActual = actualObj.get(JUTGE_CURRENT_ROUND).asInt();
			final Map<String, Map> players = daHouse.getPlayers();
			// maybe no need to update
			final boolean roundAdvanced = rondaActual > daHouse.getCurrentRound();
			boolean update = roundAdvanced;
			// Iterate users
			Integer playersDown = daHouse.getPlayersDown();
			final JsonNode usuaris = actualObj.get(JUTGE_USERS);
			for (int i = 0; i < usuaris.size(); i++) {
				final JsonNode usuari = usuaris.get(i);
				final String nom = usuari.get(JUTGE_NAME).asText();
				final String nomNormal = RacoUtils.normalitzaNom(nom);
				final Integer juga = usuari.get(JUTGE_PLAYS).asInt();
				final Integer mort = usuari.get(JUTGE_DEAD).asInt();
				final String representant = usuari.get(JUTGE_CONTESTANT).asText();
				final Map playerData = players.get(nomNormal);
				if (playerData == null) continue;
				final Boolean isAlive = (Boolean)playerData.get(DaHouse.PLAYER_ALIVE);
				// update player alive
				if (isAlive && juga == 0) {
					playerData.put(DaHouse.PLAYER_ALIVE, Boolean.FALSE);
					playerData.put(DaHouse.ROUND_KILLED, mort);
					playersDown++;
					update = true;
				}
				// update handle too
				final String handle = (String)playerData.get(DaHouse.HANDLE);
				if (!JUTGE_NO_HANDLE.equals(representant) && !handle.equals(representant)) {
					playerData.put(DaHouse.HANDLE, representant);
					update = true;
				}
			}
			// if round advanced, check all player bets
			if (roundAdvanced) {
				Integer totalWonBets = stats.getTotalWonBets();
				Integer totalFinishedBets = stats.getTotalFinishedBets();
				Long totalActiveBets = stats.getTotalActiveBets();
				Double currentlyBetKudos = stats.getCurrentlyBetKudos();
				final List<UserAccount> usersList = this.userRepository.findAll();
				// ok, so it is possible to have more than 1 round within a minute (expected time between updates)
				// need to handle one round at a time so as not to break (bets on player) / (total bets) ratio, which is used to calculate bet value (in getCurrentBalance) for endurance bets
				for (int k = daHouse.getCurrentRound() + 1; k <= rondaActual; k++) {
					Integer betMakers = daHouse.getBetMakers();
					final Iterator<UserAccount> iterator = usersList.iterator();
					while (iterator.hasNext()) {
						boolean updateUser = false;
						final UserAccount userAccount = iterator.next();
						final Bill bill = userAccount.getBill();
						final List<BaseBet> userBets = bill.getBets();
						// iterate user bets
						for (int i = 0; i < userBets.size(); i++) {
							final BaseBet bet = userBets.get(i);
							final String player = bet.getPlayer();
							if (bet.getActive()) {
								final Map playerData = players.get(player);
								// player could die next iteration and still be alive
								final Boolean isAlive = (Boolean)playerData.get(DaHouse.PLAYER_ALIVE) ? true : (Integer)playerData.get(DaHouse.ROUND_KILLED) > k;
								// Per type basis
								if (bet instanceof EnduranceBet) {
									final EnduranceBet enduranceBet = (EnduranceBet)bet;
									// Endurance bets are consumed when: 1) player dies, 2) rounds estimate expires
									if (!isAlive || enduranceBet.getBetOnRound() + enduranceBet.getEnduresRounds() <= k) {
										if (isAlive) {
											// user wins
											final Double balance = this.getCurrentBalance(enduranceBet, daHouse);
											final Double daHousePays = balance - enduranceBet.getKudos();
											bill.setKudos(bill.getKudos() + balance);
											daHouse.setKudos(daHouse.getKudos() - daHousePays);
											stats.setTotalKudosWon(stats.getTotalKudosWon() + daHousePays);
											enduranceBet.setBalance(BetUtils.formatDouble(balance));
											enduranceBet.setWon(Boolean.TRUE);
											totalWonBets++;
										} else {
											// da house wins+
											daHouse.setKudos(daHouse.getKudos() + enduranceBet.getKudos());
											enduranceBet.setBalance(enduranceBet.getKudos().toString());
											enduranceBet.setWon(Boolean.FALSE);
										}
										final Integer userBetsOnPlayer = this.activeBetsOnPlayer(player, bill);
										if (userBetsOnPlayer == 1) {
											// This user's last bet on the player -subtract 1 from playerData.get(DaHouse.BETS)
											final Integer betsOnPlayer = CastUtils.toInteger(playerData.get(DaHouse.BETS));
											playerData.put(DaHouse.BETS, betsOnPlayer - 1);
										}
										bet.setActive(Boolean.FALSE);
										bill.setActiveBets(bill.getActiveBets() - 1);
										updateUser = true;
										// update stats
										currentlyBetKudos -= enduranceBet.getKudos();
										totalActiveBets--;
										totalFinishedBets++;
									}
								} else {
									// TODO: Improve semis bet type
									// Handle semis and final type bets
								}
							}
						}
						if (updateUser) {
							if (bill.getActiveBets() == 0) betMakers--;
							this.userRepository.save(userAccount);
						}
					}
					daHouse.setBetMakers(betMakers);
				}
				// save new stats
				stats.setCurrentlyBetKudos(currentlyBetKudos);
				stats.setTotalActiveBets(totalActiveBets);
				stats.setTotalFinishedBets(totalFinishedBets);
				stats.setTotalWonBets(totalWonBets);
				stats.setAverageKudosForBet(currentlyBetKudos / totalActiveBets);
				updateMostVotedPlayersStats(stats, daHouse);
				this.statsRepository.save(stats);
			}
			// Update current round and save
			if (update) {
				daHouse.setPlayersDown(playersDown);
				daHouse.setCurrentRound(rondaActual);
				this.daHouseRepository.save(daHouse);
			}
		} catch(Exception e) {
			this.logger.error(e.getMessage());
			return false;
		}
		this.logger.info("BetService.updateDaHouse OK!");
		return true;
	}
	
	/**
	 * Big Fat Warning!!! Run this function if you want to reset the database. 
	 * To ensure data integrity, all user bills will be restored to default: 1000 kudos, no bets 
	 * 
	 * @param jsonData Data to feed into the initial daHouse Master Document.
	 * Should include the curent round along with all players' data
	 * @return true unless an exception jumps due to JSON parsing
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean initDaHouse(final String jsonData) {
		this.logger.info("BetService.initDaHouse");
		try {
			final Integer zero = 0;
			final Double dZero = zero.doubleValue();
			final DaHouse daHouse = new DaHouse();
			daHouse.setId(BetUtils.MASTA_HOUSE);
			daHouse.setKudos(dZero);
			daHouse.setBetMakers(zero);
			Integer contestants = 0;
			Integer totalPlayers = 0;
			Integer playersDown = 0;
			final Map<String, Map> newPlayers = new HashMap<String, Map>();
			// Parse JSON
			final ObjectMapper mapper = new ObjectMapper();
			final JsonNode actualObj = mapper.readTree(jsonData);
			final Integer rondaActual = actualObj.get(JUTGE_CURRENT_ROUND).asInt();
			// Iterate users
			final JsonNode usuaris = actualObj.get(JUTGE_USERS);
			for (int i = 0; i < usuaris.size(); i++) {
				final JsonNode usuari = usuaris.get(i);
				final String nom = usuari.get(JUTGE_NAME).asText();
				final String nomNormal = RacoUtils.normalitzaNom(nom);
				final Integer juga = usuari.get(JUTGE_PLAYS).asInt();
				final Integer mort = usuari.get(JUTGE_DEAD).asInt();
				final String representant = usuari.get(JUTGE_CONTESTANT).asText();
				final Boolean hasPlayer = !JUTGE_NO_HANDLE.equals(representant);
				final Boolean alive = juga == 1;
				final Map newPlayerData = new HashMap();
				newPlayerData.put(DaHouse.BETS, zero);
				newPlayerData.put(DaHouse.HANDLE, hasPlayer ? representant : BetUtils.NO_PLAYER);
				newPlayerData.put(DaHouse.PLAYER_ALIVE, alive);
				newPlayerData.put(DaHouse.ROUND_KILLED, mort);
				newPlayers.put(nomNormal, newPlayerData);
				if (alive && hasPlayer) contestants++;
				if (!alive) playersDown++;
				totalPlayers++;
			}
			// save daHouse
			daHouse.setPlayers(newPlayers);
			daHouse.setContestants(contestants);
			daHouse.setPlayersDown(playersDown);
			daHouse.setTotalPlayers(totalPlayers);
			daHouse.setCurrentRound(rondaActual);
			this.daHouseRepository.save(daHouse);
			// reset stats
			final Stats stats = new Stats();
			stats.setId(BetUtils.MASTA_HOUSE);
			stats.setCurrentlyBetKudos(dZero);
			stats.setTotalKudosWon(dZero);
			stats.setTotalActiveBets(zero.longValue());
			stats.setAverageKudosForBet(dZero);
			stats.setTotalWonBets(zero);
			stats.setTotalFinishedBets(zero);
			stats.setMostVotedPlayers(new ArrayList<Pair<String, String>>());
			this.statsRepository.save(stats);
			// reset everybody's bets
			final List<UserAccount> usersList = this.userRepository.findAll();
			final Iterator<UserAccount> iterator = usersList.iterator();
			while (iterator.hasNext()) {
				final UserAccount userAccount = iterator.next();
				final Bill bill = new Bill();
				bill.setKudos(BetUtils.INITIAL_CREDIT);
				bill.setBets(new ArrayList<BaseBet>());
				bill.setActiveBets(0);
				userAccount.setBill(bill);
				this.userRepository.save(userAccount);
			}
		} catch(Exception e) {
			this.logger.error(e.getMessage());
			return false;
		}
		this.logger.info("BetService.initDaHouse OK!");
		return true;
	}
	
	// Duh!
	private Integer activeBetsOnPlayer(final String player, final Bill bill) {
		Integer val = 0;
		for (BaseBet bet : bill.getBets()) if (bet.getActive() && bet.getPlayer().equals(player)) val++;
		return val;
	}
	
}

