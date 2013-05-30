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
package org.jutge.joc.porra.controller.mobile;

import javax.servlet.http.HttpServletRequest;

import org.jutge.joc.porra.entitystash.annotation.EntityStashManaged;
import org.jutge.joc.porra.exception.PorraException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Mobile home controller
 * @author Llop
 */
@Controller
public class MobileHomeController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@ExceptionHandler(PorraException.class) 
    public String exceptionHandler(PorraException exception, HttpServletRequest request) {
		this.logger.info("MobileHomeController.exceptionHandler");
		request.setAttribute("errorMessages", exception.getErrorMessages());
		return "/mobile/error";
    }
	
	@EntityStashManaged
	@RequestMapping(value="/mobil")
	public String home() {
		this.logger.info("MobileHomeController.home");
		return "/mobile/home";
	}
	

}
