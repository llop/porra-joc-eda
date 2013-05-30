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

import java.util.Collection;
import java.util.List;

import org.jutge.joc.porra.model.account.Account;
import org.jutge.joc.porra.model.league.League;
import org.jutge.joc.porra.model.player.Player;
import org.jutge.joc.porra.service.AccountService;
import org.jutge.joc.porra.service.LeagueService;
import org.jutge.joc.porra.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


/**
 * Intermediary data service class
 * @author Llop
 */
@Service
public class EntityStashEntityService {

	private static final GrantedAuthority USER_AUTHORITY = new SimpleGrantedAuthority("ROLE_USER");
	
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired private LeagueService leagueService;
	@Autowired private PlayerService playerService;
	@Autowired private AccountService accountService;
	
	
	//-----------------------
	// Queries
	//-----------------------
	
	public List<League> getLeagues() {
		this.logger.info("EntityStashEntityService.getLeagues");
		return this.leagueService.getAllLeagues();
	}
	
	public List<Player> getPlayers() {
		this.logger.info("EntityStashEntityService.getPlayers");
		return this.playerService.getAllPlayers();
	}
	
	public Account getAccount() {
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			final Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
			if (authorities != null && authorities.contains(USER_AUTHORITY)) {
				final String name = auth.getName();
				return this.accountService.getByName(name);
			}
		}
		return null;
	}
	
	
}
