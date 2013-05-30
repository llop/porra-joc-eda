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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.jutge.joc.porra.entitystash.annotation.EntityStashManaged;
import org.jutge.joc.porra.entitystash.box.MessageBox;
import org.jutge.joc.porra.entitystash.utils.EntityStashUtils;
import org.jutge.joc.porra.exception.ActivationException;
import org.jutge.joc.porra.exception.PorraException;
import org.jutge.joc.porra.exception.ResetPassException;
import org.jutge.joc.porra.exception.SignupException;
import org.jutge.joc.porra.model.account.Account;
import org.jutge.joc.porra.model.account.AccountStatus;
import org.jutge.joc.porra.recaptcha.annotation.ReCaptchaConsumer;
import org.jutge.joc.porra.service.AccountService;
import org.jutge.joc.porra.service.EmailService;
import org.jutge.joc.porra.utils.ReCaptchaUtils;
import org.jutge.joc.porra.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Signup controller
 * @author Llop
 */
@Controller
public class DesktopSignupController {
	
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	

	@Autowired private AccountService accountService;
	@Autowired private EmailService emailService;
	
	@Autowired private MessageSource messageSource; 
	
	
	@ExceptionHandler(PorraException.class) 
    public String porraExceptionHandler(final PorraException exception, final HttpServletRequest request) {
		this.logger.info("DesktopSignupController.porraExceptionHandler");
		request.setAttribute("errors", EntityStashUtils.toJsonString(exception.getErrorMessages()));
		return "/desktop/error";
    }
	

	@ExceptionHandler(SignupException.class) 
    public String signupExceptionHandler(final SignupException exception, final HttpServletRequest request) {
		this.logger.info("DesktopSignupController.signupExceptionHandler");
		request.setAttribute("errors", EntityStashUtils.toJsonString(exception.getErrorMessages()));
		return "/desktop/signup/signup";
    }
	

