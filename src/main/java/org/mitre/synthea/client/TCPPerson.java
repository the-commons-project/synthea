package org.mitre.synthea.client;

import com.google.gson.annotations.SerializedName;

public class TCPPerson {
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("gluu_id")
    private String gluuId;
    @SerializedName("first_name")
    private String firstNae;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGluuId() {
        return gluuId;
    }

    public void setGluuId(String gluuId) {
        this.gluuId = gluuId;
    }

    public String getFirstNae() {
        return firstNae;
    }

    public void setFirstNae(String firstNae) {
        this.firstNae = firstNae;
    }
}
