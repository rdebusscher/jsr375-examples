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
import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import javax.security.enterprise.identitystore.LdapIdentityStoreDefinition;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static javax.security.enterprise.identitystore.IdentityStore.ValidationType.PROVIDE_GROUPS;
import static javax.security.enterprise.identitystore.IdentityStore.ValidationType.VALIDATE;

/**
 *
 */
@LdapIdentityStoreDefinition(
        url = "ldap://localhost:33389/",
        callerBaseDn = "ou=caller,dc=jsr375,dc=net",
        useFor = VALIDATE
)
@ApplicationScoped
public class AuthorizationIdentityStore implements IdentityStore {

    private Map<String, Set<String>> authorization;

    @PostConstruct
    public void init() {
        authorization = new HashMap<>();

        authorization.put("rudy", new HashSet<>(asList("group1", "group2")));
        authorization.put("will", new HashSet<>(asList("group1", "group2", "group3")));
        authorization.put("arjan", new HashSet<>(asList("group1", "group3")));

    }

    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
        Set<String> result = authorization.get(validationResult.getCallerPrincipal().getName());
        if (result == null) {
            result = emptySet();
        }

        return result;
    }

    @Override
    public Set<ValidationType> validationTypes() {
        return Collections.singleton(PROVIDE_GROUPS);
    }
}
