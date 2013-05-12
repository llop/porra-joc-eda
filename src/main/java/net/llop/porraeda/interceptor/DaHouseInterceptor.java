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
package net.llop.porraeda.interceptor;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.llop.porraeda.model.DaHouse;
import net.llop.porraeda.model.Stats;
import net.llop.porraeda.service.BetService;
import net.llop.porraeda.util.BetUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Bolts daHouse in the request so it can be accessed everywhere without having to re-query DB
 * @author Llop
 */
public class DaHouseInterceptor extends HandlerInterceptorAdapter  {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired private BetService betService;
	
	@Override public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
		this.logger.info("DaHouseInterceptor.preHandle");
		final DaHouse daHouse = this.betService.getDaMastaHouse();
		final Stats stats = this.betService.getDaMastaHouseStats();
		final List<String> newsUpdates = this.betService.getNewsUpdates(stats);
		request.setAttribute(BetUtils.DA_HOUSE, daHouse);
		request.setAttribute(BetUtils.DA_HOUSE_STATS, stats);
		request.setAttribute(BetUtils.NEWS_UPDATES, newsUpdates);
		return true;
	}
	
	

}