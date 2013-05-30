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
package org.jutge.joc.porra.recaptcha.thymeleaf.processor;

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
		final String recaptchaPublicKey = "6LceYuESAAAAAG-s2C1bzURTlpt6niMTMmnHTKuT";
		final String recaptchaPrivateKey = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
		final ReCaptcha captcha = ReCaptchaFactory.newReCaptcha(recaptchaPublicKey, recaptchaPrivateKey, false);
		return captcha.createRecaptchaHtml(null, null);
	}
	
}