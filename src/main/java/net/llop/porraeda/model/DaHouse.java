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
package net.llop.porraeda.model;

import java.util.Map;

import net.llop.porraeda.util.BetUtils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A bunch of calculated fields and stuff to keep state between all bets.
 * It should know about the round we're at currently.
 * And the players in the competition.
 * @author Llop
 */
@Document
public class DaHouse {
	
	public final static String PLAYER_ALIVE = "alive";
	public final static String HANDLE = "handle";
	public final static String BETS = "bets";
	public final static String ROUND_KILLED = "roundKilled";
	
	@Id private String id;
	private Double kudos;
	private Integer currentRound;
	private Integer contestants;
	private Integer totalPlayers;
	private Integer playersDown;
	private Integer betMakers;
	@SuppressWarnings("rawtypes") private Map<String, Map> players;
	// nom.usuari | alive (true - false) - Boolean
	//            | bets (# people has active bet on this player) - Integer
	// 			  | handle ('WaitForIt', 'Thor', ...) - String
	//			  | roundKilled (round when they got killed -zero if still alive) - Integer
	
	
	public Double getKudos() {
		return kudos;
	}

	public void setKudos(Double kudos) {
		this.kudos = kudos;
	}

	@SuppressWarnings("rawtypes") public Map<String, Map> getPlayers() {
		return players;
	}

	public void setPlayers(@SuppressWarnings("rawtypes") Map<String, Map> players) {
		this.players = players;
	}

	public Integer getTotalPlayers() {
		return totalPlayers;
	}
	
	public Integer getLastRound() {
		return this.getContestants() - BetUtils.FINAL_PLAYERS;
	}
	
	public Integer getPlayersLeft() {
		return this.getTotalPlayers() - this.getPlayersDown();
	}
	
	public Integer getCurrentRound() {
		return currentRound;
	}

	public void setCurrentRound(Integer currentRound) {
		this.currentRound = currentRound;
	}
	
	public Integer getContestants() {
		return contestants;
	}

	public void setContestants(Integer contestants) {
		this.contestants = contestants;
	}

	public void setTotalPlayers(Integer totalPlayers) {
		this.totalPlayers = totalPlayers;
	}

	public Integer getPlayersDown() {
		return playersDown;
	}

	public void setPlayersDown(Integer playersDown) {
		this.playersDown = playersDown;
	}

	public Integer getBetMakers() {
		return betMakers;
	}

	public void setBetMakers(Integer betMakers) {
		this.betMakers = betMakers;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof DaHouse)) return false;
		if (this == obj) return true;
		DaHouse rhs = (DaHouse)obj;
		return new EqualsBuilder().append(id, rhs.id).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}
	
}