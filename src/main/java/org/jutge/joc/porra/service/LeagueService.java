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

import java.util.List;

import org.jutge.joc.porra.model.league.League;
import org.jutge.joc.porra.repository.LeagueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Handles league-related logic
 * @author Llop
 */
@Service
public class LeagueService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired private LeagueRepository leagueRepo;
	
	
	//-----------------------
	// Queries
	//-----------------------
	
	public List<League> getAllLeagues() {
		this.logger.info("LeagueService.getAllLeagues");
		return this.leagueRepo.findAll();
	}
	
	
}
