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

import net.llop.porraeda.util.Routes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles the login error request issued by springsecurity
 * @author Llop
 */
@Controller
@RequestMapping(Routes.LOGIN)
public class LoginController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/** Login form with error. */
	@RequestMapping(value=Routes.ERROR, method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String loginError(Model model) {
		logger.info("LoginController.loginError");
		model.addAttribute("loginError", true);
		return "login/login";
	}
	
}