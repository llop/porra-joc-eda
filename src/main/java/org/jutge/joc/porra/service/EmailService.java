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
package org.jutge.joc.porra.service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jutge.joc.porra.model.account.Account;
import org.jutge.joc.porra.utils.RacoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * Sends emails via Raco webmail
 * @author Llop
 */
@Service
public class EmailService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	public void sendPasswordResetMail(final Account account) {
		this.logger.info("EmailService.sendPasswordResetMail");
		try {
			final String message = "Hola, " + account.getName() + "!\n\n" +
					"Accedeix a la seguent adressa per entrar un nou password: " +
					"http://porra.jutge.org/apuntar-se/activar/" + URLEncoder.encode(account.getActivationToken(), "UTF-8") + "\n\n" + 
					"Salutacions, equip porra-joc-eda";
			this.sendMail(account, message);
		} catch (Exception e) { this.logger.error(e.getMessage()); }
	}
	
	public void sendActivationMail(final Account account) {
		this.logger.info("EmailService.sendActivationMail");
		try {
			final String message = "Hola, benvingut a la porra del Joc!\n\n" +
				"El teu nom d'usuari es: " + account.getName() + "\n" + 
				"Introdueix un password per activar el teu usuari en aquesta adressa: " +
				"http://porra.jutge.org/apuntar-se/activar/" + URLEncoder.encode(account.getActivationToken(), "UTF-8") + "\n\n" + 
				"Salutacions, equip porra-joc-eda";
			this.sendMail(account, message);
		} catch (Exception e) { this.logger.error(e.getMessage()); }
	}
	
	/**
	 * Mighty delicate a process it is to send an email through the Raco webmail frontend. Let me break it down:
	 * You need to log in as you'd normally do in the Raco. After a couple of redirects to the CAS server, you should be inside.
	 * Then you issue a GET for the mail compose form. The response contains a form token to be used in the actual multipart POST that should send the mail.
	 * The minute they change the login system or the webmail styles, this'll probably break down LOL: Let's hope they keep it this way til the Joc d'EDA is over
	 * @param userAccount user to send the mail to
	 * @param message the message body
	 * @throws Exception should anything crash
	 */
	private void sendMail(final Account userAccount, final String message) throws Exception {
		// Enviar mail pel Raco
		// Authenticate
		final List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("service", "https://raco.fib.upc.edu/oauth/gestio-api/api.jsp"));
		params.add(new BasicNameValuePair("loginDirecte", "true"));
		params.add(new BasicNameValuePair("username", "XXXXXXXX"));
		params.add(new BasicNameValuePair("password", "XXXXXXXX"));
		HttpPost post = new HttpPost("https://raco.fib.upc.edu/cas/login");
		post.setEntity(new UrlEncodedFormEntity(params));
		final DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.setRedirectStrategy(new DefaultRedirectStrategy() {                
	        public boolean isRedirected(final HttpRequest request, final HttpResponse response, final HttpContext context)  {
	            boolean isRedirect = false;
	            try { isRedirect = super.isRedirected(request, response, context); } 
	            catch (ProtocolException e) { EmailService.this.logger.error(e.getMessage()); }
	            if (!isRedirect) {
	                final int responseCode = response.getStatusLine().getStatusCode();
	                isRedirect = responseCode == 301 || responseCode == 302;
	            }
	            return isRedirect;
	        }
	    });
		HttpResponse response = httpClient.execute(post);
		HttpEntity entity = response.getEntity();
		EntityUtils.consumeQuietly(entity);
		// get form page
		final HttpGet get = new HttpGet("https://webmail.fib.upc.es/horde/imp/compose.php?thismailbox=INBOX&uniq=" + System.currentTimeMillis());
		response = httpClient.execute(get);
		entity = response.getEntity();
		String responseBody = EntityUtils.toString(entity);
		// Find form action with uniq parameter 
		Pattern pattern = Pattern.compile(RacoUtils.RACO_MAIL_ACTION_PATTERN);
		Matcher matcher = pattern.matcher(responseBody);
		matcher.find();
		final String action = matcher.group();
		// formtoken
		responseBody = responseBody.substring(matcher.end(), responseBody.length());
		pattern = Pattern.compile(RacoUtils.RACO_FORMTOKEN_PATTERN);
		matcher = pattern.matcher(responseBody);
		matcher.find();
		String formToken = matcher.group();
		formToken = formToken.substring(28, formToken.length());
		// to
		final String email = userAccount.getEmail();
		// Send mail - it's a multipart post
		final MultipartEntity multipart = new MultipartEntity();
		multipart.addPart("MAX_FILE_SIZE", new StringBody("1038090240"));
		multipart.addPart("actionID", new StringBody("send_message"));
		multipart.addPart("__formToken_compose", new StringBody(formToken));
		multipart.addPart("messageCache", new StringBody(""));
		multipart.addPart("spellcheck", new StringBody(""));
		multipart.addPart("page", new StringBody(""));
		multipart.addPart("start", new StringBody(""));
		multipart.addPart("thismailbox", new StringBody("INBOX"));
		multipart.addPart("attachmentAction", new StringBody(""));
		multipart.addPart("reloaded", new StringBody("1"));
		multipart.addPart("oldrtemode", new StringBody(""));
		multipart.addPart("rtemode", new StringBody("1"));
		multipart.addPart("last_identity", new StringBody("0"));
		multipart.addPart("from", new StringBody("porra-joc-eda@est.fib.upc.edu"));
		multipart.addPart("to", new StringBody(email));
		multipart.addPart("cc", new StringBody(""));
		multipart.addPart("bcc", new StringBody(""));
		multipart.addPart("subject", new StringBody("Activacio Porra Joc EDA"));
		multipart.addPart("charset", new StringBody("UTF-8"));
		multipart.addPart("save_sent_mail", new StringBody("on"));
		multipart.addPart("message", new StringBody(message));
		multipart.addPart("upload_1", new StringBody(""));
		multipart.addPart("upload_disposition_1", new StringBody("attachment"));
		multipart.addPart("save_attachments_select", new StringBody("0"));
		multipart.addPart("link_attachments", new StringBody("0"));
		post = new HttpPost("https://webmail.fib.upc.es/horde/imp/" + action);
		post.setEntity(multipart);
		response = httpClient.execute(post);
		EntityUtils.consumeQuietly(entity);
		this.logger.info("EmailService.sendMail done");
	}
	
}

