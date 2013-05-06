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
package net.llop.porraeda.repository;

import net.llop.porraeda.model.UserAccount;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * user account mongo repo. Used by springdata to create the proxy bean
 * @author Llop
 */
public interface UserAccountRepository extends MongoRepository<UserAccount, ObjectId> {
	
	UserAccount findByUsername(final String username);
	UserAccount findByUsernameAndEmail(final String username, final String email);
	UserAccount findByEmail(final String email);
	UserAccount findByActivationToken(final String activationToken);
	
}