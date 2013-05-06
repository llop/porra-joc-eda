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
package net.llop.porraeda.util;

/**
 * Springdata MongoDB to Java sometimes screws up object type since DB definition is "unusual"
 * @author Llop
 */
public class CastUtils {
	
	public final static Integer toInteger(Object obj) {
		if (obj instanceof Integer) return (Integer)obj;
		else if (obj instanceof Long) return ((Long)obj).intValue();
		else if (obj instanceof Short) return ((Short)obj).intValue();
		else if (obj instanceof Byte) return ((Byte)obj).intValue();
		else if (obj instanceof Double) return ((Double)obj).intValue();
		else if (obj instanceof Float) return ((Float)obj).intValue();
		else if (obj instanceof String) try { return Integer.parseInt((String)obj); } catch (Exception e) {}
		return null;
	}
	
	private CastUtils() {}

}
