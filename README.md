# jsr375-examples
Examples of Java EE 8 security API


## app-mem-basic

Basic example to get familiar with Http Mechanism, IdentityStore and the configuration by the CDI definition annotation.

Runs fine on GlassFish 5.0

## ldap-customform

More realistic scenario using a custom login form (Using PrimeFaces) and authentication/authorization against an external LDAP.

External LDAP is configured by the CDI definition annotation in the Servlet class.

Example LDAP entries can be found within _apacheDS_JCP.ldif_

Runs fine on GlassFish 5.0

## ldapform-jsf

Same as previous but now the protected resource is a JSF page, not a servlet.

Additional config is made within web.xml to define the protected and free accessible pages.

On GlassFish 5.0, there is an Internal Server Error with no explanation.

Therefor a EE7 profile is created in Maven so that the application runs on WildFly 10+.

## multiple-store

This is an example of custom implementations for HttpAuthenticationMechanism, IdentityStore and IdentityStoreHandler.

* TestAuthenticationMechanism -> Custom implementation which reds user name and password from query parameters.
* AuthorizationIdentityStore -> Custom implementation which only provide the groups. Authentication is performed by a store through CDI definition annotation
* CustomIdentityStoreHandler -> Custom implementation which loops over all IdentityStores doing authentication so implement a 'black list'. See also BlackListedIdentityStore and beans.xml

Runs fine on GlassFish 5.0

## jwt for JAX-RS endpoint

This example protects JAX-RS endpoints by JWT Bearer tokens.

Code and example can be found in the [Atbash JSR375 extensions repository](https://github.com/atbashEE/jsr375-extensions).

There is an issue on my machine with the JAX-RS endpoints on GlassFish 5.0. Therefor a EE7 profile is created in Maven so that the application runs on WildFly 10+.

## OAuth2 authentication for JSF application

POC for the usage of an OAuth2 provider (Google) for retrieving the end user information.

Code and example can be found in the [Atbash JSR375 extensions repository](https://github.com/atbashEE/jsr375-extensions).

There is an issue on GlassFish 5.0 with custom Principals (which needs to be allowed according to the JSR-375 spec). Therefor a EE7 profile is created in Maven so that the application runs on WildFly 10+.
