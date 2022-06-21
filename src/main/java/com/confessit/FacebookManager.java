package com.confessit;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.FacebookType;

public class FacebookManager {
    private String accessToken = "EAAFUjsLdaPsBAPot93rsER0qjef9HRZBC99f4c3QYColVOi4K4MNcOBwDFQana00mZCH8T9eVlDK9BSpG4cZB0xSRDr8dfcuO7SrOeLL6tJAEHiZAug3uNONwg4IytcIBOw7wQJkkeZCgBwj4RnoPgWhIGmDdZA16me30rUAA45u51xvv2FxXCMBsPGPAPxmwZD";
    private String pageID = "103632975733381";
    private FacebookClient fbClient;

    public FacebookManager() {} {
        this.fbClient = new DefaultFacebookClient(accessToken,  Version.VERSION_14_0);
    }

    private void extendAccessToken() {
        FacebookClient.AccessToken extendAccessToken = fbClient.obtainExtendedAccessToken("374447108090107", "72e53c85ac5e9ce402c489bd82e2e845");
        this.accessToken = extendAccessToken.getAccessToken();
        System.out.println("Extended Time: " + extendAccessToken.getExpires());
    }

    public void publishToFacebook(String content) {
        String postLink = pageID + "/feed";
        fbClient.publish(postLink, FacebookType.class, Parameter.with("message", content));
    }


}
