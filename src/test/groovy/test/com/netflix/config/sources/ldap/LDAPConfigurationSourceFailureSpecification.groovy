package test.com.netflix.config.sources.ldap

import com.netflix.config.DynamicPropertyFactory
import com.netflix.config.DynamicStringProperty
import com.netflix.config.sources.LDAPConfigurationSource

class LDAPConfigurationSourceFailureSpecification extends LDAPBaseSpecification implements ConfigurationSourceInstaller {

    def "throws exception on LDAP error"(rootCN, password, errorMessage) {
        when:
        String ldapURL = "ldap://${ds.getConnection().getHostPort()}/cn=$rootCN,dc=example,dc=com?description?sub?(description=*)".toString()
        installConfigurationSource(
                new LDAPConfigurationSource(ldapURL, 'uid=admin,ou=People,dc=example,dc=com', password as char[])
        )
        DynamicStringProperty actualProperty = DynamicPropertyFactory.getInstance().getStringProperty('whatever', "default");

        then:
        Exception ex = thrown()
        ex.cause.message.startsWith(errorMessage)

        where:
        rootCN               | password         | errorMessage
        'Configuration'      | 'wrong password' | "[LDAP: error code 49 - Unable to bind"
        'WrongConfiguration' | 'password'       | "[LDAP: error code 32 - Unable to perform the search because base entry"
    }


}
