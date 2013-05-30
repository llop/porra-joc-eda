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
package org.jutge.joc.porra.security;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jutge.joc.porra.entitystash.box.MessageBox;
import org.jutge.joc.porra.exception.LoginException;
import org.jutge.joc.porra.model.account.Account;
import org.jutge.joc.porra.model.account.AccountStatus;
import org.jutge.joc.porra.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Adapted from the springsecurity + mongodb example
 * 
 * Extend AbstractUserDetailsAuthenticationProvider when you want to prehandle authentication, as in throwing custom exception messages, checking status, etc.
 * @author Llop
 */
@Component
public class MongoDBAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
	

	private final Logger logger = LoggerFactory.getLogger(getClass());

	
	@Autowired private AccountService accountService;
	
	@Autowired private MessageSource messageSource; 
	

	@Override protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {}
	
	@Override public UserDetails retrieveUser(final String name, final UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		this.logger.info("MongoDBAuthenticationProvider.retrieveUser");
		boolean valid = true;
		// Make sure an actual password was entered
		final String password = (String)authentication.getCredentials();
		if (!StringUtils.hasText(password)) {
			this.logger.warn("Username {}: no password provided", name);
			valid = false;
		}
		// Look for user and check their account is activated
		final Account account = this.accountService.getByName(name);
		if (account == null) {
			this.logger.warn("Username {}: user not found", name);
			valid = false;
		} else {
			if (!AccountStatus.STATUS_APPROVED.name().equals(account.getStatus())) {
				this.logger.warn("Username {}: not approved", name);
				valid = false;
			}
			// Check password
			final String hashedPassword = BCrypt.hashpw(password, account.getSalt());
			if (!hashedPassword.equals(account.getHashedPass())) {
				this.logger.warn("Username {}: bad password entered", name);
				valid = false;
			}
		}
		if (!valid) {
			final Locale locale = LocaleContextHolder.getLocale();
			final String message = this.messageSource.getMessage("exception.wrongAccountNameAndPass", null, locale);
			final MessageBox messageBox = new MessageBox("wrongAccountNameAndPass", message, new ArrayList<String>());
			final List<MessageBox> errorMessages = new ArrayList<MessageBox>();
			errorMessages.add(messageBox);
			final LoginException loginException = new LoginException(errorMessages, name);
			throw new BadCredentialsException("Invalid Username/Password", loginException);
		}
		
		// Create Springframework-typed User instance
		final List<String> roles = account.getRoles();
		final List<GrantedAuthority> auths = !roles.isEmpty() ? AuthorityUtils.commaSeparatedStringToAuthorityList(account.getRolesCSV()) : AuthorityUtils.NO_AUTHORITIES;
		// enabled, account not expired, credentials not expired, account not locked
		return new User(name, password, true, true, true, true, auths);
	}
	
	
}