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


/**
 * Does format validation
 * @author Llop
 */
public class ValidationUtils {
	
	private final static String PASSWORD_PATTERN = "[a-zA-Z0-9]{4,16}";
	private final static String NAME_PATTERN = "[a-zA-Z_0-9 ]{4,16}";
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	public static final boolean validatePassword(String password) {
		return password == null ? false : password.trim().matches(PASSWORD_PATTERN);
	}

	public static final boolean validateName(String name) {
		return name == null ? false : name.trim().matches(NAME_PATTERN);
	}

	public static final boolean validateEmail(String email) {
		return email == null ? false : email.trim().matches(EMAIL_PATTERN);
	}

}
