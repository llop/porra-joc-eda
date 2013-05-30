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
package org.jutge.joc.porra.controller.desktop;

import javax.servlet.http.HttpServletRequest;

import org.jutge.joc.porra.entitystash.annotation.EntityStashManaged;
import org.jutge.joc.porra.entitystash.module.EntityStashEntityModule;
import org.jutge.joc.porra.entitystash.module.EntityStashViewModule;
import org.jutge.joc.porra.exception.PorraException;
import org.jutge.joc.porra.service.BetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DesktopHomeController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired BetService betService;
	
	
	@ExceptionHandler(PorraException.class) 
    public String exceptionHandler(PorraException exception, HttpServletRequest request) {
		this.logger.info("DesktopHomeController.exceptionHandler");
		request.setAttribute("errorMessages", exception.getErrorMessages());
		return "/desktop/error";
    }
	
	@EntityStashManaged(entities=EntityStashEntityModule.ALL, views={ EntityStashViewModule.LEAGUE_INFO, EntityStashViewModule.LEAGUE_STATS, EntityStashViewModule.LEAGUE_POPULAR_PLAYERS, EntityStashViewModule.RICHEST_ACCOUNTS, EntityStashViewModule.ACCOUNT_INFO })
	@RequestMapping(value="/", method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String home() {
		this.logger.info("DesktopHomeController.home");
		return "/desktop/home";
	}
	
	@EntityStashManaged(entities=EntityStashEntityModule.ACCOUNT, views=EntityStashViewModule.ACCOUNT_INFO)
	@RequestMapping(value="/faq", method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String faq() {
		this.logger.info("DesktopHomeController.faq");
		return "/desktop/faq";
	}

	@EntityStashManaged(entities=EntityStashEntityModule.ACCOUNT, views=EntityStashViewModule.ACCOUNT_INFO)
	@RequestMapping(value="/contribueix", method=RequestMethod.GET, produces=MediaType.TEXT_HTML_VALUE)
	public String contribute() {
		this.logger.info("DesktopHomeController.contribute");
		return "/desktop/contribute";
	}
	
	
}
