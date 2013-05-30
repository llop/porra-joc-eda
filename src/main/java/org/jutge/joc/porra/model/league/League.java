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
package org.jutge.joc.porra.model.league;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jutge.joc.porra.model.player.PopularPlayer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * League model
 * @author Llop
 */
@Document
public class League {
	
	@Id private String name;
	
	private Integer round;
	private Integer roundTemp;
	private Integer finalPlayers;

	private Integer alivePlayers;
	private Integer killedPlayers;
	
	private Integer betMakers;
	private Integer activeBets;
	private Integer betsWon;
	private Integer betsLost;
	private Integer finishedBets;

	private Double kudosWon;
	private Double kudosLost;
	private Double kudosAtStake;
	
	List<PopularPlayer> popularPlayers;

	
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Integer getFinalPlayers() {
		return finalPlayers;
	}

	public void setFinalPlayers(final Integer finalPlayers) {
		this.finalPlayers = finalPlayers;
	}

	public Integer getRound() {
		return round;
	}

	public void setRound(final Integer round) {
		this.round = round;
	}

	public Integer getRoundTemp() {
		return roundTemp;
	}

	public void setRoundTemp(final Integer roundTemp) {
		this.roundTemp = roundTemp;
	}

	public Integer getAlivePlayers() {
		return alivePlayers;
	}

	public void setAlivePlayers(final Integer alivePlayers) {
		this.alivePlayers = alivePlayers;
	}

	public Integer getKilledPlayers() {
		return killedPlayers;
	}

	public void setKilledPlayers(final Integer killedPlayers) {
		this.killedPlayers = killedPlayers;
	}

	public Integer getBetMakers() {
		return betMakers;
	}

	public void setBetMakers(final Integer betMakers) {
		this.betMakers = betMakers;
	}

	public Integer getActiveBets() {
		return activeBets;
	}

	public void setActiveBets(final Integer activeBets) {
		this.activeBets = activeBets;
	}

	public Integer getBetsWon() {
		return betsWon;
	}

	public void setBetsWon(final Integer betsWon) {
		this.betsWon = betsWon;
	}

	public Integer getBetsLost() {
		return betsLost;
	}

	public void setBetsLost(final Integer betsLost) {
		this.betsLost = betsLost;
	}

	public Integer getFinishedBets() {
		return finishedBets;
	}

	public void setFinishedBets(Integer finishedBets) {
		this.finishedBets = finishedBets;
	}

	public Double getKudosWon() {
		return kudosWon;
	}

	public void setKudosWon(final Double kudosWon) {
		this.kudosWon = kudosWon;
	}

	public Double getKudosLost() {
		return kudosLost;
	}

	public void setKudosLost(final Double kudosLost) {
		this.kudosLost = kudosLost;
	}

	public Double getKudosAtStake() {
		return kudosAtStake;
	}

	public void setKudosAtStake(final Double kudosAtStake) {
		this.kudosAtStake = kudosAtStake;
	}

	public List<PopularPlayer> getPopularPlayers() {
		return popularPlayers;
	}

	public void setPopularPlayers(final List<PopularPlayer> popularPlayers) {
		this.popularPlayers = popularPlayers;
	}
	
	public Boolean getOver() {
		return this.roundTemp == 93;
	}
	

	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof League)) return false;
		if (this == obj) return true;
		League phs = (League)obj;
		return new EqualsBuilder().append(name, phs.name).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(name).toHashCode();
	}
	
}
