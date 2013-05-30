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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Base bet model
 * @author Llop
 */
@Document
public class Bet {
	
	@Id private ObjectId id;
	
	@Indexed private String type;
	@Indexed private String status;

	@Indexed private String account;
	@Indexed private String player;
	@Indexed private String league;
	
	private Double kudos;
	private Double balance;
	
	
	public Bet() {
		super();
		this.id = new ObjectId();
	}
	
	public Bet(final ObjectId id) {
		super();
		this.id = id;
	}
	
	
	public ObjectId getId() {
		return id;
	}
	
	public void setId(final ObjectId id) {
		this.id = id;
	}
	
	public String getStatus() {
		return status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setStatus(final String status) {
		this.status = status;
	}
	
	public String getAccount() {
		return account;
	}
	
	public void setAccount(final String account) {
		this.account = account;
	}
	
	public String getPlayer() {
		return player;
	}
	
	public void setPlayer(final String player) {
		this.player = player;
	}
	
	public String getLeague() {
		return league;
	}
	
	public void setLeague(final String league) {
		this.league = league;
	}
	
	public Double getKudos() {
		return kudos;
	}
	
	public void setKudos(final Double kudos) {
		this.kudos = kudos;
	}
	
	public Double getBalance() {
		return balance;
	}
	
	public void setBalance(final Double balance) {
		this.balance = balance;
	}
	
	
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof Bet)) return false;
		if (this == obj) return true;
		Bet bhs = (Bet)obj;
		return new EqualsBuilder().append(id, bhs.id).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}
	
}
