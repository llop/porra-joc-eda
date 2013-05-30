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
package org.jutge.joc.porra.repository;

import java.util.List;

import org.jutge.joc.porra.model.player.Player;
import org.jutge.joc.porra.repository.base.ExtendedMongoRepository;


/**
 * user account mongo repo. Used by springdata to create the proxy bean
 * @author Llop
 */
public interface PlayerRepository extends ExtendedMongoRepository<Player, String> {
	
	
	List<Player> findByLeague(final String league);
	List<Player> findByStatus(final String status);
	
	
}