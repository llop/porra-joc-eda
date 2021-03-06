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
package org.jutge.joc.porra.model.bet;

import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Winner bet model
 * @author Llop
 */
@Document(collection="bet")
public class WinnerBet extends Bet {
	
	
	
	public WinnerBet() {
		super();
		this.setType(BetType.WINNER.name());
	}
	
	
	
}
