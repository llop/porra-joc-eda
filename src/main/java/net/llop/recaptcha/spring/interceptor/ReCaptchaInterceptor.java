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
package net.llop.recaptcha.spring.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.llop.porraeda.service.UserService;
import net.llop.recaptcha.ReCaptchaUtils;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Validates the captcha before the controller method's called. 
 * Will leave a boolean flag in the request with the validation's outcome
 * @author Llop
 */
public class ReCaptchaInterceptor extends HandlerInterceptorAdapter  {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired private UserService userService;
	
	@Override public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
		this.logger.info("ReCaptchaInterceptor.preHandle");
		// handle post
		if (RequestMethod.POST.name().equals(request.getMethod())) {
			String recaptchaResponse = request.getParameter(ReCaptchaUtils.RECAPTCHA_RESPONSE_FIELD);
			String recaptchaChallenge = request.getParameter(ReCaptchaUtils.RECAPTCHA_CHALLENGE_FIELD);
			String remoteAddr = request.getRemoteAddr();
			ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
			// reCaptcha.setPublicKey(RECAPTCHA_PUBLIC_KEY);
			reCaptcha.setPrivateKey(System.getenv(ReCaptchaUtils.RECAPTCHA_PRIVATE_KEY));
			ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, recaptchaChallenge, recaptchaResponse);
			request.setAttribute(ReCaptchaUtils.RECAPTCHA_IS_VALID, reCaptchaResponse.isValid());
		}
		return true;
	}
	
	

}