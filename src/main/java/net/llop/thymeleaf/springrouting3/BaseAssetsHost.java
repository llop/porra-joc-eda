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
package net.llop.thymeleaf.springrouting3;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.llop.thymeleaf.springrouting3.IAssetsHost;

/**
 * Simple, base implementation of an assets host
 * @author Llop
 */
public class BaseAssetsHost implements IAssetsHost {
	
	private final static String URL_SEPARATOR = "/";
	
	private String rootUrl;
	private Map<String, String> folders;
	
	public BaseAssetsHost() {
		super();
	}

	public void setRootUrl(final String rootUrl) {
		// TODO: make sure it's not null
		// Ideally we should also validate it also looks like a url (ie. http:// at the start, etc.)
		this.rootUrl = rootUrl;
		if (!this.rootUrl.endsWith(URL_SEPARATOR)) this.rootUrl += URL_SEPARATOR;
	}

	public void setFolders(final Map<String, String> folders) {
		this.folders = folders;
	}
	
	// We don't use args
	// We silently fail if the instance is not set up properly
	@Override public String produceUrl(String resourceName, String folder, Object... args) {
		if (this.rootUrl != null && this.folders != null) {
			// TODO: make sure resourceName and folder are not blank
			String folderFragment = this.folders.get(folder);
			return StringUtils.isBlank(folderFragment) ? 
					this.rootUrl + resourceName : 
					this.rootUrl + folderFragment + URL_SEPARATOR + resourceName;
		}
		return "";
	}

}
