package com.netflix.config.sources

import com.unboundid.ldap.listener.InMemoryDirectoryServer
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig
import com.unboundid.ldap.sdk.LDAPConnection
import com.unboundid.ldap.sdk.LDAPInterface
import com.unboundid.ldap.sdk.schema.Schema
import com.unboundid.ldif.LDIFReader
import spock.lang.Shared
import spock.lang.Specification

class LDAPSpecification extends Specification {

    @Shared
    InMemoryDirectoryServer ds

    def setupSpec() {
        InMemoryDirectoryServerConfig config =  new InMemoryDirectoryServerConfig("dc=example,dc=com");
        ds = new InMemoryDirectoryServer(config);

        LDIFReader ldifReader = new LDIFReader(getResource('test-data.ldif'))

        ds.importFromLDIF(true, ldifReader);
        ds.startListening();

    }

    LDAPInterface getTestLDAPInterface() {

        LDAPConnection conn = ds.getConnection();
        String[] hostAndPort = conn.getHostPort().split(':')
        new LDAPConnection(hostAndPort[0], Integer.parseInt(hostAndPort[1]),
                "uid=admin,ou=People,dc=example,dc=com", "password")

    }

    def cleanupSpec() {
        ds.shutDown(true);
    }

    InputStream getResource(String resource) {
        Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)
    }
}
