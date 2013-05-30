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
package org.jutge.joc.porra.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jutge.joc.porra.entitystash.box.MessageBox;
import org.jutge.joc.porra.exception.SignupException;
import org.jutge.joc.porra.model.account.Account;
import org.jutge.joc.porra.model.account.AccountRole;
import org.jutge.joc.porra.model.account.AccountStatus;
import org.jutge.joc.porra.model.bet.Bet;
import org.jutge.joc.porra.repository.AccountRepository;
import org.jutge.joc.porra.utils.AccountUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;


/**
 * Handles account-related logic
 * @author Llop
 */
@Service
public class AccountService {
	
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	

	@Autowired private AccountRepository accountRepo;

	@Autowired private MessageSource messageSource;
	
	
	public List<Account> getApprovedAccounts() {
		return this.accountRepo.findByStatus(AccountStatus.STATUS_APPROVED.name());
	}
	
	//-----------------------
	// Account lifecycle
	//-----------------------
	
	/**
	 * Assume name and email are already set
	 * @param account
	 */
	public void createAccount(final Account account) throws SignupException {
		this.logger.info("AccountService.createAccount");
		// Starting status 'pending no password' - User must activate account and provide own password (ie. by following emailed link)
		final String activationToken = AccountUtils.generateActivationToken();
		account.setStatus(AccountStatus.STATUS_PENDING_NOPASSWORD.name());
		account.setRoles(new ArrayList<String>());
		account.addRole(AccountRole.ROLE_USER.name());
		account.setActivationToken(activationToken);
		account.setKudos(AccountUtils.INITIAL_KUDOS);
		account.setActiveBets(0);
		account.setBets(new ArrayList<Bet>());
		account.setCreatedOn(new Date());
		try {
			this.accountRepo.insert(account);
		} catch (final DuplicateKeyException exception) {
			this.handleDuplicatekeyException(exception);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void handleDuplicatekeyException(final DuplicateKeyException exception) throws SignupException {
		final Throwable cause = exception.getCause();
		if (cause instanceof MongoException) {
			final Locale locale = LocaleContextHolder.getLocale();
			final MongoException mongoException = (MongoException)cause;
			final String exceptionMessage = mongoException.getMessage();
			List errorMessages = new ArrayList();
			if (exceptionMessage.indexOf("account.$_id_") != -1) {
				final String message = this.messageSource.getMessage("exception.accountNameTaken", null, locale);
				final MessageBox messageBox = new MessageBox("accountNameTaken", message, new ArrayList<String>());
				errorMessages.add(messageBox);
			} else if (exceptionMessage.indexOf("account.$email") != -1) {
				final String message = this.messageSource.getMessage("exception.accountEmailTaken", null, locale);
				final MessageBox messageBox = new MessageBox("accountEmailTaken", message, new ArrayList<String>());
				errorMessages.add(messageBox);
			}
			throw new SignupException(errorMessages);
		} else {
			this.logger.error(exception.getMessage());
		}			
	}
	
	public void resetAccount(final Account account) {
		this.logger.info("AccountService.resetAccount");
		// status goes back to pending no password. password and salt back to null and new activation token
		final String activationToken = AccountUtils.generateActivationToken();
		account.setStatus(AccountStatus.STATUS_PENDING_NOPASSWORD.name());
		account.setSalt(null);
		account.setHashedPass(null);
		account.setActivationToken(activationToken);
		this.accountRepo.save(account);
	}
	
	/**
	 * Assume password has been set as plain text
	 * @param account
	 */
	public void approveAccount(final Account account) {
		this.logger.info("AccountService.approveAccount");
		// Create random salt and store a hashed password
		final String textPassword = account.getHashedPass();
		final String salt = BCrypt.gensalt(16);
		final String hashedPassword = BCrypt.hashpw(textPassword, salt);
		account.setSalt(salt);
		account.setHashedPass(hashedPassword);
		// status is now approved
		account.setStatus(AccountStatus.STATUS_APPROVED.name());
		this.accountRepo.save(account);
	}
	
	
	//-----------------------
	// Queries
	//-----------------------
	
	public Account getByActivationToken(final String activationToken) {
		this.logger.info("AccountService.getByActivationToken");
		return this.accountRepo.findByActivationToken(activationToken.trim());
	}

	public Account getByName(final String name) {
		this.logger.info("AccountService.getByName");
		return this.accountRepo.findOne(name.trim());
	}

	public Account getByNameAndEmail(final String name, final String email) {
		this.logger.info("AccountService.getByNameAndEmail");
		return this.accountRepo.findByNameAndEmail(name.trim(), email.trim());
	}
	
	
	//-----------------------
	// Expire away!
	//-----------------------
	
	/**
	 * Expires accounts that haven't been activated within 6 hours (or so) after creation
	 */
	public void expireAccounts() {
		this.logger.info("AccountService.expireAccounts");
		final List<Account> accounts = this.accountRepo.findAll();
		for (final Account account : accounts) {
			if (AccountUtils.shouldExpireAccount(account)) {
				this.accountRepo.delete(account);
			}
		}
	}
	
	
}
