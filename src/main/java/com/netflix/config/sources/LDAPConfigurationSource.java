package com.netflix.config.sources;

import com.netflix.config.PollResult;
import com.netflix.config.PolledConfigurationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LDAPConfigurationSource implements PolledConfigurationSource {

    private static Logger log = LoggerFactory.getLogger(LDAPConfigurationSource.class);
    private final LDAPPropertyTree ldapPropertyTree;

    public LDAPConfigurationSource(String ldapSearchSpecification) {
        this.ldapPropertyTree = new LDAPPropertyTree(ldapSearchSpecification);
    }

    public LDAPConfigurationSource(String ldapSearchSpecification, String bindDN, char[] password) {
        this.ldapPropertyTree = new LDAPPropertyTree(ldapSearchSpecification, bindDN, password);
    }

    @Override
    public PollResult poll(boolean initial, Object checkPoint) throws Exception {
        return PollResult.createFull(ldapPropertyTree.asMap());
    }

}
