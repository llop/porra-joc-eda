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
 * Constants to build routes
 * @author Llop
 */
public class Routes {
	
	public static final String ROOT = "/";
	public static final String ABOUT = "/sobre-la-web";
	public static final String FAQ = "/faq";
	public static final String CONTRIBUTE = "/contribueix";
	public static final String SIGNUP = "/apuntar-se";
	public static final String RESET_PASS = "/nova-clau";
	public static final String RESET_PASS_ERROR = "/nova-clau/error";
	public static final String SIGNUP_ACTIVATE = "/activar/{activationToken}";
	public static final String USER = "/usuari";
	public static final String MAKE_A_BET = "/fer-aposta";
	public static final String LOGIN = "/entrar";
	public static final String ERROR = "/error";
	public static final String BETS = "/apostes";
	public static final String ENDURANCE_BET = "/tipus/resistencia/kudos/{kudos}/jugador/{jugador}/rondes/{rondes}";
	public static final String BASE_BET = "/tipus/{tipus}/kudos/{kudos}/jugador/{jugador}";
	public static final String UPDATE = "/actualitzacions";
	
	private Routes() {}

}
