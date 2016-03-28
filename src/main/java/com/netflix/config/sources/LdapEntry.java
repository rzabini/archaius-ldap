package com.netflix.config.sources;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.util.List;
import java.util.Map;

public class LDAPEntry {

    private final SearchResult entry;

    public LDAPEntry(SearchResult entryName) {
        this.entry = entryName;
    }

    public static String toPropertyName(String entryName) throws InvalidNameException {
        List<Rdn> rdns = new LdapName(entryName).getRdns();
        StringBuilder sb = new StringBuilder(rdns.get(0).getValue().toString());
        for (int i = 1; i < rdns.size(); ++i)
            sb.append(".").append(rdns.get(i).getValue().toString());
        return sb.toString();
    }

    void addToMap(Map<String, Object> map) throws NamingException {
        map.put(toPropertyName(entry.getName()), firstValueOfFirstAttribute(entry));
    }

    private Object firstValueOfFirstAttribute(SearchResult entry) throws NamingException {
        return entry.getAttributes().getAll().next().get();
    }
}
