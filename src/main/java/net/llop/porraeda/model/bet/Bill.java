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

import java.util.ArrayList;
import java.util.List;


public class Bill {

	
	private Double kudos;
	private List<BaseBet> bets;
	private Integer activeBets;
	
	public Integer getActiveBets() {
		return activeBets;
	}

	public void setActiveBets(Integer activeBets) {
		this.activeBets = activeBets;
	}

	public Bill() {
		super();
		this.bets = new ArrayList<BaseBet>();
	}
	
	public Double getKudos() {
		return kudos;
	}
	public void setKudos(Double kudos) {
		this.kudos = kudos;
	}

	public List<BaseBet> getBets() {
		return bets;
	}
	public void setBets(List<BaseBet> bets) {
		this.bets = bets;
	}
	
	
}