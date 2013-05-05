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
package net.llop.porraeda.security;


import java.util.List;

import net.llop.porraeda.model.UserAccount;
import net.llop.porraeda.model.UserAccountStatus;
import net.llop.porraeda.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

/*
Extend AbstractUserDetailsAuthenticationProvider when you want to
prehandle authentication, as in throwing custom exception messages,
checking status, etc.
 */
@Component
public class MongoDBAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired private UserService userService;

	@Override protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {}

	@Override public UserDetails retrieveUser(final String username, final UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		this.logger.info("MongoDBAuthenticationProvider.retrieveUser");
		// Make sure an actual password was entered
		final String password = (String) authentication.getCredentials();
		if (!StringUtils.hasText(password)) {
			this.logger.warn("Username {}: no password provided", username);
			throw new BadCredentialsException("Please enter password");
		}
		// Look for user and check their account is activated but not disabled (perhaps to abuse? ...No policy regarding this as of now)
		final UserAccount user = userService.getByUsername(username);
		if (user == null) {
			this.logger.warn("Username {}: user not found", username);
			throw new BadCredentialsException("Invalid Username/Password");
		}
		if (!UserAccountStatus.STATUS_APPROVED.name().equals(user.getStatus())) {
			this.logger.warn("Username {}: not approved", username);
			throw new BadCredentialsException("User has not been approved");
		}
		if (!user.getEnabled()) {
			this.logger.warn("Username {}: disabled", username);
			throw new BadCredentialsException("User disabled");
		}
		// Check password
		final String hashedPassword = BCrypt.hashpw(password, user.getSalt());
		if (!hashedPassword.equals(user.getPassword())) {
			this.logger.warn("Username {}: bad password entered", username);
			throw new BadCredentialsException("Invalid Username/Password");
		}
		// Create Springframework-typed User instance
		final List<GrantedAuthority> auths = !user.getRoles().isEmpty() ? 
			AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRolesCSV()) :
			AuthorityUtils.NO_AUTHORITIES;
		// enabled, account not expired, credentials not expired, account not locked
		return new User(username, password, user.getEnabled(), true, true, true, auths);
	}

}