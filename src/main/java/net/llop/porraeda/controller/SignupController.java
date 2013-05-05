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
package net.llop.porraeda.controller;

import net.llop.porraeda.model.UserAccount;
import net.llop.porraeda.model.UserAccountStatus;
import net.llop.porraeda.service.UserService;
import net.llop.porraeda.util.Routes;
import net.llop.porraeda.util.ValidationUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(Routes.SIGNUP)
public class SignupController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired private UserService userService;
	
	@RequestMapping(value=Routes.SIGNUP_ACTIVATE, method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String activateGet(@PathVariable final String activationToken, final Model model) {
		this.logger.info("SignupController.activateGet");
		// validate token
		final UserAccount userAccount = this.userService.getByActivationToken(activationToken);
		if (userAccount == null || !UserAccountStatus.STATUS_PENDING_NOPASSWORD.name().equals(userAccount.getStatus())) {
			model.addAttribute("errorMessage", "Error amb l'usuari!");
			return "error";
		}
		model.addAttribute("userName", userAccount.getUsername());
		return "signup/activate";
	}
	
	@RequestMapping(value=Routes.SIGNUP_ACTIVATE, method=RequestMethod.POST, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces=MediaType.TEXT_HTML_VALUE)
	public String activatePost(@PathVariable final String activationToken, @RequestBody final MultiValueMap<String, String> dataMap, final Model model) {
		this.logger.info("SignupController.activatePost");
		// validate token
		final UserAccount userAccount = this.userService.getByActivationToken(activationToken);
		if (userAccount == null || !UserAccountStatus.STATUS_PENDING_NOPASSWORD.name().equals(userAccount.getStatus())) {
			model.addAttribute("errorMessage", "Error amb l'usuari!");
			return "error";
		}
		model.addAttribute("userName", userAccount.getUsername());
		// validate params
		final String password = dataMap.getFirst("pwd");
		final String passwordConfirm = dataMap.getFirst("pwd-con");
		if (!this.validateParams(password, passwordConfirm)) {
			model.addAttribute("errorMessage", "Revisa el password o la confirmació (entre 4 i 16 caracters alfanumèrics)");
			return "signup/activate";
		}
		this.userService.activate(userAccount, password);
		return "signup/active";
	}
	
	private boolean validateParams(final String password, final String passwordConfirm) {
		return !(StringUtils.isBlank(password) || StringUtils.isBlank(passwordConfirm) || !password.trim().equals(passwordConfirm.trim()) || !ValidationUtils.validatePassword(password));
	}
	
}