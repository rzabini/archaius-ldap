package test.com.netflix.config.sources.ldap

import com.netflix.config.DynamicPropertyFactory
import com.netflix.config.DynamicStringProperty
import com.netflix.config.sources.LDAPConfigurationSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class LDAPConfigurationSourceSuccessSpecification extends LDAPBaseSpecification implements ConfigurationSourceInstaller {
    private static Logger log = LoggerFactory.getLogger(LDAPConfigurationSourceSuccessSpecification.class);

    abstract LDAPConfigurationSource getConfigurationSource()

    def "can read configuration from LDAP anonymous connection"(property, value) {

        when:
        installConfigurationSource(getConfigurationSource())
        DynamicStringProperty actualProperty = DynamicPropertyFactory.getInstance().getStringProperty(property, "default");

        then:
        actualProperty.get() == value

        where:
        property            | value
        "not.existing.prop" | "default"
        "prop1"             | "value1"
        "prop1.sub1"        | "value1-sub1"
        "prop1.sub2"        | "prop1-sub2"
    }


}
