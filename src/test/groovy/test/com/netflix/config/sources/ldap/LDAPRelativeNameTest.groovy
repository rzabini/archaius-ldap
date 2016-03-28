package test.com.netflix.config.sources.ldap

import com.netflix.config.sources.LDAPRelativeName
import spock.lang.Specification

class LDAPRelativeNameTest extends Specification {
    def "can convert ldap name to property name"(ldapName, propertyName) {

        expect:
        new LDAPRelativeName(ldapName).toPropertyName() == propertyName

        where:
        ldapName            | propertyName
        'cn=myname'         | 'myname'
        'cn=myname.sub'     | 'myname.sub'
        'cn=sub, ou=parent' | 'parent.sub'

    }
}
