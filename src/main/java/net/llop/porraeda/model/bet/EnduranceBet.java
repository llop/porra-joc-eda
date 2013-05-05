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
package net.llop.porraeda.model.bet;


public class EnduranceBet extends BaseBet {

	
	private Integer betOnRound;
	private Integer enduresRounds;
	
	
	public Integer getBetOnRound() {
		return betOnRound;
	}
	
	public void setBetOnRound(Integer betOnRound) {
		this.betOnRound = betOnRound;
	}
	
	public Integer getEnduresRounds() {
		return enduresRounds;
	}
	
	public void setEnduresRounds(Integer enduresRounds) {
		this.enduresRounds = enduresRounds;
	}

	
}