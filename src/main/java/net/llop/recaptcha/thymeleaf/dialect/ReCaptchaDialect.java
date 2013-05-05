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
package net.llop.recaptcha.thymeleaf.dialect;

import java.util.HashSet;
import java.util.Set;

import net.llop.recaptcha.thymeleaf.processor.ReCaptchaAttrProcessor;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

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
