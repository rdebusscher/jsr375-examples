# jsr375-examples
Examples of Java EE 8 security API


## app-mem-basic

Basic example to get familiar with Http Mechanism, IdentityStore and the configuration by the CDI definition annotation.

## ldap-customform

More realistic scenario using a custom login form (Using PrimeFaces) and authentication/authorization against an external LDAP.

External LDAP is configured by the CDI definition annotation in the Servlet class.

Example LDAP entries can be found within _apacheDS_JCP.ldif_

## ldapform-jsf

Same as previous but now the protected resource is a JSF page, not a servlet.

Additional config is made within web.xml to define the protected and free accessible pages.

On GlassFish 5.0, there is an Internal Server Error with no explanation.

Therefor a EE7 profile is created in Maven so that the application runs on WildFly 10+
