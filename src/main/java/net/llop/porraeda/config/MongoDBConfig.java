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
package net.llop.porraeda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.util.StringUtils;

import com.mongodb.Mongo;

@Configuration
public class MongoDBConfig {
	
	private static final String ENV_MONGODB_HOST = "OPENSHIFT_MONGODB_DB_HOST";
	private static final String ENV_MONGODB_PORT = "OPENSHIFT_MONGODB_DB_PORT";
	private static final String ENV_MONGODB_DATABASE = "OPENSHIFT_APP_NAME";
	private static final String ENV_MONGODB_USERNAME = "OPENSHIFT_MONGODB_DB_USERNAME";
	private static final String ENV_MONGODB_PASSWORD = "OPENSHIFT_MONGODB_DB_PASSWORD";

	public @Bean Mongo mongo() throws Exception {
		String host = System.getenv(ENV_MONGODB_HOST);
        int port = Integer.parseInt(System.getenv(ENV_MONGODB_PORT));
		return new Mongo(host, port);
	}

	public @Bean MongoDbFactory mongoDbFactory() throws Exception {
		Mongo mongo = this.mongo();
		String database = System.getenv(ENV_MONGODB_DATABASE);
		String username = System.getenv(ENV_MONGODB_USERNAME);
		String password = System.getenv(ENV_MONGODB_PASSWORD);
		MongoDbFactory factory = null;
		if (!StringUtils.isEmpty(username)) {
			UserCredentials userCredentials = new UserCredentials(username, password);
			factory = new SimpleMongoDbFactory(mongo, database, userCredentials);
		} else factory = new SimpleMongoDbFactory(mongo, database);
		return factory;
	}

	public @Bean MongoTemplate mongoTemplate() throws Exception {
		MongoDbFactory factory = this.mongoDbFactory();
		return new MongoTemplate(factory);
	}

}
