package test.com.netflix.config.sources.ldap

import com.netflix.config.DynamicConfiguration
import com.netflix.config.DynamicPropertyFactory
import com.netflix.config.DynamicStringProperty
import com.netflix.config.FixedDelayPollingScheduler
import com.netflix.config.sources.ldap.LDAPConfigurationSource
import com.netflix.config.sources.ldap.LDAPConfigurationStrategy
import com.unboundid.ldap.sdk.Attribute
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LDAPWrongConfigurationSourceTest extends LDAPSpecification {
    private static Logger log = LoggerFactory
            .getLogger(LDAPWrongConfigurationSourceTest.class);


    def "exception thrown if base dn not found"() {
        String nonExistingNode = 'cn=NoConfiguration'
        LDAPConfigurationSource source = new LDAPConfigurationSource(testLDAPInterface,
                new LDAPConfigurationStrategy(nonExistingNode, new Attribute('cn'), new Attribute('description')))
        FixedDelayPollingScheduler scheduler = new FixedDelayPollingScheduler(0, 10, false)

        when:
        DynamicConfiguration configuration = new DynamicConfiguration(source, scheduler)

        then:
        RuntimeException ex = thrown()
        ex.cause.message == "cannot find base DN: $nonExistingNode"

    }

}
