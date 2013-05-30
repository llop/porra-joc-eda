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
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

/**
 * Extended mongo repo that supports insert operations 
 * @author Llop
 */
public class ExtendedMongoRepositoryImpl<T, ID extends Serializable> extends SimpleMongoRepository<T, Serializable> implements ExtendedMongoRepository<T, Serializable> {

	private final MongoOperations mongoOperations;
	private final MongoEntityInformation<T, Serializable> entityInformation;
	
	public ExtendedMongoRepositoryImpl(final MongoEntityInformation<T, Serializable> metadata, final MongoOperations mongoOperations) {
		super(metadata, mongoOperations);
		this.entityInformation = metadata;
		this.mongoOperations = mongoOperations;
	}

	@Override public <S extends T> S insert(final S entity) {
		this.mongoOperations.insert(entity, this.entityInformation.getCollectionName());
		return entity;
	}

	@Override public <S extends T> List<S> insert(final Iterable<S> entities) {
		final List<S> result = new ArrayList<S>();
		for (S entity : entities) {
			this.save(entity);
			result.add(entity);
		}
		return result;
	}

}
