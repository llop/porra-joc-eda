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
package net.llop.porraeda.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.llop.porraeda.model.DaHouse;
import net.llop.porraeda.model.Stats;
import net.llop.porraeda.model.UserAccount;
import net.llop.porraeda.model.bet.BaseBet;
import net.llop.porraeda.model.bet.BetType;
import net.llop.porraeda.model.bet.Bill;
import net.llop.porraeda.model.bet.EnduranceBet;
import net.llop.porraeda.security.LoggedUserUtils;
import net.llop.porraeda.service.BetService;
import net.llop.porraeda.service.UserService;
import net.llop.porraeda.util.BetUtils;
import net.llop.porraeda.util.Routes;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User requests controller
 * @author Llop
 */
@Controller
@RequestMapping(Routes.USER)
public class UserController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired BetService betService;
	@Autowired UserService userService;
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value=Routes.MAKE_A_BET, method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody LinkedHashMap makeABet(final HttpServletRequest request, @RequestBody final LinkedHashMap map) {
		logger.info("UserController.makeABet");
		final LinkedHashMap response = new LinkedHashMap();
		final DaHouse daHouse = (DaHouse)request.getAttribute(BetUtils.DA_HOUSE);
		final Stats stats = (Stats)request.getAttribute(BetUtils.DA_HOUSE_STATS);
		final UserAccount userAccount = (UserAccount)request.getAttribute(LoggedUserUtils.LOGGED_USER); 
		if (this.validateParams(userAccount, map, response, daHouse)) {
			final String jugador = (String)map.get("jugador");
			final String kudos = (String)map.get("kudos");
			final String tipus = (String)map.get("tipus");
			BaseBet bet = null;
			if (BetType.AGUANTA.name().equals(tipus)) {
				final String rondas = (String)map.get("rondas");
				final EnduranceBet enduranceBet = new EnduranceBet();
				enduranceBet.setBetOnRound(daHouse.getCurrentRound());
				enduranceBet.setEnduresRounds(Integer.parseInt(rondas));
				bet = enduranceBet;
			} else bet = new BaseBet();
			bet.setKudos(Integer.parseInt(kudos));
			bet.setPlayer(BetUtils.sanitizePlayerName(jugador));
			bet.setType(tipus);
			bet.setActive(Boolean.TRUE);
			bet.setDate(new Date());
			this.betService.makeABet(userAccount, bet, daHouse, stats);
			this.buildOkResponse(userAccount, daHouse, stats, response);
		}
		return response;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void buildOkResponse(final UserAccount userAccount, final DaHouse daHouse, final Stats stats, final LinkedHashMap response) {
		// Add bets data
		final List betsData = new ArrayList();
		final Iterator<BaseBet> iterator = userAccount.getBill().getBets().iterator();
		while (iterator.hasNext()) {
			final BaseBet bet = iterator.next();
			final Map betMap = new HashMap();
			betMap.put("kudos", bet.getKudos());
			betMap.put("player", bet.getPlayer());
			betMap.put("active", bet.getActive());
			betMap.put("won", bet.getWon());
			betMap.put("balance", BetUtils.formatDouble(this.betService.getCurrentBalance(bet, daHouse)));
			betMap.put("type", bet.getType());
			if (BetType.AGUANTA.name().equals(bet.getType())) {
				final EnduranceBet enduranceBet = (EnduranceBet)bet;
				betMap.put("enduresRounds", enduranceBet.getEnduresRounds());
				betMap.put("betOnRound", enduranceBet.getBetOnRound());
			}
			betsData.add(betMap);
		}
		response.put("betsData", betsData);
		
		// Add general data
		final Map generalDataMap = new HashMap();
		final Bill bill = userAccount.getBill();
		final Integer maxEndureRounds = 100;  // TODO: update to currentRound - BetUtils.FINAL_PLAYERS;
		final List<String> alivePlayerNames = this.betService.getAlivePlayerNames(daHouse);
		final Integer currentRound = daHouse.getCurrentRound();
		generalDataMap.put("kudosLeft", BetUtils.formatDouble(bill.getKudos()));
		generalDataMap.put("kudosLeftInt", BetUtils.toInteger(bill.getKudos()));
		generalDataMap.put("maxEndureRounds", maxEndureRounds);
		generalDataMap.put("currentRound", currentRound);
		generalDataMap.put("alivePlayerNames", alivePlayerNames);
		generalDataMap.put("activeBets", bill.getActiveBets());
		response.put("generalData", generalDataMap);
		
		// add stats data
		final List<Map<String, String>> mvPlayersList = new ArrayList<>();
		final List<Pair<String, String>> mvPlayers = stats.getMostVotedPlayers();
		for (Pair<String, String> mvPlayer : mvPlayers) {
			Map<String, String> mvPlayerMap = new HashMap<>();
			mvPlayerMap.put("name", mvPlayer.getLeft());
			mvPlayerMap.put("popularity", mvPlayer.getRight());
			mvPlayersList.add(mvPlayerMap);
		}
		response.put("mostPopularPlayers", mvPlayersList);
		
		// news-updates
		List<String> newsUpdates = this.betService.getNewsUpdates(stats);
		response.put("newsUpdates", newsUpdates);
 	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean validateParams(final UserAccount userAccount, final LinkedHashMap request, final LinkedHashMap response, final DaHouse daHouse) {
		final LinkedHashMap map = new LinkedHashMap();
		final String jugador = (String)request.get("jugador");
		final String kudos = (String)request.get("kudos");
		final String tipus = (String)request.get("tipus");
		// Validar que jugador existeix i esta viu encara.
		if (!this.betService.validatePlayerAlive(jugador, daHouse)) this.addError("Jugador no disponible!", map);
		Integer kudosInt = null;
		try { 
			kudosInt = Integer.parseInt(kudos); 
			// Te prou kudos ?
			if (StringUtils.isBlank(kudos) || userService.validateKudos(kudosInt, userAccount)) this.addError("No tens prou kudos!!!!", map);
		} catch (Exception e) { this.addError("Format kudos!!!!", map); }
		if (userAccount.getBill().getActiveBets() >= BetUtils.MAX_USER_BETS) this.addError("No pots tenir més de " + BetUtils.MAX_USER_BETS + " apostes actives!!!!", map);
		// tracta tipus d'aposta
		if (BetType.AGUANTA.name().equals(tipus)) {
			final String rondas = (String)request.get("rondas");
			Integer rondasInt = null;
			try { 
				rondasInt = Integer.parseInt(rondas);
				if (daHouse.getCurrentRound() + rondasInt > daHouse.getLastRound()) this.addError("No queden prou rondes!!!!", map);
			} catch (Exception e) { this.addError("Format rondes!!!!", map); }
		} else if (BetType.SEMIS.name().equals(tipus) || BetType.FINAL.name().equals(tipus)) {
			// No disponible fins la final
			if (daHouse.getCurrentRound() < daHouse.getLastRound()) this.addError("Mode d'aposta encara no disponible!!!!", map);
		} else this.addError("Mode d'aposta no disponible!!!!", map);
		if (map.isEmpty()) return true;
		response.putAll(map);
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value=Routes.ENDURANCE_BET, method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody LinkedHashMap getEnduranceBetRate(@PathVariable final Integer kudos, @PathVariable final String jugador, @PathVariable final Integer rondes, final HttpServletRequest request) {
		logger.info("UserController.getEnduranceBetRate");
		final LinkedHashMap response = new LinkedHashMap();
		final DaHouse daHouse = (DaHouse)request.getAttribute(BetUtils.DA_HOUSE);
		final UserAccount userAccount = (UserAccount)request.getAttribute(LoggedUserUtils.LOGGED_USER); 
		if (this.betService.validatePlayerAlive(jugador, daHouse)) {
			final EnduranceBet bet = new EnduranceBet();
			bet.setBetOnRound(daHouse.getCurrentRound());
			bet.setEnduresRounds(rondes);
			bet.setKudos(kudos);
			bet.setPlayer(jugador);
			bet.setType(BetType.AGUANTA.name());
			final Double rate = this.betService.getFutureBetRate(userAccount, bet, daHouse);
			final String formattedeRate = rate == null || rate.isNaN() ? "?" : BetUtils.formatDouble(rate);
			response.put("rate", formattedeRate);
		} else this.addError("Jugador mort", response);
		return response;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value=Routes.BASE_BET, method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody LinkedHashMap getBaseBetRate(@PathVariable final String tipus, @PathVariable final Integer kudos, @PathVariable final String jugador, final HttpServletRequest request) {
		logger.info("UserController.getBaseBetRate");
		final LinkedHashMap response = new LinkedHashMap();
		final DaHouse daHouse = (DaHouse)request.getAttribute(BetUtils.DA_HOUSE);
		final UserAccount userAccount = (UserAccount)request.getAttribute(LoggedUserUtils.LOGGED_USER); 
		if (this.validateTipus(tipus) && this.betService.validatePlayerAlive(jugador, daHouse)) {
			final BaseBet bet = new BaseBet();
			bet.setKudos(kudos);
			bet.setPlayer(jugador);
			bet.setType(tipus);
			final Double rate = this.betService.getFutureBetRate(userAccount, bet, daHouse);
			final String formattedeRate = rate == null || rate.isNaN() ? "?" : BetUtils.formatDouble(rate);
			response.put("rate", formattedeRate);
		} else this.addError("Tipus desconegut o jugador mort", response);
		return response;
	}
	
	private boolean validateTipus(final String tipus) {
		if (StringUtils.isBlank(tipus)) return false;
		if (!(BetType.AGUANTA.name().equals(tipus) || BetType.SEMIS.name().equals(tipus) || BetType.FINAL.name().equals(tipus))) return false;
		return true;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addError(final String errorMessage, final LinkedHashMap response) {
		List<String> errors = (List<String>)response.get("errors");
		if (errors == null) {
			errors = new ArrayList<String>();
			response.put("errors", errors);
		}
		errors.add(errorMessage);
	}
	
}