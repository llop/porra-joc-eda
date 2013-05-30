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
package org.jutge.joc.porra.model.player;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Player model
 * @author Llop
 */
@Document
public class Player {
	
	@Id private String name;
	private String handle;
	
	@Indexed private String league;
	
	@Indexed private String status;
	private Integer roundKilled;
	
	private Integer betMakers;
	private Integer activeBets;
	
	private Boolean locked;
	
	
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(final String handle) {
		this.handle = handle;
	}

	public String getLeague() {
		return league;
	}

	public void setLeague(final String league) {
		this.league = league;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public Integer getRoundKilled() {
		return roundKilled;
	}

	public void setRoundKilled(final Integer roundKilled) {
		this.roundKilled = roundKilled;
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

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}
	
	
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof Player)) return false;
		if (this == obj) return true;
		Player phs = (Player)obj;
		return new EqualsBuilder().append(name, phs.name).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(name).toHashCode();
	}
	
}
