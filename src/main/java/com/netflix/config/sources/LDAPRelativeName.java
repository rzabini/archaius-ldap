package com.netflix.config.sources;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.util.List;

public class LDAPRelativeName {

    private final String entryName;

    public LDAPRelativeName(String entryName) {
        this.entryName = entryName;
    }

    public String toPropertyName() throws InvalidNameException {
        List<Rdn> rdns = new LdapName(entryName).getRdns();
        StringBuilder sb = new StringBuilder(rdns.get(0).getValue().toString());
        for (int i = 1; i < rdns.size(); ++i)
            sb.append(".").append(rdns.get(i).getValue().toString());
        return sb.toString();
    }
}
