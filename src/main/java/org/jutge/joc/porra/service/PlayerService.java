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

import org.jutge.joc.porra.entitystash.stash.EntityStash;
import org.jutge.joc.porra.model.player.Player;
import org.jutge.joc.porra.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Handles player-related logic
 * @author Llop
 */
@Service
public class PlayerService {
	
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	
	@Autowired private PlayerRepository playerRepo;
	
	
	//-----------------------
	// Queries
	//-----------------------
	
	public List<Player> getAllPlayers() {
		this.logger.info("PlayerService.getAllPlayers");
		return this.playerRepo.findAll();
	}
	

	
	public void lockPlayers(final List<String> playerNames, final EntityStash entityStash) {
		this.logger.info("PlayerService.lockPlayers");
		this.handleLockPlayers(playerNames, entityStash, true);
	}
	
	public void unlockPlayers(final List<String> playerNames, final EntityStash entityStash) {
		this.logger.info("PlayerService.unlockPlayers");
		this.handleLockPlayers(playerNames, entityStash, false);
	}
	
	private void handleLockPlayers(final List<String> playerNames, final EntityStash entityStash, final boolean lock) {
		for (final String playerName : playerNames) {
			final Player player = entityStash.getPlayer(playerName);
			player.setLocked(lock);
			this.playerRepo.save(player);
		}
	}
	
	
}