	@EntityStashManaged
	@RequestMapping(value="/apuntar-se", method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String signupGet() {
		this.logger.info("DesktopSignupController.signupGet");
		return "/desktop/signup/signup";
	}
	
	
	@ReCaptchaConsumer
	@EntityStashManaged
	@RequestMapping(value="/apuntar-se", method=RequestMethod.POST, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces=MediaType.TEXT_HTML_VALUE)
	public String signupPost(@RequestBody final MultiValueMap<String, String> dataMap, final Locale locale, final HttpServletRequest request) {
		this.logger.info("DesktopSignupController.signupPost");
		// handle params
		final String name = dataMap.getFirst("usr");
		final String email = dataMap.getFirst("mil");
		request.setAttribute("usr", name);
		request.setAttribute("mil", email);
		final Boolean validRecaptcha = (Boolean)request.getAttribute(ReCaptchaUtils.RECAPTCHA_IS_VALID);
		this.validateSignupParams(name, email, validRecaptcha, locale);
		// try saving new account
		final Account account = new Account();
		account.setName(name.trim());
		account.setEmail(email.trim());
		this.accountService.createAccount(account);
		this.emailService.sendActivationMail(account);
		return "/desktop/signup/signup-done";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void validateSignupParams(final String name, final String email, final Boolean validRecaptcha, final Locale locale) throws SignupException {
		final List errorMessages = new ArrayList();
		if (validRecaptcha == null || !validRecaptcha) {
			final String message = this.messageSource.getMessage("exception.invalidRecaptcha", null, locale);
			final MessageBox messageBox = new MessageBox("invalidRecaptcha", message, new ArrayList<String>());
			errorMessages.add(messageBox);
		}
		if (!ValidationUtils.validateName(name)) {
			final String message = this.messageSource.getMessage("exception.invalidAccountName", null, locale);
			final MessageBox messageBox = new MessageBox("invalidAccountName", message, new ArrayList<String>());
			errorMessages.add(messageBox);
		}
		if (!ValidationUtils.validateEmail(email)) {
			final String message = this.messageSource.getMessage("exception.invalidAccountEmail", null, locale);
			final MessageBox messageBox = new MessageBox("invalidAccountEmail", message, new ArrayList<String>());
			errorMessages.add(messageBox);
		}
		if (!errorMessages.isEmpty()) {
			throw new SignupException(errorMessages);
		}
	}
	
	
	@ExceptionHandler(ActivationException.class) 
    public String activationExceptionHandler(final ActivationException exception, final HttpServletRequest request) {
		this.logger.info("DesktopSignupController.activationExceptionHandler");
		request.setAttribute("errors", EntityStashUtils.toJsonString(exception.getErrorMessages()));
		return "/desktop/signup/activate";
    }
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Account validateAccountToken(final String activationToken, final Locale locale) throws PorraException {
		final Account account = this.accountService.getByActivationToken(activationToken);
		if (account == null || !AccountStatus.STATUS_PENDING_NOPASSWORD.name().equals(account.getStatus())) {
			List errorMessages = new ArrayList();
			final String message = this.messageSource.getMessage("exception.userError", null, locale);
			final MessageBox messageBox = new MessageBox("userError", message, new ArrayList<String>());
			errorMessages.add(messageBox);
			throw new PorraException(errorMessages);
		}
		return account;
	}

	@EntityStashManaged
	@RequestMapping(value="/apuntar-se/activar/{activationToken}", method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String activateGet(@PathVariable final String activationToken, final Model model, final Locale locale) {
		this.logger.info("DesktopSignupController.activateGet");
		final Account account = this.validateAccountToken(activationToken, locale);
		model.addAttribute("userName", account.getName());
		model.addAttribute("activationToken", activationToken);
		return "/desktop/signup/activate";
	}
	

	
	private boolean validatePasswordParams(final String password, final String passwordConfirm) {
		return !(StringUtils.isBlank(password) || StringUtils.isBlank(passwordConfirm) || !password.trim().equals(passwordConfirm.trim()) || !ValidationUtils.validatePassword(password));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void validatePassword(final Account account, final String passwordConfirm, final Locale locale) throws ActivationException {
		if (!this.validatePasswordParams(account.getHashedPass(), passwordConfirm)) {
			List errorMessages = new ArrayList();
			final String message = this.messageSource.getMessage("exception.passwordError", null, locale);
			final MessageBox messageBox = new MessageBox("passwordError", message, new ArrayList<String>());
			errorMessages.add(messageBox);
			throw new ActivationException(errorMessages);
		}
	}

	@EntityStashManaged
	@RequestMapping(value="/apuntar-se/activar/{activationToken}", method=RequestMethod.POST, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces=MediaType.TEXT_HTML_VALUE)
	public String activatePost(@PathVariable final String activationToken, @RequestBody final MultiValueMap<String, String> dataMap, final HttpServletRequest request, final Locale locale) {
		this.logger.info("DesktopSignupController.activatePost");
		final Account account = this.validateAccountToken(activationToken, locale);
		request.setAttribute("userName", account.getName());
		request.setAttribute("activationToken", activationToken);
		// validate params
		final String password = dataMap.getFirst("pwd");
		final String passwordConfirm = dataMap.getFirst("pwd-con");
		account.setHashedPass(password);
		this.validatePassword(account, passwordConfirm, locale);
		this.accountService.approveAccount(account);
		return "/desktop/signup/activate-done";
	}
	
	
	@ExceptionHandler(ResetPassException.class) 
    public String resetPassExceptionHandler(final ResetPassException exception, final HttpServletRequest request) {
		this.logger.info("DesktopSignupController.resetPassExceptionHandler");
		request.setAttribute("errors", EntityStashUtils.toJsonString(exception.getErrorMessages()));
		return "/desktop/signup/reset";
    }

	@EntityStashManaged
	@RequestMapping(value="nova-clau", method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String resetPassGet() {
		this.logger.info("DesktopSignupController.resetPassGet");
		return "/desktop/signup/reset";
	}

	@ReCaptchaConsumer
	@EntityStashManaged
	@RequestMapping(value="/nova-clau", method=RequestMethod.POST, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces=MediaType.TEXT_HTML_VALUE)
	public String resetPassPost(@RequestBody final MultiValueMap<String, String> dataMap, final Model model, final HttpServletRequest request, final Locale locale) {
		this.logger.info("DesktopSignupController.resetPassPost");
		// validation first
		final String name = dataMap.getFirst("usr");
		final String email = dataMap.getFirst("mil");
		request.setAttribute("usr", name);
		request.setAttribute("mil", email);
		final Boolean recaptchaIsValid = (Boolean)request.getAttribute(ReCaptchaUtils.RECAPTCHA_IS_VALID);
		final Account account = validateResetPassParams(name, email, recaptchaIsValid, locale);
		// Do actual reset
		this.accountService.resetAccount(account);
		this.emailService.sendPasswordResetMail(account);
		return "/desktop/signup/reset-done";
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Account validateResetPassParams(final String name, final String email, final Boolean validRecaptcha, final Locale locale) throws ResetPassException {
		final List errorMessages = new ArrayList();
		if (validRecaptcha == null || !validRecaptcha) {
			final String message = this.messageSource.getMessage("exception.invalidRecaptcha", null, locale);
			final MessageBox messageBox = new MessageBox("invalidRecaptcha", message, new ArrayList<String>());
			errorMessages.add(messageBox);
		}
		final Account account = this.accountService.getByNameAndEmail(name.trim(), email.trim());
		if (account == null) {
			final String message = this.messageSource.getMessage("exception.wrongAccountNameAndPass", null, locale);
			final MessageBox messageBox = new MessageBox("wrongAccountNameAndPass", message, new ArrayList<String>());
			errorMessages.add(messageBox);
		}
		if (!errorMessages.isEmpty()) {
			throw new ResetPassException(errorMessages);
		}
		return account;
	}
	
	
}
