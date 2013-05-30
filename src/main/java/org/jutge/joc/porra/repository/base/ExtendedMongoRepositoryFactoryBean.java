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
package org.jutge.joc.porra.repository.base;

import java.io.Serializable;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;


/**
 * Extended mongo repo factory bean
 * @author Llop
 */
public class ExtendedMongoRepositoryFactoryBean<T extends ExtendedMongoRepository<S, ID>, S, ID extends Serializable> extends MongoRepositoryFactoryBean<ExtendedMongoRepository<S, ID>, S, Serializable> {

	@Override protected RepositoryFactorySupport getFactoryInstance(final MongoOperations operations) {
		return new ExtendedMongoRepositoryFactory(operations);
	}
	
}
