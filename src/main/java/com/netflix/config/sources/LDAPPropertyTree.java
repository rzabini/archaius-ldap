package com.netflix.config.sources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class LDAPPropertyTree {

    private static Logger log = LoggerFactory.getLogger(LDAPPropertyTree.class);


    private final String ldapSearchSpecification;
    private final String bindDN;
    private final char[] password;

    LDAPPropertyTree(String ldapSearchSpecification) {
        this.ldapSearchSpecification = ldapSearchSpecification;
        this.bindDN = "";
        this.password = new char[]{};
    }

    LDAPPropertyTree(String ldapSearchSpecification, String bindDN, char[] password) {
        this.ldapSearchSpecification = ldapSearchSpecification;
        this.bindDN = bindDN;
        this.password = password;
    }

    public static String toPropertyName(String entryName) throws InvalidNameException {
        List<Rdn> rdns = new LdapName(entryName).getRdns();
        StringBuilder sb = new StringBuilder(rdns.get(0).getValue().toString());
        for (int i = 1; i < rdns.size(); ++i)
            sb.append(".").append(rdns.get(i).getValue().toString());
        return sb.toString();
    }

    Map<String, Object> asMap() throws NamingException {
        Map<String, Object> map = new HashMap<String, Object>();
        Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put(Context.SECURITY_PRINCIPAL, bindDN);
        env.put(Context.SECURITY_CREDENTIALS, password);

        NamingEnumeration<SearchResult> entries = new InitialDirContext(env).search(ldapSearchSpecification, "", null);

        while (entries.hasMore()) {
            SearchResult entry = entries.next();
            log.info(String.format("adding %s with value %s", entry.getName(), firstValueOfFirstAttribute(entry)));
            map.put(LDAPPropertyTree.toPropertyName(entry.getName()), firstValueOfFirstAttribute(entry));
        }
        return map;
    }

    private Object firstValueOfFirstAttribute(SearchResult entry) throws NamingException {
        return entry.getAttributes().getAll().next().get();
    }
}
