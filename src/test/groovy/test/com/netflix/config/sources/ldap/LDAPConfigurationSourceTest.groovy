package test.com.netflix.config.sources.ldap

import com.netflix.config.*
import com.netflix.config.sources.ldap.LDAPConfigurationSource
import com.netflix.config.sources.ldap.LDAPConfigurationStrategy
import com.unboundid.ldap.sdk.Attribute
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LDAPConfigurationSourceTest extends LDAPSpecification {
    private static Logger log = LoggerFactory
            .getLogger(LDAPConfigurationSourceTest.class);


    def setupSpec() {
        LDAPConfigurationSource source = new LDAPConfigurationSource(testLDAPInterface,
                new LDAPConfigurationStrategy('cn=Configuration', new Attribute('cn'), new Attribute('description')))
        FixedDelayPollingScheduler scheduler = new FixedDelayPollingScheduler(0, 10, false)

        DynamicConfiguration configuration = new DynamicConfiguration(source, scheduler)
        ConfigurationManager.install(configuration);
    }

    def "can read configuration from LDAP node"(property, value) {
        when:
        DynamicStringProperty actualProperty = DynamicPropertyFactory.getInstance().getStringProperty(property, "default");

        then:
        actualProperty.get() == value

        where:
        property                |value
        "not.existing.prop"     |"default"
        "prop1"                 |"value1"
        "prop1.sub1"            |"value1-sub1"
        "prop1.sub2"            |"prop1-sub2"
    }


}
