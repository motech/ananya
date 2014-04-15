package org.motechproject.ananya.support.admin.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@TypeDiscriminator("doc.type === 'AdminUser'")
public class AdminUser extends MotechBaseDataObject {

    @JsonProperty
    private String name;

    @JsonProperty
    private String password;

    public AdminUser() {
    }

    public AdminUser(String name, String password) {
        this.name = name;
        this.password = hash(password);
    }

    private String hash(String password) {
        String hashWord = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes());
            BigInteger hash = new BigInteger(1, md5.digest());
            hashWord = hash.toString(16);
        } catch (NoSuchAlgorithmException e) {
            // ignore
        }
        return hashWord;
    }

    public boolean passwordIs(String password) {
        return StringUtils.isNotBlank(password) && this.password.equals(hash(password));
    }

    public String getName() {
        return name;
    }
}
