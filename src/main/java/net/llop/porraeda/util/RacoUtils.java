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
package net.llop.porraeda.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


public class RacoUtils {
	
	public static final String ENV_RACO_USR = "RACO_USR";
	public static final String ENV_RACO_PASS = "RACO_PASS";
	public static final String RACO_USERNAME_PATTERN = "([a-z-]+)(\\.[a-z-]+)+";
	public static final String RACO_MAIL_ACTION_PATTERN = "compose\\.php\\?uniq=([a-z0-9]+)";
	public static final String RACO_FORMTOKEN_PATTERN = "__formToken_compose\" value=\"[a-z0-9]+";
	public static final String GRAU_EDA = "GRAU-EDA";
	public final static String RACO_MAIL = "@est.fib.upc.edu";
	
	private final static Pattern PATTERN = Pattern.compile("[a-z]+");
	
	public final static String normalitzaNom(String userName) {
		Matcher matcher = PATTERN.matcher(BetUtils.sanitizePlayerName(userName));
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) matcher.appendReplacement(buffer, StringUtils.capitalize(matcher.group()));
		matcher.appendTail(buffer);
		return buffer.toString();
	}
	
	public final static boolean validaNom(String userName) {
		if (StringUtils.isBlank(userName)) return false;
		return userName.matches(RacoUtils.RACO_USERNAME_PATTERN);
	}
	
	private RacoUtils() {}

}
