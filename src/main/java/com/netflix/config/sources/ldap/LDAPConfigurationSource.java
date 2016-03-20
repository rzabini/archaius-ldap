package com.netflix.config.sources.ldap;

import com.netflix.config.PollResult;
import com.netflix.config.PolledConfigurationSource;
import com.unboundid.ldap.sdk.LDAPInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LDAPConfigurationSource implements PolledConfigurationSource {
    private static Logger log = LoggerFactory.getLogger(LDAPConfigurationSource.class);
    private final LDAPInterface ldapInterface;
    private final LDAPConfigurationStrategy ldapConfigurationStrategy;

    public LDAPConfigurationSource(LDAPInterface ldapInterface, LDAPConfigurationStrategy ldapConfigurationStrategy) {
        this.ldapInterface = ldapInterface;
        this.ldapConfigurationStrategy = ldapConfigurationStrategy;
    }

    @Override
    public PollResult poll(boolean initial, Object checkPoint) throws Exception {
        Map<String, Object> map = ldapConfigurationStrategy.load(ldapInterface);
        return PollResult.createFull(map);
    }

}
