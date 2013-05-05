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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.llop.porraeda.model.DaHouse;
import net.llop.porraeda.model.UserAccount;
import net.llop.porraeda.model.bet.BaseBet;
import net.llop.porraeda.model.bet.Bill;
import net.llop.porraeda.security.LoggedUserUtils;
import net.llop.porraeda.service.BetService;
import net.llop.porraeda.service.EmailService;
import net.llop.porraeda.service.UserService;
import net.llop.porraeda.util.BetUtils;
import net.llop.porraeda.util.RacoUtils;
import net.llop.porraeda.util.Routes;
import net.llop.porraeda.util.ValidationUtils;
import net.llop.recaptcha.ReCaptchaUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * @author Llop
 * 
 * Remember restful routing lessons:
 * 
 * Method		Path				Action
 * ------		----				------
 * GET			/things				Lists all existing things
 * GET			/things/new			Provides a form to create a new thing
 * POST			/things				Creates a new thing
 * GET			/things/{id}		Displays an existing thing
 * GET			/things/{id}/edit	Provides a form to edit an existing thing
 * PUT/PATCH	/things/{id}		Updates an existing thing
 * DELETE		/things/{id}		Deletes an existing thing
 */
@Controller
public class HomeController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired private UserService userService;
	@Autowired private BetService betService;
	@Autowired private EmailService emailService;

	@RequestMapping(value=Routes.ROOT, method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String welcome() {	
		this.logger.info("HomeController.welcome");
		return "home";
	}
	
	@RequestMapping(value=Routes.FAQ, method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String faq() {
		this.logger.info("HomeController.faq");
		return "faq";
	}
	
	@RequestMapping(value=Routes.CONTRIBUTE, method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String contribute() {
		this.logger.info("HomeController.contribute");
		return "contribute";
	}
	
	/**
	 * Signup
	 */
	@RequestMapping(value=Routes.SIGNUP, method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String signupGet() {
		this.logger.info("HomeController.signupGet");
		return "signup/signup";
	}
	
	@RequestMapping(value=Routes.SIGNUP, method=RequestMethod.POST, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces=MediaType.TEXT_HTML_VALUE)
	public String signupPost(@RequestBody final MultiValueMap<String, String> dataMap, final Model model, final HttpServletRequest request) {
		this.logger.info("HomeController.signupPost");
		// validation first
		final List<String> errors = new ArrayList<>();
		final String userName = dataMap.getFirst("usr");
		final String email = dataMap.getFirst("mil");
		final Boolean recaptchaIsValid = (Boolean)request.getAttribute(ReCaptchaUtils.RECAPTCHA_IS_VALID);
		if (recaptchaIsValid == null || !recaptchaIsValid) errors.add("Error en el captcha!");
		if (!ValidationUtils.validateName(userName)) errors.add("Error en el nom! Ha de tenir entre 4 i 16 caracters alfanumèrics");
		if (!ValidationUtils.validateEmail(email)) errors.add("Error en l'email! Comprova el format");
		if (this.userService.getByUsername(userName) != null) errors.add("Aquest nom ja està ocupat!");
		if (this.userService.getByEmail(email) != null) errors.add("Aquest email ja està ocupat!");
		if (!errors.isEmpty()) {
			model.addAttribute("errors", errors);
			return "signup/signup";
		}
		// Create the user in database and set status to pending
		final UserAccount userAccount = new UserAccount();
		userAccount.setUsername(userName.trim());
		userAccount.setEmail(email.trim());
		// user exists
		if (!this.userService.create(userAccount)) {
			model.addAttribute("errorMessage", "Revisa el teu mail del Racó!");
			return "error";
		}
		// Send activation mail
		this.emailService.sendActivationMail(userAccount);
		return "signup/done";
	}
	
	/**
	 * Password reset
	 */
	@RequestMapping(value=Routes.RESET_PASS, method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String resetPassGet() {
		this.logger.info("HomeController.resetPassGet");
		return "signup/reset";
	}
	
	@RequestMapping(value=Routes.RESET_PASS, method=RequestMethod.POST, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces=MediaType.TEXT_HTML_VALUE)
	public String resetPassPost(@RequestBody final MultiValueMap<String, String> dataMap, final Model model, final HttpServletRequest request) {
		this.logger.info("HomeController.resetPassPost");
		// validation first
		final List<String> errors = new ArrayList<>();
		final String userName = dataMap.getFirst("usr");
		final String email = dataMap.getFirst("mil");
		final Boolean recaptchaIsValid = (Boolean)request.getAttribute(ReCaptchaUtils.RECAPTCHA_IS_VALID);
		if (recaptchaIsValid == null || !recaptchaIsValid) errors.add("Error en el captcha!");
		final UserAccount userAccount = this.userService.getByUsernameAndEmail(userName == null ? "" : userName, email == null ? "" : email);
		if (userAccount == null) errors.add("Error amb la combinació usuari-email!");
		if (!errors.isEmpty()) {
			model.addAttribute("errors", errors);
			return "signup/reset";
		}
		// Do actual reset
		this.userService.reset(userAccount);
		this.emailService.sendPasswordResetMail(userAccount);
		model.addAttribute("userName", RacoUtils.normalitzaNom(userAccount.getUsername()));
		return "signup/reset-ok";
	}
	
	@RequestMapping(value=Routes.RESET_PASS_ERROR, method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String resetPassError(Model model) {
		logger.info("HomeController.resetPassError");
		model.addAttribute("errorMessage", "Error amb el captcha!");
		return "signup/reset";
	}
	
	@RequestMapping(value=Routes.USER, method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String userDashboard(final HttpServletRequest request, final Model model, final HttpServletRequest req) {
		this.logger.info("HomeController.userDashboard");
		final DaHouse daHouse = (DaHouse)req.getAttribute(BetUtils.DA_HOUSE);
		final List<String> alivePlayerNames = this.betService.getAlivePlayerNames(daHouse);
		final Integer currentRound = daHouse.getCurrentRound();
		final Integer maxEndureRounds = 100;  // TODO: update to currentRound - BetUtils.FINAL_PLAYERS;
		final UserAccount userAccount = (UserAccount)request.getAttribute(LoggedUserUtils.LOGGED_USER);
		final Bill bill = userAccount.getBill();
		final Integer kudosLeftInt = BetUtils.toInteger(bill.getKudos());
		final Iterator<BaseBet> iterator = bill.getBets().iterator(); 
		while (iterator.hasNext()) {
			BaseBet bet = iterator.next();
			if (bet.getActive()) {
				Double balance = this.betService.getCurrentBalance(bet, daHouse);
				bet.setBalance(BetUtils.formatDouble(balance));
			}
		}
		model.addAttribute("alivePlayerNames", alivePlayerNames);
		model.addAttribute("currentRound", currentRound);
		model.addAttribute("maxEndureRounds", maxEndureRounds);
		model.addAttribute("defaultEndureRounds", maxEndureRounds > BetUtils.SUGGESTED_MIN_ENDURE_ROUNDS ? BetUtils.SUGGESTED_MIN_ENDURE_ROUNDS : maxEndureRounds);
		model.addAttribute("kudosLeftInt", kudosLeftInt);
		model.addAttribute("defaultKudos", kudosLeftInt > BetUtils.SUGGESTED_MIN_BET ? BetUtils.SUGGESTED_MIN_BET : kudosLeftInt);
		return "user/dashboard";
	}
	
	@RequestMapping(value=Routes.LOGIN, method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String login() {
		this.logger.info("HomeController.login");
		return "login/login";
	}
	
}