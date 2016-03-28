package test.com.netflix.config.sources.ldap

import com.unboundid.ldap.listener.InMemoryDirectoryServer
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig
import com.unboundid.ldif.LDIFReader
import spock.lang.Shared
import spock.lang.Specification

abstract class LDAPSpecification extends Specification {

    @Shared
    InMemoryDirectoryServer ds


    def setupSpec() {
        InMemoryDirectoryServerConfig config =  new InMemoryDirectoryServerConfig("dc=example,dc=com");
        ds = new InMemoryDirectoryServer(config);

        LDIFReader ldifReader = new LDIFReader(getResource('test-data.ldif'))

        ds.importFromLDIF(true, ldifReader);
        ds.startListening();

    }


    def cleanupSpec() {
        ds.shutDown(true);
    }


    InputStream getResource(String resource) {
        Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)
    }
}
