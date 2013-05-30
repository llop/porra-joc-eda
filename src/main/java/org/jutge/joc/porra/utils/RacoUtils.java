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
package org.jutge.joc.porra.utils;


/**
 * Raco utility methods + constants
 * @author Llop
 */
public class RacoUtils {
	
	
	//public static final String RACO_USERNAME_PATTERN = "([a-z-]+)(\\.[a-z-]+)+";
	public static final String RACO_MAIL_ACTION_PATTERN = "compose\\.php\\?uniq=([a-z0-9]+)";
	public static final String RACO_FORMTOKEN_PATTERN = "__formToken_compose\" value=\"[a-z0-9]+";
	
	
	public static String sanitizeUserName(final String userName) {
		if (userName != null) {
			return userName.replace('.', ' ');
		}
		return null;
	}

	public static String desanitizeUserName(final String userName) {
		if (userName != null) {
			return userName.replace(' ', '.');
		}
		return null;
	}
	
	
	private RacoUtils() {}

}
