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


public interface IAssetsHost {
	
	// Implementations should return the url for a resource:
	//   - named 'resourceName' (ie. 'my-image.gif'),
	//   - that lives at the specified 'folder' (ie. 'my-images')
	// The last args are intended for provider-specific functionality
	String produceUrl(String resourceName, String folder, Object... args);
	
}
