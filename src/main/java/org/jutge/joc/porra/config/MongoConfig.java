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
package org.jutge.joc.porra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;


/**
 * Mongo beans.
 * @author Llop
 */
@Configuration
@EnableMongoRepositories
class MongoConfig extends AbstractMongoConfiguration {

	@Override protected String getDatabaseName() {
		return "XXXXXXXXX";
	}

	@Override public Mongo mongo() throws Exception {
		return new MongoClient("XXXXXX", 00000);
	}

	@Override protected String getMappingBasePackage() {
		return "org.jutge.joc.porra.repository";
	}
	
}
