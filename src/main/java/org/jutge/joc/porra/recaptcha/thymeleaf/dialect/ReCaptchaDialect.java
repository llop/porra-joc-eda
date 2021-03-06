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
package org.jutge.joc.porra.recaptcha.thymeleaf.dialect;

import java.util.HashSet;
import java.util.Set;

import org.jutge.joc.porra.recaptcha.thymeleaf.processor.ReCaptchaAttrProcessor;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;


/**
 * Allows recaptcha libs to feed HTML to thymeleaf.
 * You'd put the following in the thmeleaf template:
 * 
 * xmlns:recaptcha="http://net.llop.thymeleaf.recaptcha"
 * 
 * on the html tag to define the custom namespace.
 * Then this:
 * 
 * <div recaptcha:recaptcha=""></div>
 * 
 * will write the appropriate HTML code for the captcha
 * @author Llop
 */
public class ReCaptchaDialect extends AbstractDialect  {
	
	public final static String RECAPTCHA_DIALECT_PREFIX = "recaptcha";
	
	@Override public String getPrefix() {
		return RECAPTCHA_DIALECT_PREFIX;
	}

	@Override public boolean isLenient() {
		return false;
	}
	@Override public Set<IProcessor> getProcessors() {
		final Set<IProcessor> processors = new HashSet<IProcessor>();
		processors.add(new ReCaptchaAttrProcessor());
		return processors;
	}
	
}
