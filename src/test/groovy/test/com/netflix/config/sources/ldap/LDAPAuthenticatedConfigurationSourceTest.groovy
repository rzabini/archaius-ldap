package test.com.netflix.config.sources.ldap

import com.netflix.config.sources.LDAPConfigurationSource

class LDAPAuthenticatedConfigurationSourceTest extends LDAPConfigurationSourceSuccessSpecification {

    @Override
    LDAPConfigurationSource getConfigurationSource() {
        String ldapURL = "ldap://${ds.getConnection().getHostPort()}/cn=Configuration,dc=example,dc=com?description?sub?(description=*)".toString()
        new LDAPConfigurationSource(ldapURL, 'uid=admin,ou=People,dc=example,dc=com', 'password' as char[])
    }

}
