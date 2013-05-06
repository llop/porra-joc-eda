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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.llop.porraeda.model.UserAccount;
import net.llop.porraeda.model.bet.Bill;
import net.llop.porraeda.service.UserService;
import net.llop.porraeda.util.BetUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * If user is authenticated, their userAccount bean and other useful stuff will be stuffed in the request
 * @author Llop
 */
public class LoggedUserInterceptor extends HandlerInterceptorAdapter  {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final GrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER");
	
	@Autowired private UserService userService;
	
	@Override public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
		this.logger.info("LoggedUserInterceptor.preHandle");
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth.getAuthorities().contains(this.userAuthority)) {
			final String name = auth.getName();
			final UserAccount userAccount = this.userService.getByUsername(name);
			final Bill bill = userAccount.getBill();
			request.setAttribute(LoggedUserUtils.LOGGED_USER, userAccount);
			//request.setAttribute(LoggedUserUtils.USER_NAME_NORMALIZED, RacoUtils.normalitzaNom(name));
			request.setAttribute(LoggedUserUtils.USER_NAME, name);
			request.setAttribute(LoggedUserUtils.KUDOS, BetUtils.formatDouble(bill.getKudos()));
			request.setAttribute(LoggedUserUtils.ACTIVE_BETS, bill.getActiveBets().toString());
			request.setAttribute(LoggedUserUtils.BETS, bill.getBets());
		}
		return true;
	}
	
	

}