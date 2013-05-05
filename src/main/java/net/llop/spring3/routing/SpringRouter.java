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
package net.llop.spring3.routing;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import net.llop.thymeleaf.springrouting3.IAssetsHost;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;


public class SpringRouter implements ApplicationContextAware, ServletContextAware {

	private final static String CONTROLLER_SUFFIX = "Controller";
	private final static String PATTERN = "\\{.*?\\}";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ServletContext servletContext;
	private ApplicationContext applicationContext;
	private Map<RequestMappingInfo, HandlerMethod> handlerMethods;
	private Pattern pattern;
	
	@Autowired private IAssetsHost assetsHost;
	
	public SpringRouter() {
		super();
		this.pattern = Pattern.compile(PATTERN);
	}

	// args[0] is the resource name (ie. 'myimage.gif')
	// args[1] is the folder where the resource is stored
	public String s(Object... args) {
		// TODO: Make sure args has at least 1 non blank string
		
		try {
			if (this.assetsHost != null) {
				return this.assetsHost.produceUrl((String)args[0], args.length > 1 ? (String)args[1] : "");
			}
		} catch (Exception e) {}
		return "";
	}
	
	public String r(Object... args) {
		// TODO: make sure there's at least 1 non blank arg
		
		try {
			// First arg should be the controller
			String[] controllerNames = getControllerNames((String)args[0]);
			
			// 1 arg: only the controller is provided, so the first matching mapping is gonna get returned
			// Should be the case where 1 class maps 1 resource only
			if (args.length == 1) {
				// Try to find the appropriate mapping
				RequestMappingInfo mapping = findByControllerNames(controllerNames); 
				String route = mapping.getPatternsCondition().getPatterns().iterator().next();
				return produceCompleteRoute(route);
			} 
			
			// 2 args: controllerName and methodName - assume the mapping takes no params then
			// More than 2 args: each new arg represents a parameter
			else {
				// TODO: make sure args[1] is not blank
				RequestMappingInfo mapping = findByControllerNamesAndMethod(controllerNames, (String)args[1]);
				
				// If we found a matching mapping we can proceed
				if (mapping != null) {
					
					// get the url first
					String route = mapping.getPatternsCondition().getPatterns().iterator().next();
					
					// 2 args leaves here
					if (args.length == 2) return produceCompleteRoute(route);
					
					// Substitute pathvars and parameters
					else {
						int argsIndex = 2;
						// Params are gonna be stuffed in the following order:
						//   - Pathvars in the route go first, in the order they are found, from beginning to end of the string
						//   - Query params get to go then, also getting matched as they come
						
						// Pathvars
						// TODO: make sure you get pathvars in the mapping anyway
						StringBuffer buffer = new StringBuffer();
						argsIndex = replacePathVars(buffer, route, argsIndex, args);
						
						// Query params
						if (argsIndex < args.length) appendParams(buffer, route, mapping, argsIndex, args);
						
						return produceCompleteRoute(buffer.toString());
					}
				}
			}
		} catch (Exception e) {}
		// Nothing found
		return "";
	}
	
	// Follow this convention:
	//   - If controllerName does not end with Controller, append "Controller" at the end for the lookup.
	//     This allows programmer to be lazy and type just "home" instead of "homeController" for example
	private String[] getControllerNames(String controllerName) {
		return controllerName.endsWith(CONTROLLER_SUFFIX) ? new String[] { controllerName } : new String[] { controllerName + CONTROLLER_SUFFIX, controllerName };
	}
	
	private RequestMappingInfo findByControllerNames(String[] controllerNames) {
		final Map<RequestMappingInfo, HandlerMethod> mappings = this.getHandlerMethods();
		for (Entry<RequestMappingInfo, HandlerMethod> mappingEntry : mappings.entrySet()) {
			HandlerMethod mappingMethod = mappingEntry.getValue();
			String controllerBeanName = (String)mappingMethod.getBean();
			for (int i = 0; i < controllerNames.length; ++i) {
				if (controllerNames[i].equals(controllerBeanName)) return mappingEntry.getKey();
			}
		}
		return null;
	}
	
	private RequestMappingInfo findByControllerNamesAndMethod(String[] controllerNames, String methodName) {
		final Map<RequestMappingInfo, HandlerMethod> mappings = this.getHandlerMethods();
		for (Entry<RequestMappingInfo, HandlerMethod> mappingEntry : mappings.entrySet()) {
			HandlerMethod mappingMethod = mappingEntry.getValue();
			String controllerBeanName = (String)mappingMethod.getBean();
			String controllerBeanMethod = mappingMethod.getMethod().getName();
			if (methodName.equals(controllerBeanMethod)) {
				for (int i = 0; i < controllerNames.length; ++i) {
					if (controllerNames[i].equals(controllerBeanName)) {
						return mappingEntry.getKey();
					}
				}
			}
		}
		return null;
	}
	
	private String produceCompleteRoute(final String route) {
		return this.servletContext.getContextPath() + route;
	}
	
	private int replacePathVars(StringBuffer buffer, String route, int index, Object... args) {
		Matcher matcher = this.pattern.matcher(route);
		while (matcher.find()) {
			matcher.appendReplacement(buffer, this.format(args[index++]));
		}
		matcher.appendTail(buffer);
		return index;
	}
	
	private void appendParams(StringBuffer buffer, String route, RequestMappingInfo mapping, int index, Object... args) {
		// Add ? to url
		buffer.append('?');
		
		// TODO: MAke sure the mapping has params
		Set<NameValueExpression<String>> params = mapping.getParamsCondition().getExpressions();
		
		Iterator<NameValueExpression<String>> iter = params.iterator();
		buffer.append(iter.next().getName() + '=' + this.format(args[index++]));
		while (iter.hasNext()) {
			NameValueExpression<String> nameValueExpression = iter.next();
			buffer.append('&' + nameValueExpression.getName() + '=' + this.format(args[index++]));
		}
	}
	
	private String format(Object object) {
		String text = null;
		try { text = URLEncoder.encode(object.toString(), "UTF-8"); } 
		catch (UnsupportedEncodingException e) { logger.error(e.getMessage()); }
		return text;
	}
	
	private Map<RequestMappingInfo, HandlerMethod> getHandlerMethods() {
		if (this.handlerMethods == null) {
			synchronized (this) {
				handlerMethods = new HashMap<RequestMappingInfo, HandlerMethod>();
				// Find all HandlerMethods in the ApplicationContext, including ancestor contexts.
				Map<String, RequestMappingHandlerMapping> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(this.applicationContext, RequestMappingHandlerMapping.class, true, false);
				if (!matchingBeans.isEmpty()) {
					List<RequestMappingHandlerMapping> mappings = new ArrayList<RequestMappingHandlerMapping>(matchingBeans.values());
					for (RequestMappingHandlerMapping mapping : mappings) handlerMethods.putAll(mapping.getHandlerMethods());
				}
			}
		}
		return this.handlerMethods;
	}

	@Override public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override public void setServletContext(final ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
}
