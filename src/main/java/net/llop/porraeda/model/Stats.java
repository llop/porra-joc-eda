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

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A bunch of interesting calculated fields from da masta house.
 * @author Llop
 */
@Document
public class Stats {
	
	
	public static final Integer MAX_MOST_VOTED_PLAYERS = 10;
	
	
	@Id private String id;
	private Double totalKudosWon;
	private Double currentlyBetKudos;
	private Double averageKudosForBet;
	private Long totalActiveBets;
	private Integer totalWonBets;
	private Integer totalFinishedBets;
	private List<Pair<String, String>> mostVotedPlayers; // first: playername, second: popularity percentage

	
	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Double getTotalKudosWon() {
		return totalKudosWon;
	}

	public void setTotalKudosWon(final Double totalKudosWon) {
		this.totalKudosWon = totalKudosWon;
	}

	public Double getCurrentlyBetKudos() {
		return currentlyBetKudos;
	}

	public void setCurrentlyBetKudos(final Double currentlyBetKudos) {
		this.currentlyBetKudos = currentlyBetKudos;
	}

	public Double getAverageKudosForBet() {
		return averageKudosForBet;
	}

	public void setAverageKudosForBet(final Double averageKudosForBet) {
		this.averageKudosForBet = averageKudosForBet;
	}

	public Long getTotalActiveBets() {
		return totalActiveBets;
	}

	public void setTotalActiveBets(Long totalActiveBets) {
		this.totalActiveBets = totalActiveBets;
	}
	
	public Integer getTotalWonBets() {
		return totalWonBets;
	}

	public void setTotalWonBets(Integer totalWonBets) {
		this.totalWonBets = totalWonBets;
	}

	public Integer getTotalFinishedBets() {
		return totalFinishedBets;
	}

	public void setTotalFinishedBets(Integer totalFinishedBets) {
		this.totalFinishedBets = totalFinishedBets;
	}
	
	public List<Pair<String, String>> getMostVotedPlayers() {
		return mostVotedPlayers;
	}

	public void setMostVotedPlayers(List<Pair<String, String>> mostVotedPlayers) {
		this.mostVotedPlayers = mostVotedPlayers;
	}

	
	public boolean equals(final Object obj) {
		if (!(obj instanceof Stats)) return false;
		if (this == obj) return true;
		final Stats rhs = (Stats)obj;
		return new EqualsBuilder().append(id, rhs.id).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}
	
}