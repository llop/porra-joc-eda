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
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;


/**
 * Extended mongo repo factory
 * @author Llop
 */
public class ExtendedMongoRepositoryFactory extends MongoRepositoryFactory {
	
	private final MongoOperations mongoOperations;
	
	public ExtendedMongoRepositoryFactory(final MongoOperations mongoOperations) {
		super(mongoOperations);
		this.mongoOperations = mongoOperations;
	}

	@Override protected Class<?> getRepositoryBaseClass(final RepositoryMetadata metadata) {
		return ExtendedMongoRepositoryImpl.class;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override protected Object getTargetRepository(final RepositoryMetadata metadata) {
		final MongoEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());
		return new ExtendedMongoRepositoryImpl(entityInformation, this.mongoOperations);
	}
	
}
