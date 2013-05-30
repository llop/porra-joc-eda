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
package org.jutge.joc.porra.recaptcha.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.jutge.joc.porra.recaptcha.annotation.ReCaptchaConsumer;
import org.jutge.joc.porra.utils.ReCaptchaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * Validates the captcha before the controller method's called. 
 * Will leave a boolean flag in the request with the validation's outcome
 * @author Llop
 */
public class ReCaptchaInterceptor extends HandlerInterceptorAdapter  {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	
	@Override public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
		this.logger.info("ReCaptchaInterceptor.preHandle");
		final ReCaptchaConsumer annotation = ReCaptchaInterceptor.getReCaptchaConsumerAnnotation(handler);
		if (annotation != null) {
			final String recaptchaResponse = request.getParameter(ReCaptchaUtils.RECAPTCHA_RESPONSE_FIELD);
			final String recaptchaChallenge = request.getParameter(ReCaptchaUtils.RECAPTCHA_CHALLENGE_FIELD);
			final String remoteAddr = request.getRemoteAddr();
			final ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
			reCaptcha.setPrivateKey("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			final ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, recaptchaChallenge, recaptchaResponse);
			request.setAttribute(ReCaptchaUtils.RECAPTCHA_IS_VALID, reCaptchaResponse.isValid());
		}
		return true;
	}
	

	
	private static ReCaptchaConsumer getReCaptchaConsumerAnnotation(final Object handler) {
		if (handler instanceof HandlerMethod) {
			final HandlerMethod handlerMethod = (HandlerMethod)handler;
			return handlerMethod.getMethodAnnotation(ReCaptchaConsumer.class);
		}
		return null;
	}

}