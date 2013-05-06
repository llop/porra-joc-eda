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
package net.llop.porraeda.controller;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

import net.llop.porraeda.model.DaHouse;
import net.llop.porraeda.service.BetService;
import net.llop.porraeda.service.UserService;
import net.llop.porraeda.util.BetUtils;
import net.llop.porraeda.util.Routes;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles update requests -not available to users!!!
 * They should be invoked by a daemon thread that runs every so often.
 * @author Llop
 */
@Controller
@RequestMapping(Routes.UPDATE)
public class UpdateController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired private UserService userService;
	@Autowired private BetService betService;
	
	@RequestMapping(value=Routes.BETS, method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String updateBets(final Model model, final HttpServletRequest request) {
		this.logger.info("UpdateController.updateBets");
		try {
			// battle-royale.jutge.org has an untrusted cert
			final TrustStrategy easyStrategy = new TrustStrategy() { @Override public boolean isTrusted(final X509Certificate[] certificate, final String authType) throws CertificateException { return true; } };
		    final SSLSocketFactory socketFactory = new SSLSocketFactory(easyStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		    final SchemeRegistry registry = new SchemeRegistry();
		    registry.register(new Scheme("https", 443, socketFactory));
		    final ClientConnectionManager connectionManager = new PoolingClientConnectionManager(registry);
			// Get data
		    final HttpClient httpClient = new DefaultHttpClient(connectionManager);
			final HttpGet get = new HttpGet("https://battle-royale.jutge.org/info.php");
			final HttpResponse response = httpClient.execute(get);
			final HttpEntity entity = response.getEntity();
			final String responseBody = EntityUtils.toString(entity);
			final DaHouse daHouse = (DaHouse)request.getAttribute(BetUtils.DA_HOUSE);
			this.betService.updateDaHouse(responseBody, daHouse);
			model.addAttribute("result", "Update OK");
		} catch(Exception e) {
			this.logger.error(e.getMessage());
			model.addAttribute("result", "Update not OK");
		}
		return "updated";
	}
	
	@RequestMapping(value=Routes.USER, method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String updateUsers(final Model model, final HttpServletRequest request) {
		this.logger.info("UpdateController.updateUsers");
		this.userService.expireUsers();
		return "updated";
	}
}