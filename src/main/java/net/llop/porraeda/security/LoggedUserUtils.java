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
package net.llop.porraeda.security;

/**
 * Constants used by logged user interceptor, theuy are ids for the injected request attributes
 * @author Llop
 */
public class LoggedUserUtils {
	
	public static final String LOGGED_USER = "loggedUser";
	public static final String USER_NAME_NORMALIZED = "userNameNormalized";
	public static final String USER_NAME = "userName";
	public static final String KUDOS = "kudos";
	public static final String ACTIVE_BETS = "activeBets";
	public static final String BETS = "bets";
	private LoggedUserUtils() {}

}
