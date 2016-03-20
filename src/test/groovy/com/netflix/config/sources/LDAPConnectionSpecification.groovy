package com.netflix.config.sources

import com.unboundid.ldap.listener.InMemoryDirectoryServer
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig
import com.unboundid.ldap.sdk.LDAPConnection
import com.unboundid.ldap.sdk.SearchResultEntry
import com.unboundid.ldif.LDIFReader
import spock.lang.Specification

class LDAPConnectionSpecification extends LDAPSpecification {

    def connect() {
        when:
        SearchResultEntry entry = testLDAPInterface.getEntry("dc=example,dc=com");

        then:
        1 == 1
    }


}
