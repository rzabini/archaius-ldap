package com.netflix.config.sources;

import com.netflix.config.PollResult;
import com.netflix.config.PolledConfigurationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;
import java.util.HashMap;
import java.util.Map;

public class LDAPConfigurationSource implements PolledConfigurationSource {

    private static Logger log = LoggerFactory.getLogger(LDAPConfigurationSource.class);
    private final String ldapSearchSpecification;

    public LDAPConfigurationSource(String ldapSearchSpecification) {
        this.ldapSearchSpecification = ldapSearchSpecification;
    }

    @Override
    public PollResult poll(boolean initial, Object checkPoint) throws Exception {
        Map<String, Object> map = load();
        return PollResult.createFull(map);
    }

    private Map<String, Object> load() throws NamingException {
        Map<String, Object> map = new HashMap<String, Object>();
        NamingEnumeration<SearchResult> entries = new InitialDirContext().search(ldapSearchSpecification, "", null);

        while (entries.hasMore())
            new LDAPEntry(entries.next()).addToMap(map);
        return map;
    }


}
