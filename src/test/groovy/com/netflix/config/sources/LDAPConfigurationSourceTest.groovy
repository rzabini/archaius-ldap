package com.netflix.config.sources

import com.unboundid.ldap.sdk.Attribute
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.config.DynamicConfiguration;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.config.FixedDelayPollingScheduler


class LDAPConfigurationSourceTest extends LDAPSpecification {
    private static Logger log = LoggerFactory
            .getLogger(LDAPConfigurationSourceTest.class);


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

    def "can read configuration from LDAP node"() {

        given:
        LDAPConfigurationSource source = new LDAPConfigurationSource(testLDAPInterface,
                new LDAPConfigurationStrategy('cn=Configuration', new Attribute('cn'), new Attribute('description')))
        FixedDelayPollingScheduler scheduler = new FixedDelayPollingScheduler(0, 10, false)

        when:
        DynamicConfiguration configuration = new DynamicConfiguration(source, scheduler)
        DynamicPropertyFactory.initWithConfigurationSource(configuration)


        DynamicStringProperty defaultProp = DynamicPropertyFactory.getInstance().getStringProperty(
                "this.prop.does.not.exist.use.default", "default");

        then:
        defaultProp.get() == "default"

        when:
        DynamicStringProperty prop1 = DynamicPropertyFactory.getInstance().getStringProperty(
                "prop1", "default");
        then:
        prop1.get() == "value1"


    }
}
