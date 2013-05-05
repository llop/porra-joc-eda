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
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.llop.porraeda.model.DaHouse;
import net.llop.porraeda.model.bet.BaseBet;
import net.llop.porraeda.model.bet.BetType;
import net.llop.porraeda.model.bet.EnduranceBet;
import net.llop.porraeda.service.BetService;
import net.llop.porraeda.service.UserService;
import net.llop.porraeda.util.BetUtils;
import net.llop.porraeda.util.Routes;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(Routes.BETS)
public class BetController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired private BetService betService;
	@Autowired private UserService userService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value=Routes.ENDURANCE_BET, method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody LinkedHashMap getEnduranceBetRate(@PathVariable final Integer kudos, @PathVariable final String jugador, @PathVariable final Integer rondes, final HttpServletRequest request) {
		logger.info("BetController.getEnduranceBetRate");
		final LinkedHashMap response = new LinkedHashMap();
		final DaHouse daHouse = (DaHouse)request.getAttribute(BetUtils.DA_HOUSE);
		if (this.betService.validatePlayerAlive(jugador, daHouse)) {
			final EnduranceBet bet = new EnduranceBet();
			bet.setBetOnRound(daHouse.getCurrentRound());
			bet.setEnduresRounds(rondes);
			bet.setKudos(kudos);
			bet.setPlayer(jugador);
			bet.setType(BetType.AGUANTA.name());
			final Double rate = this.betService.getRate(bet, daHouse);
			final String formattedeRate = rate == null || rate.isNaN() ? "?" : BetUtils.formatDouble(rate);
			response.put("rate", formattedeRate);
		} else this.addError("Jugador mort", response);
		return response;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value=Routes.BASE_BET, method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody LinkedHashMap getBaseBetRate(@PathVariable final String tipus, @PathVariable final Integer kudos, @PathVariable final String jugador, final HttpServletRequest request) {
		logger.info("BetController.getBaseBetRate");
		final LinkedHashMap response = new LinkedHashMap();
		final DaHouse daHouse = (DaHouse)request.getAttribute(BetUtils.DA_HOUSE);
		if (this.validateTipus(tipus) && this.betService.validatePlayerAlive(jugador, daHouse)) {
			final BaseBet bet = new BaseBet();
			bet.setKudos(kudos);
			bet.setPlayer(jugador);
			bet.setType(tipus);
			final Double rate = this.betService.getRate(bet, daHouse);
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