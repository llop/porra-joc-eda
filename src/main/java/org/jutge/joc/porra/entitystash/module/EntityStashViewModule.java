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
package org.jutge.joc.porra.entitystash.module;


/**
 * Identifies which json strings to store in the request
 * @author Llop
 */
public enum EntityStashViewModule {
	LEAGUE_INFO, LEAGUE_STATS, LEAGUE_POPULAR_PLAYERS, LEAGUE_ALIVE_PLAYERS,
	ACCOUNT_INFO, ACCOUNT_BETS,
	LEAGUE_BET_RESTRICTIONS, ACCOUNT_BET_RESTRICTIONS, RICHEST_ACCOUNTS, 
	ALL, NONE;
}
