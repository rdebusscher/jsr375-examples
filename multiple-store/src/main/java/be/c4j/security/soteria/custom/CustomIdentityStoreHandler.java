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

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.spi.CDI;
import javax.interceptor.Interceptor;
import javax.security.enterprise.CallerPrincipal;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static javax.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;
import static javax.security.enterprise.identitystore.CredentialValidationResult.Status.VALID;
import static javax.security.enterprise.identitystore.IdentityStore.ValidationType.PROVIDE_GROUPS;
import static javax.security.enterprise.identitystore.IdentityStore.ValidationType.VALIDATE;

/**
 *
 */
@Alternative
@Priority(Interceptor.Priority.APPLICATION)
@ApplicationScoped
public class CustomIdentityStoreHandler implements IdentityStoreHandler {

    private List<IdentityStore> validatingIdentityStores;
    private List<IdentityStore> groupProvidingIdentityStores;

    @PostConstruct
    public void init() {
        List<IdentityStore> identityStores = getBeanReferencesByType();

        validatingIdentityStores = identityStores.stream()
                .filter(i -> i.validationTypes().contains(VALIDATE))
                .sorted(comparing(IdentityStore::priority))
                .collect(toList());

        groupProvidingIdentityStores = identityStores.stream()
                .filter(i -> i.validationTypes().contains(PROVIDE_GROUPS))
                .sorted(comparing(IdentityStore::priority))
                .collect(toList());
    }

    private List<IdentityStore> getBeanReferencesByType() {
        return CDI.current().select(IdentityStore.class).stream().collect(Collectors.toList());
    }

    @Override
    public CredentialValidationResult validate(Credential credential) {
        CredentialValidationResult validationResult = null;
        IdentityStore identityStore = null;

        // Check all stores and stop when one marks it as invalid.
        for (IdentityStore authenticationIdentityStore : validatingIdentityStores) {
            CredentialValidationResult temp = authenticationIdentityStore.validate(credential);
            switch (temp.getStatus()) {

                case NOT_VALIDATED:
                    // Don't do anything
                    break;
                case INVALID:
                    validationResult = temp;
                    break;
                case VALID:
                    validationResult = temp;
                    identityStore = authenticationIdentityStore;
                    break;
                default:
                    throw new IllegalArgumentException("Value not supported " + temp.getStatus());
            }
            if (validationResult != null && validationResult.getStatus() == CredentialValidationResult.Status.INVALID) {
                break;
            }
        }

        if (validationResult == null) {
            // No authentication store at all
            return INVALID_RESULT;
        }

        if (validationResult.getStatus() != VALID) {
            // No store validated (authenticated), no need to continue
            return validationResult;
        }

        CallerPrincipal callerPrincipal = validationResult.getCallerPrincipal();

        Set<String> groups = new HashSet<>();
        if (identityStore.validationTypes().contains(PROVIDE_GROUPS)) {
            groups.addAll(validationResult.getCallerGroups());
        }

        // Ask all stores that were configured for authorization to get the groups for the
        // authenticated caller
        for (IdentityStore authorizationIdentityStore : groupProvidingIdentityStores) {
            groups.addAll(authorizationIdentityStore.getCallerGroups(validationResult));
        }

        return new CredentialValidationResult(callerPrincipal, groups);

    }
}
