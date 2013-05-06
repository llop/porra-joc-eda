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
package net.llop.thymeleaf.recaptcha.processor;

import net.llop.recaptcha.ReCaptchaUtils;
import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractUnescapedTextChildModifierAttrProcessor;

/**
 * Writes the recaptcha HTML code inside the containing element (a div in this example):
 * 
 * <div recaptcha:recaptcha=""></div>
 * @author Llop
 */
public class ReCaptchaAttrProcessor extends AbstractUnescapedTextChildModifierAttrProcessor {
	
	public final static String RECAPTCHA_ELEMENT_NAME = "recaptcha";

	public ReCaptchaAttrProcessor() {
		super(RECAPTCHA_ELEMENT_NAME);
	}

	public int getPrecedence() {
		return 12000;
	}

	@Override protected String getText(final Arguments arguments, final Element element, final String attributeName) {
		final String recaptchaPublicKey = System.getenv(ReCaptchaUtils.RECAPTCHA_PUBLIC_KEY);
		final String recaptchaPrivateKey = System.getenv(ReCaptchaUtils.RECAPTCHA_PRIVATE_KEY);
		final ReCaptcha captcha = ReCaptchaFactory.newReCaptcha(recaptchaPublicKey, recaptchaPrivateKey, false);
		return captcha.createRecaptchaHtml(null, null);
	}
	
}