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
package org.jutge.joc.porra.entitystash.box;

import java.io.Serializable;
import java.util.List;


/**
 * Message box POJO
 * @author Llop
 */

public class MessageBox implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	
	private String name;
	private String message;
	private List<String> args;
	
	
	public MessageBox(final String name, final String message, final List<String> args) {
		super();
		this.name = name;
		this.message = message;
		this.args = args;
	}
	

	public String getName() {
		return name;
	}

	public String getMessage() {
		return message;
	}

	public List<String> getArgs() {
		return args;
	}
	
	
}
