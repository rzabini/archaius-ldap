package com.netflix.config.sources.ldap;

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
        return new SearchRequest(base, SearchScope.SUB, "objectclass=*", keyAttribute.getName(), valueAttribute.getName());
    }

    public Map<String, Object> load(LDAPInterface ldapInterface) throws LDAPException {

        SearchResultEntry base = getValidBase(ldapInterface);
        Map<String, Object> map = new HashMap<String, Object>();
        SearchResult searchResult = ldapInterface.search(searchRequest(base.getDN()));
        for (SearchResultEntry entry : searchResult.getSearchEntries()) {

            DN dn = entry.getParsedDN();
            RDN[] rdns = dn.getRDNs();
            int baseDnLength = base.getParsedDN().getRDNs().length;
            String[] components = new String[rdns.length - baseDnLength];
            if (components.length > 0) {
                int j = 0;
                for (int i = dn.getRDNStrings().length - baseDnLength; i > 0; i--)
                    components[j++] = rdns[i-1].getAttributeValues()[0];

                map.put(strJoin(components, "."), entry.getAttributeValue(valueAttribute.getName()));
            }

        }

        return map;
    }


    private static String strJoin(String[] aArr, String sSep) {
        StringBuilder sbStr = new StringBuilder();
        for (int i = 0, il = aArr.length; i < il; i++) {
            if (i > 0)
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
