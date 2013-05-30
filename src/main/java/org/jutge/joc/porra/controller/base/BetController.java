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
package org.jutge.joc.porra.controller.base;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.jutge.joc.porra.entitystash.annotation.EntityStashManaged;
import org.jutge.joc.porra.entitystash.aspect.EntityStashAspect;
import org.jutge.joc.porra.entitystash.box.MessageBox;
import org.jutge.joc.porra.entitystash.module.EntityStashEntityModule;
import org.jutge.joc.porra.entitystash.module.EntityStashViewModule;
import org.jutge.joc.porra.entitystash.service.EntityStashViewService;
import org.jutge.joc.porra.entitystash.stash.EntityStash;
import org.jutge.joc.porra.exception.BetException;
import org.jutge.joc.porra.exception.PorraException;
import org.jutge.joc.porra.model.account.Account;
import org.jutge.joc.porra.model.bet.Bet;
import org.jutge.joc.porra.model.bet.BetType;
import org.jutge.joc.porra.model.bet.RoundsBet;
import org.jutge.joc.porra.model.bet.WinnerBet;
import org.jutge.joc.porra.model.league.League;
import org.jutge.joc.porra.model.player.Player;
import org.jutge.joc.porra.service.BetService;
import org.jutge.joc.porra.service.PlayerService;
import org.jutge.joc.porra.utils.BetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Bet controller
 * @author Llop
 */
@Controller
public class BetController {
	
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired BetService betService;
	@Autowired PlayerService playerService;
	@Autowired private EntityStashViewService entityStashViewService;
	
