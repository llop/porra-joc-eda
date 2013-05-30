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
package org.jutge.joc.porra.controller.base;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

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
import org.jutge.joc.porra.entitystash.annotation.EntityStashManaged;
import org.jutge.joc.porra.entitystash.module.EntityStashEntityModule;
import org.jutge.joc.porra.entitystash.module.EntityStashViewModule;
import org.jutge.joc.porra.entitystash.stash.EntityStash;
import org.jutge.joc.porra.service.AccountService;
import org.jutge.joc.porra.service.BetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Updates controller
 * @author Llop
 */
@Controller
@RequestMapping("/actualitzacions")
public class UpdateController {
	
	
	private final static String UPDATE_URL = "https://battle-royale.jutge.org/info.php";
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired private BetService betService;
	@Autowired private AccountService accountService;
	
	
	@EntityStashManaged(entities={ EntityStashEntityModule.LEAGUES, EntityStashEntityModule.PLAYERS }, views=EntityStashViewModule.NONE)
	@RequestMapping(value="/apostes", method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String updateBets(final Model model, final HttpServletRequest request, final EntityStash entityStash) {
		this.logger.info("UpdateController.updateBets");
		String jsonString = this.getUpdateJson();
		if (jsonString != null) {
			this.betService.updateEnduranceBets(jsonString, entityStash);
		}
		return "/desktop/update";
	}

	
	@RequestMapping(value="/usuari", method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String updateAccounts(final Model model, final HttpServletRequest request) {
		this.logger.info("UpdateController.updateAccounts");
		this.accountService.expireAccounts();
		return "/desktop/update";
	}
	
	private String getUpdateJson() {
		try {
			// battle-royale.jutge.org has an untrusted cert
			final TrustStrategy easyStrategy = new TrustStrategy() { @Override public boolean isTrusted(final X509Certificate[] certificate, final String authType) throws CertificateException { return true; } };
		    final SSLSocketFactory socketFactory = new SSLSocketFactory(easyStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		    final SchemeRegistry registry = new SchemeRegistry();
		    registry.register(new Scheme("https", 443, socketFactory));
		    final ClientConnectionManager connectionManager = new PoolingClientConnectionManager(registry);
			// Get data
		    final HttpClient httpClient = new DefaultHttpClient(connectionManager);
			final HttpGet get = new HttpGet(UPDATE_URL);
			final HttpResponse response = httpClient.execute(get);
			final HttpEntity entity = response.getEntity();
			final String responseBody = EntityUtils.toString(entity);
			return responseBody;
		} catch(Exception exception) {
			this.logger.error(exception.getMessage());
		}
		return null;
	}
	
	/*
	@RequestMapping(value="/init", method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String init(final Model model, final HttpServletRequest request) {
		this.logger.info("UpdateController.updateAccounts");
		this.betService.initDB(this.getUpdateJson());
		return "/desktop/update";
	}
	
	@EntityStashManaged(entities=EntityStashEntityModule.ALL, views=EntityStashViewModule.NONE)
	@RequestMapping(value="/migrate", method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String migrate(final EntityStash entityStash, final HttpServletRequest request) {
		this.logger.info("UpdateController.updateAccounts");
		try {
		final HttpClient httpClient = new DefaultHttpClient();
		final HttpGet get = new HttpGet("https://dl.dropboxusercontent.com/u/156484492/porraeda/j.txt");
		final HttpResponse response = httpClient.execute(get);
		final HttpEntity entity = response.getEntity();
		final String responseBody = EntityUtils.toString(entity);
		this.betService.migrate(responseBody, entityStash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/desktop/update";
	}
	*/
	
}
