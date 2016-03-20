package com.netflix.config.sources;

import com.unboundid.ldap.sdk.*;

import java.util.HashMap;
import java.util.Map;

public class LDAPConfigurationStrategy {
    private final RDN baseRDN;
    private final Attribute keyAttribute;
    private final Attribute valueAttribute;

    public LDAPConfigurationStrategy(String baseRDN, Attribute keyAttribute, Attribute valueAttribute) throws LDAPException {
        this.baseRDN = new RDN(baseRDN);
        this.keyAttribute = keyAttribute;
        this.valueAttribute = valueAttribute;
    }

    private SearchRequest searchRequest(String base) throws LDAPException {
        return new SearchRequest(base, SearchScope.ONE, "objectclass=*", keyAttribute.getName(), valueAttribute.getName());
    }

    public Map<String, Object> load(LDAPInterface ldapInterface) throws LDAPException {

        SearchResultEntry base = ldapInterface.searchForEntry(
                new SearchRequest("", SearchScope.SUB, baseRDN.toString(), null));
        if (base == null)
            throw new LDAPSearchException(ResultCode.NO_RESULTS_RETURNED, String.format("cannot find base DN: %s", baseRDN));
        Map<String, Object> map = new HashMap<String, Object>();
        SearchResult searchResult = ldapInterface.search(searchRequest(base.getDN()));
        for (SearchResultEntry entry : searchResult.getSearchEntries())
            map.put(entry.getAttributeValue(keyAttribute.getName()), entry.getAttributeValue(valueAttribute.getName()));

        return map;
    }
}
