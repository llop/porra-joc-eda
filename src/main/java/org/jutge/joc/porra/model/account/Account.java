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
package org.jutge.joc.porra.model.account;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jutge.joc.porra.model.bet.Bet;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Account model
 * @author Llop
 */
@Document
public class Account {
	
	@Id private String name;
	@Indexed(unique=true) private String email;
	
	@Indexed(unique=true) private String activationToken;
	@Indexed(direction=IndexDirection.DESCENDING) private Date createdOn;
	
	private String hashedPass;
	private String salt;
	
	private String status;
	private List<String> roles;
	
	private Double kudos;
	private Integer activeBets;
	
	@DBRef private List<Bet> bets;
	
	
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getActivationToken() {
		return activationToken;
	}

	public void setActivationToken(final String activationToken) {
		this.activationToken = activationToken;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(final Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getHashedPass() {
		return hashedPass;
	}

	public void setHashedPass(final String hashedPass) {
		this.hashedPass = hashedPass;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(final String salt) {
		this.salt = salt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(final List<String> roles) {
		this.roles = roles;
	}

	public void addRole(final String role) {
		this.roles.add(role);
	}

	public void removeRole(final String role) {
		final Iterator<String> iter = this.roles.iterator();
		while (iter.hasNext()) {
			if (iter.next().equals(role)) {
				iter.remove();
			}
		}
	}

	public String getRolesCSV() {
		final StringBuilder sb = new StringBuilder();
		final Iterator<String> iter = this.roles.iterator();
		while (iter.hasNext()) {
			sb.append(iter.next());
			if (iter.hasNext()) {
				sb.append(',');
			}
		}
		return sb.toString();
	}	

	public Double getKudos() {
		return kudos;
	}

	public void setKudos(final Double kudos) {
		this.kudos = kudos;
	}

	public Integer getActiveBets() {
		return activeBets;
	}

	public void setActiveBets(final Integer activeBets) {
		this.activeBets = activeBets;
	}

	public List<Bet> getBets() {
		return bets;
	}

	public void setBets(final List<Bet> bets) {
		this.bets = bets;
	}
	
		
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof Account)) return false;
		if (this == obj) return true;
		Account ahs = (Account)obj;
		return new EqualsBuilder().append(name, ahs.name).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(name).toHashCode();
	}
	
}