	@Autowired private MessageSource messageSource;
	
	
	@ExceptionHandler(PorraException.class) 
    public String exceptionHandler(PorraException exception, HttpServletRequest request) {
		this.logger.info("BetController.exceptionHandler");
		request.setAttribute("errorMessages", exception.getErrorMessages());
		return "/desktop/error";
    }
	
	
	@EntityStashManaged(entities=EntityStashEntityModule.ALL, views=EntityStashViewModule.NONE)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/usuari/aposta-guanyador", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody LinkedHashMap makeWinnerBet(@RequestBody final LinkedHashMap map, final EntityStash entityStash, final HttpServletRequest request, final Locale locale) {
		this.logger.info("BetController.makeWinnerBet");
		final LinkedHashMap response = new LinkedHashMap();
		try {
			final WinnerBet bet = (WinnerBet)this.createBetInstance(map, entityStash, locale);
			this.betService.newWinnerBet(bet, entityStash);
			this.createResponse(response, entityStash, locale);
		} catch (final BetException exception) {
			response.put("errors", exception.getErrorMessages());
		}
		return response;
	}

	@EntityStashManaged(entities=EntityStashEntityModule.ALL, views=EntityStashViewModule.NONE)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/usuari/aposta-guanyador/{jugador}", method=RequestMethod.DELETE, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody LinkedHashMap deleteWinnerBet(@PathVariable final String jugador, final EntityStash entityStash, final HttpServletRequest request, final Locale locale) {
		this.logger.info("BetController.deleteWinnerBet");
		final LinkedHashMap response = new LinkedHashMap();
		try {
			this.betService.deleteWinnerBet(jugador.replace(" ", "."), entityStash);
			this.createResponse(response, entityStash, locale);
		} catch (final BetException exception) {
			response.put("errors", exception.getErrorMessages());
		}
		return response;
	}
	
	private List<String> validate4PlayerFinalAcion(final String player1, final String player2, final String player3, final String player4, final EntityStash entityStash, final Locale locale) throws PorraException {
		final Account account = entityStash.getAccount();
		if (!"albert lobo".equals(account.getName())) {
			final List<MessageBox> errorMessages = new ArrayList<>();
			final String message = this.messageSource.getMessage("exception.invalidUser", null, locale);
			final MessageBox messageBox = new MessageBox("invalidUser", message, new ArrayList<String>());
			errorMessages.add(messageBox);
			throw new PorraException(errorMessages);
		}
		final List<String> playerNames = new ArrayList<>();
		playerNames.add(player1.replace(" ", "."));
		playerNames.add(player2.replace(" ", "."));
		playerNames.add(player3.replace(" ", "."));
		playerNames.add(player4.replace(" ", "."));
		for (final String playerName : playerNames) {
			final Player player = entityStash.getPlayer(playerName);
			if (player == null) {
				final List<MessageBox> errorMessages = new ArrayList<>();
				final String message = this.messageSource.getMessage("exception.invalidPlayer", null, locale);
				final MessageBox messageBox = new MessageBox("invalidPlayer", message, new ArrayList<String>());
				errorMessages.add(messageBox);
				throw new PorraException(errorMessages);
			}
		}
		return playerNames;
	}
	
	@EntityStashManaged(entities=EntityStashEntityModule.ALL, views=EntityStashViewModule.NONE)
	@RequestMapping(value="/usuari/consumir-ronda-final/{guanyador}/{perdedor1}/{perdedor2}/{perdedor3}/{tipus}", method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String consumeFinalRound(@PathVariable final String guanyador, @PathVariable final String perdedor1, @PathVariable final String perdedor2, @PathVariable final String perdedor3, 
			@PathVariable final String tipus, final EntityStash entityStash, final HttpServletRequest request, final Locale locale) {
		this.logger.info("BetController.consumeFinalRound");
		final List<String> playerNames = this.validate4PlayerFinalAcion(guanyador, perdedor1, perdedor2, perdedor3, entityStash, locale);
		this.betService.consumeFinalRound(playerNames, entityStash, tipus);
		return "/desktop/update";
	}
	
	@EntityStashManaged(entities=EntityStashEntityModule.ALL, views=EntityStashViewModule.NONE)
	@RequestMapping(value="/usuari/bloquejar-finalistes/{jugador1}/{jugador2}/{jugador3}/{jugador4}", method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String lockPlayers(@PathVariable final String jugador1, @PathVariable final String jugador2, @PathVariable final String jugador3, @PathVariable final String jugador4, 
			final EntityStash entityStash, final HttpServletRequest request, final Locale locale) {
		this.logger.info("BetController.lockPlayers");
		final List<String> playerNames = this.validate4PlayerFinalAcion(jugador1, jugador2, jugador3, jugador4, entityStash, locale);
		this.playerService.lockPlayers(playerNames, entityStash);
		return "/desktop/update";
	}
	
	@EntityStashManaged(entities=EntityStashEntityModule.ALL, views=EntityStashViewModule.NONE)
	@RequestMapping(value="/usuari/desbloquejar-finalistes/{jugador1}/{jugador2}/{jugador3}/{jugador4}", method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String unlockPlayers(@PathVariable final String jugador1, @PathVariable final String jugador2, @PathVariable final String jugador3, @PathVariable final String jugador4, 
			final EntityStash entityStash, final HttpServletRequest request, final Locale locale) {
		this.logger.info("BetController.lockPlayers");
		final List<String> playerNames = this.validate4PlayerFinalAcion(jugador1, jugador2, jugador3, jugador4, entityStash, locale);
		this.playerService.unlockPlayers(playerNames, entityStash);
		return "/desktop/update";
	}
	
	
	@EntityStashManaged(entities=EntityStashEntityModule.ALL, views=EntityStashViewModule.NONE)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/usuari/aposta/tipus/rondes/kudos/{kudos}/jugador/{jugador}/rondes/{rondes}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody LinkedHashMap getRoundsBetRate(@PathVariable final Integer kudos, @PathVariable final String jugador, @PathVariable final Integer rondes, 
			final EntityStash entityStash, final HttpServletRequest request) {
		this.logger.info("BetController.getRoundsBetRate");
		final LinkedHashMap response = new LinkedHashMap();
		try {
			final RoundsBet roundsBet = this.createRoundsBetInstance(kudos, jugador, rondes, entityStash);
			final double betRate = this.betService.getBetRate(roundsBet, entityStash);
			final String formattedeRate = BetUtils.formatDouble(betRate);
			response.put("rate", formattedeRate);
		} catch (final BetException exception) {
			response.put("errors", exception.getErrorMessages());
		}
		return response;
	}
	
	private RoundsBet createRoundsBetInstance(final Integer kudos, final String jugador, final Integer rondes, final EntityStash entityStash) {
		final Account account = entityStash.getAccount();
		final String league = EntityStashAspect.DEFAULT_LEAGUE_NAME;
		final RoundsBet roundsBet = new RoundsBet();
		roundsBet.setAccount(account.getName());
		roundsBet.setLeague(league);
		roundsBet.setPlayer(jugador);
		roundsBet.setKudos(kudos.doubleValue());
		roundsBet.setEnduresRounds(rondes);
		return roundsBet;
	}
	
	
	@EntityStashManaged(entities=EntityStashEntityModule.ALL, views=EntityStashViewModule.NONE)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/usuari/fer-aposta", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody LinkedHashMap makeABet(@RequestBody final LinkedHashMap map, final EntityStash entityStash, final HttpServletRequest request, final Locale locale) {
		this.logger.info("BetController.makeABet");
		final LinkedHashMap response = new LinkedHashMap();
		try {
			final Bet bet = this.createBetInstance(map, entityStash, locale);
			this.betService.createBet(bet, entityStash);
			this.createResponse(response, entityStash, locale);
		} catch (final BetException exception) {
			response.put("errors", exception.getErrorMessages());
		}
		return response;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Bet createBetInstanceByType(final LinkedHashMap map, final List errorMessages, final Locale locale) {
		final String betType = (String)map.get("tipus");
		if (BetType.ROUNDS.name().equals(betType)) {
			final RoundsBet roundsBet = new RoundsBet();
			final String roundsStr = (String)map.get("rondas");
			try {
				final int rounds = Integer.parseInt(roundsStr);
				roundsBet.setEnduresRounds(rounds);
			} catch (final NumberFormatException exception) {
				final String message = this.messageSource.getMessage("exception.roundsFormat", null, locale);
				final MessageBox messageBox = new MessageBox("roundsFormat", message, new ArrayList<String>());
				errorMessages.add(messageBox);
			}
			try {
				final String kudos = (String)map.get("kudos");
				final Integer kudosInt = Integer.parseInt(kudos);
				if (roundsBet != null) {
					roundsBet.setKudos(kudosInt.doubleValue());
				}
			} catch (NumberFormatException exception) {
				final String message = this.messageSource.getMessage("exception.kudosFormat", null, locale);
				final MessageBox messageBox = new MessageBox("kudosFormat", message, new ArrayList<String>());
				errorMessages.add(messageBox);
			}
			return roundsBet;
		} else if (BetType.WINNER.name().equals(betType)) {
			return new WinnerBet();
		}
		return null;
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Bet createBetInstance(final LinkedHashMap map, final EntityStash entityStash, final Locale locale) throws BetException {
		// Find parameter format errors first
		final List errorMessages = new ArrayList();
		final Bet bet = this.createBetInstanceByType(map, errorMessages, locale);
		if (bet == null) {
			final String message = this.messageSource.getMessage("exception.betTypeUnavailable", null, locale);
			final MessageBox messageBox = new MessageBox("betTypeUnavailable", message, new ArrayList<String>());
			errorMessages.add(messageBox);
		}
		// Parameter format validated: fire exception if any errors were found
		if (!errorMessages.isEmpty()) {
			throw new BetException(errorMessages);
		}
		// proceed with remaining required fields
		final String playerName = (String)map.get("jugador");
		final Player player = entityStash.getPlayer(playerName);
		if (player != null && bet != null) {
			bet.setPlayer(playerName);
			bet.setLeague(player.getLeague());
		}
		if (bet != null) {
			final Account account = entityStash.getAccount();
			bet.setAccount(account.getName());
		}
		return bet;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createResponse(final LinkedHashMap response, final EntityStash entityStash, final Locale locale) {
		final Account account = entityStash.getAccount();
		final League defaultLeague = entityStash.getLeague(EntityStashAspect.DEFAULT_LEAGUE_NAME);
		// league info
		final String leagueInfo = this.entityStashViewService.getLeagueInfo(defaultLeague, locale);
		response.put(EntityStashAspect.DEFAULT_LEAGUE_INFO, leagueInfo);
		// league stats
		final String leagueStats = this.entityStashViewService.getLeagueStats(defaultLeague, locale);
		response.put(EntityStashAspect.DEFAULT_LEAGUE_STATS, leagueStats);
		// popular palyers
		final String leaguePopularPlayers = this.entityStashViewService.getLeaguePopularPlayers(defaultLeague, locale);
		response.put(EntityStashAspect.DEFAULT_LEAGUE_POPULAR_PLAYERS, leaguePopularPlayers);
		// alive players
		final List<Player> leaguePlayers = entityStash.getLeaguePlayers(defaultLeague);
		final String alivePlayers = this.entityStashViewService.getAlivePlayers(leaguePlayers);
		response.put(EntityStashAspect.DEFAULT_LEAGUE_ALIVE_PLAYERS, alivePlayers);
		// league bet restrictions
		final String leagueBetRestrictions = this.entityStashViewService.getLeagueBetRestrictions(defaultLeague, locale);
		response.put(EntityStashAspect.DEFAULT_LEAGUE_BET_RESTRICTIONS, leagueBetRestrictions);
		// account bet restrictions
		final String accountBetRestrictions = this.entityStashViewService.getAccountBetRestrictions(account, locale);
		response.put(EntityStashAspect.ACCOUNT_BET_RESTRICTIONS, accountBetRestrictions);
		// account info
		final String accountInfo = this.entityStashViewService.getAccountInfo(account, locale);
		response.put(EntityStashAspect.ACCOUNT_INFO, accountInfo);
		// account bets
		final String accountBets = this.entityStashViewService.getAccountBets(account, entityStash, locale);
		response.put(EntityStashAspect.ACCOUNT_BETS, accountBets);
	}
	
	
}
