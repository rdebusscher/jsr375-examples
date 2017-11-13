/*
 * Copyright 2017 Rudy De Busscher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.c4j.security.soteria.custom;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Typed;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.Collections;
import java.util.Set;

import static javax.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;
import static javax.security.enterprise.identitystore.CredentialValidationResult.NOT_VALIDATED_RESULT;
import static javax.security.enterprise.identitystore.IdentityStore.ValidationType.VALIDATE;

/**
 *
 */
@ApplicationScoped
@Typed  // Comment this when you want activate it.
public class BlackListedIdentityStore implements IdentityStore {

    @Override
    public CredentialValidationResult validate(Credential credential) {
        CredentialValidationResult result = NOT_VALIDATED_RESULT;
        if (credential instanceof UsernamePasswordCredential) {
            UsernamePasswordCredential usernamePassword = (UsernamePasswordCredential) credential;

            if ("rudy".equals(usernamePassword.getCaller())) {
                result = INVALID_RESULT;
            }
            return result;
        }
        return result;
    }

    @Override
    public int priority() {
        return 1000;
    }

    @Override
    public Set<ValidationType> validationTypes() {
        return Collections.singleton(VALIDATE);
    }
}
