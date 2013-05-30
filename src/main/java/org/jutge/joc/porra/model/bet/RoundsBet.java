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
package org.jutge.joc.porra.model.bet;

import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Rounds bet model
 * @author Llop
 */
@Document(collection="bet")
public class RoundsBet extends Bet {

	private Integer playersAlive;
	private Integer betOnRound;
	private Integer enduresRounds;
	
	
	public RoundsBet() {
		super();
		this.setType(BetType.ROUNDS.name());
	}
	

	public Integer getPlayersAlive() {
		return playersAlive;
	}

	public void setPlayersAlive(Integer playersAlive) {
		this.playersAlive = playersAlive;
	}

	public Integer getBetOnRound() {
		return betOnRound;
	}
	
	public void setBetOnRound(final Integer betOnRound) {
		this.betOnRound = betOnRound;
	}
	
	public Integer getEnduresRounds() {
		return enduresRounds;
	}
	
	public void setEnduresRounds(final Integer enduresRounds) {
		this.enduresRounds = enduresRounds;
	}
	
	
}
