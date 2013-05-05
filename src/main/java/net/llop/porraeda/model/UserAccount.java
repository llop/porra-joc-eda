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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.llop.porraeda.model.bet.Bill;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class UserAccount {

	@Id private ObjectId id;
	@Indexed(unique=true) private String username;
	private String password;
	@Transient private String passwordConfirm;
	private String salt;
	@Indexed(unique=true) private String email;
	private String activationToken;	private String status;
	private Date createdOn;
	private Boolean enabled;
	@DBRef private List<Role> roles = new ArrayList<Role>();
	private Bill bill;
	
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void addRole(Role role) {
		this.roles.add(role);
	}
	
	public String getActivationToken() {
		return activationToken;
	}

	public void setActivationToken(String activationToken) {
		this.activationToken = activationToken;
	}

	public void removeRole(Role role) {
		//use iterator to avoid java.util.ConcurrentModificationException with foreach
		Iterator<Role> iter = this.roles.iterator();
		while (iter.hasNext()) if (iter.next().equals(role)) iter.remove();
	}

	public String getRolesCSV() {
		StringBuilder sb = new StringBuilder();
		Iterator<Role> iter = this.roles.iterator();
		while (iter.hasNext()) {
			sb.append(iter.next().getId());
			if (iter.hasNext()) sb.append(',');
		}
		return sb.toString();
	}	

	public boolean equals(Object obj) {
		if (!(obj instanceof UserAccount)) return false;
		if (this == obj) return true;
		UserAccount rhs = (UserAccount)obj;
		return new EqualsBuilder().append(id, rhs.id).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(id).append(username).append(email).toHashCode();
	}
	
}