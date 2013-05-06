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
package net.llop.porraeda.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.llop.porraeda.model.Role;
import net.llop.porraeda.model.UserAccount;
import net.llop.porraeda.model.UserAccountStatus;
import net.llop.porraeda.model.bet.BaseBet;
import net.llop.porraeda.model.bet.Bill;
import net.llop.porraeda.repository.RoleRepository;
import net.llop.porraeda.repository.UserAccountRepository;
import net.llop.porraeda.util.BetUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Does user account stuff like creating and updating -even deleting! 
 * @author Llop
 */
@Service
public class UserService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired private UserAccountRepository userRepository;
	@Autowired private RoleRepository roleRepository;

	public Role getRole(final String role) {
		this.logger.info("UserService.getRole");
		return this.roleRepository.findOne(role);
	}

	// Validation delegated to controller hmph!
	public boolean create(final UserAccount userAccount) {
		this.logger.info("UserService.create");
		Assert.isNull(userAccount.getId());
		// user.setId(UUID.randomUUID().toString().replace("-", ""));
		// duplicate email
		//if (this.userRepository.findByUsername(userAccount.getUsername()) != null) {
		//	return false;
		//}
		// Starting status 'pending no password' - User must activate account and provide own password (ie. by following emailed link)
		userAccount.setEnabled(false);
		userAccount.setStatus(UserAccountStatus.STATUS_PENDING_NOPASSWORD.name());
		userAccount.setCreatedOn(new Date());
		userAccount.addRole(new Role("ROLE_USER"));
		final String activationToken = BCrypt.gensalt(16).replace("/", "7").replace(".", "d");
		userAccount.setActivationToken(activationToken);
		final Bill bill = new Bill();
		bill.setKudos(BetUtils.INITIAL_CREDIT);
		bill.setBets(new ArrayList<BaseBet>());
		bill.setActiveBets(0);
		userAccount.setBill(bill);
		this.userRepository.save(userAccount);
		return true;
	}
	
	public boolean reset(final UserAccount userAccount) {
		this.logger.info("UserService.reset");
		// status goes back to pending no password. password and salt back to null and new activation token
		userAccount.setStatus(UserAccountStatus.STATUS_PENDING_NOPASSWORD.name());
		userAccount.setSalt(null);
		userAccount.setPassword(null);
		final String activationToken = BCrypt.gensalt(16).replace("/", "7").replace(".", "d");
		userAccount.setActivationToken(activationToken);
		this.userRepository.save(userAccount);
		return true;
	}
	
	public boolean activate(final UserAccount userAccount, final String password) {
		this.logger.info("UserService.activate");
		// Create random salt and store a hashed password
		final String salt = BCrypt.gensalt(16);
		final String hashedPassword = BCrypt.hashpw(password, salt);
		userAccount.setSalt(salt);
		userAccount.setPassword(hashedPassword);
		// status is now approved
		userAccount.setEnabled(true);
		userAccount.setStatus(UserAccountStatus.STATUS_APPROVED.name());
		this.userRepository.save(userAccount);
		return true;
	}
	
	public boolean validateKudos(final Integer kudos, final UserAccount user) {
		this.logger.info("UserService.validateKudos");
		return user.getBill().getKudos() < kudos;
	}
	
	public void expireUsers() {
		this.logger.info("UserService.expireUsers");
		final List<UserAccount> userAccounts = this.userRepository.findAll();
		final Iterator<UserAccount> iter = userAccounts.iterator();
		while (iter.hasNext()) {
			final UserAccount userAccount = iter.next();
			if (UserAccountStatus.STATUS_PENDING_NOPASSWORD.name().equals(userAccount.getStatus()) &&
					!userAccount.getEnabled() && this.hasExpired(userAccount.getCreatedOn())) {
				this.userRepository.delete(userAccount);
			}
		}
	}
	
	private boolean hasExpired(final Date date) {
		final Date expiresOn = new Date(date.getTime() + 21600000);
		return new Date().after(expiresOn);
	}

//	public void save(final UserAccount user) {
//		Assert.notNull(user.getId());
//		userRepository.save(user);
//	}
//
//	public void delete(UserAccount user) {
//		Assert.notNull(user.getId());
//		userRepository.delete(user);
//	}

	public UserAccount getByActivationToken(final String activationToken) {
		this.logger.info("UserService.getByActivationToken");
		return this.userRepository.findByActivationToken(activationToken.trim());
	}

	public UserAccount getByUsername(final String userName) {
		this.logger.info("UserService.getByUsername");
		return this.userRepository.findByUsername(userName.trim());
	}

	public UserAccount getByUsernameAndEmail(final String userName, final String email) {
		this.logger.info("UserService.getByUsernameAndEmail");
		return this.userRepository.findByUsernameAndEmail(userName.trim(), email.trim());
	}

	public UserAccount getByEmail(final String email) {
		this.logger.info("UserService.getByUsernameAndEmail");
		return this.userRepository.findByEmail(email.trim());
	}

}

