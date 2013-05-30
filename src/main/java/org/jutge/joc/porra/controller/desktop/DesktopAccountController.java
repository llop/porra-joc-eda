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
package org.jutge.joc.porra.controller.desktop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jutge.joc.porra.entitystash.annotation.EntityStashManaged;
import org.jutge.joc.porra.entitystash.module.EntityStashEntityModule;
import org.jutge.joc.porra.entitystash.module.EntityStashViewModule;
import org.jutge.joc.porra.entitystash.utils.EntityStashUtils;
import org.jutge.joc.porra.exception.LoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Home controller
 * @author Llop
 */
@Controller
public class DesktopAccountController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	

	@ExceptionHandler(LoginException.class) 
    public String signupExceptionHandler(final LoginException exception, final HttpServletRequest request) {
		this.logger.info("DesktopAccountController.signupExceptionHandler");
		request.setAttribute("usr", exception.getAccountName());
		request.setAttribute("errors", EntityStashUtils.toJsonString(exception.getErrorMessages()));
		return "desktop/account/login";
    }
	
	
	@EntityStashManaged
	@RequestMapping(value="/entrar", method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String login(final HttpSession session, final HttpServletRequest request) {
		this.logger.info("DesktopAccountController.login");
		this.validateLoginParams(session, request);
		return "/desktop/account/login";
	}
	
	private void validateLoginParams(final HttpSession session, final HttpServletRequest request) throws LoginException {
		final AuthenticationException authenticationException = (AuthenticationException)session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		if (authenticationException != null) {
			final Throwable cause = authenticationException.getCause();
			if (cause instanceof LoginException) {
				final LoginException loginException = (LoginException)cause;
				throw loginException;
			}
		}
	}
	
	
	@EntityStashManaged(entities=EntityStashEntityModule.ALL, views=EntityStashViewModule.ALL)
	@RequestMapping(value="/usuari", method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String accountDashboard() {
		this.logger.info("DesktopAccountController.accountDashboard");
		return "/desktop/account/dashboard";
	}
	
	
}
