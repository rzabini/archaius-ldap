package com.netflix.config.sources.ldap;

import com.unboundid.ldap.sdk.*;

import java.util.HashMap;
import java.util.Map;

public class LDAPConfigurationStrategy {
    public static final String PROPERTY_SEPARATOR = ".";
    private final RDN baseRDN;
    private final Attribute keyAttribute;
    private final Attribute valueAttribute;

    public LDAPConfigurationStrategy(String baseRDN, Attribute keyAttribute, Attribute valueAttribute) throws LDAPException {
        this.baseRDN = new RDN(baseRDN);
        this.keyAttribute = keyAttribute;
        this.valueAttribute = valueAttribute;
    }

    private SearchRequest searchRequest(String base) throws LDAPException {
        return new SearchRequest(base, SearchScope.SUB, "objectclass=*", keyAttribute.getName(), valueAttribute.getName());
    }

    public Map<String, Object> load(LDAPInterface ldapInterface) throws LDAPException {

        SearchResultEntry base = getValidBase(ldapInterface);
        Map<String, Object> map = new HashMap<String, Object>();
        SearchResult searchResult = ldapInterface.search(searchRequest(base.getDN()));
        for (SearchResultEntry entry : searchResult.getSearchEntries()) {
            String[] components = relativeComponents(entry, base);
            if (components.length > 0)
                map.put(reverseStrJoin(components, PROPERTY_SEPARATOR), entry.getAttributeValue(valueAttribute.getName()));
        }

        return map;
    }

    private String[] relativeComponents(Entry entry, Entry base) throws LDAPException {
        RDN[] rdns = entry.getParsedDN().getRDNs();
        int baseDnLength = base.getParsedDN().getRDNs().length;
        String[] components = new String[rdns.length - baseDnLength];
        if (components.length > 0) {
            for (int i = 0; i < components.length; i++)
                components[i] = rdns[i].getAttributeValues()[0];
        }

        return components;

    }


    private static String reverseStrJoin(String[] aArr, String sSep) {
        StringBuilder sbStr = new StringBuilder();
        for (int i = aArr.length -1; i >= 0; i--) {
            if (i < aArr.length - 1)
                sbStr.append(sSep);
            sbStr.append(aArr[i]);
        }
        return sbStr.toString();
    }

    private SearchResultEntry getValidBase(LDAPInterface ldapInterface) throws LDAPException {
        SearchResultEntry base = ldapInterface.searchForEntry(
                new SearchRequest("", SearchScope.SUB, baseRDN.toString(), null));
        if (base == null)
            throw new LDAPSearchException(ResultCode.NO_RESULTS_RETURNED, String.format("cannot find base DN: %s", baseRDN));
        return base;
    }
}
