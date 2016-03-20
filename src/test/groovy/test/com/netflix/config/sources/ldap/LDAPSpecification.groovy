package test.com.netflix.config.sources.ldap

import com.netflix.config.ConfigurationManager
import com.netflix.config.DynamicPropertyFactory
import com.unboundid.ldap.listener.InMemoryDirectoryServer
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig
import com.unboundid.ldap.sdk.LDAPConnection
import com.unboundid.ldap.sdk.LDAPInterface
import com.unboundid.ldif.LDIFReader
import spock.lang.Shared
import spock.lang.Specification

import javax.naming.Context
import javax.naming.directory.DirContext
import javax.naming.directory.InitialDirContext

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

    LDAPInterface getTestLDAPInterface() {

        LDAPConnection conn = ds.getConnection();
        String[] hostAndPort = conn.getHostPort().split(':')
        new LDAPConnection(hostAndPort[0], Integer.parseInt(hostAndPort[1]),
                "uid=admin,ou=People,dc=example,dc=com", "password")

    }

    DirContext getTestLDAPContext() {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://${ds.getConnection().getHostPort()}/".toString());

        return new InitialDirContext(env);
    }

    def cleanupSpec() {
        ds.shutDown(true);
        resetConfigurationManager()
    }

    def resetConfigurationManager() {
        ConfigurationManager.customConfigurationInstalled = false
        ConfigurationManager.instance = null
        DynamicPropertyFactory.config = null
    }

    InputStream getResource(String resource) {
        Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)
    }
}
