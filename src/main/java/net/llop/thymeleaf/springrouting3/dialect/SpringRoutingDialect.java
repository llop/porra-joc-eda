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
package net.llop.thymeleaf.springrouting3.dialect;

import java.util.HashMap;
import java.util.Map;

import net.llop.spring3.routing.SpringRouter;

import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.dialect.IExpressionEnhancingDialect;
import org.thymeleaf.spring3.dialect.SpringStandardDialect;

public class SpringRoutingDialect extends SpringStandardDialect implements IExpressionEnhancingDialect  {
	
	@Autowired private SpringRouter springRouter;

	@Override public Map<String, Object> getAdditionalExpressionObjects(IProcessingContext processingContext) {
		final Map<String,Object> objects = new HashMap<String, Object>();
        final IContext context = processingContext.getContext();
        final IWebContext webContext = (context instanceof IWebContext? (IWebContext)context : null);
        if (webContext != null) objects.put("r", this.springRouter);
		return objects;
	}

}
