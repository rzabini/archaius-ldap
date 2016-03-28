package test.com.netflix.config.sources.ldap

import com.netflix.config.*

trait ConfigurationSourceInstaller {

    void installConfigurationSource(PolledConfigurationSource source) {
        FixedDelayPollingScheduler scheduler = new FixedDelayPollingScheduler(0, 10, false)
        DynamicConfiguration configuration = new DynamicConfiguration(source, scheduler)
        ConfigurationManager.customConfigurationInstalled = false
        ConfigurationManager.instance = null
        DynamicPropertyFactory.config = null
        ConfigurationManager.install(configuration);
    }

}