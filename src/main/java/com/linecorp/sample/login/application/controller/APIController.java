/*
 * Copyright 2016 LY Corporation
 *
 * LY Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.sample.login.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linecorp.sample.login.infra.line.api.v2.LineAPIService;
import com.linecorp.sample.login.infra.line.api.v2.response.AccessToken;
import com.linecorp.sample.login.infra.line.api.v2.response.IdToken;
import com.linecorp.sample.login.infra.line.api.v2.response.Verify;

import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = "*") // すべてのオリジンを許可（開発中のみ推奨）
public class APIController {

    @Autowired
    private LineAPIService lineAPIService;

    @RequestMapping("api/refreshToken")
    public AccessToken refreshToken(HttpSession httpSession) {
        final AccessToken token = getAccessToken(httpSession);
        final AccessToken newAccessToken = lineAPIService.refreshToken(token);
        if (newAccessToken != null) {
            setAccessToken(httpSession, newAccessToken);
        }
        return newAccessToken;
    }
    @GetMapping("/search/{name}")
    public IdToken getProductById(@PathVariable String name) {
    	System.out.println(name);
    	
    	return null;
    }
    @GetMapping("/verify")
    public IdToken getverify(@RequestHeader("AuthToken") String authHeader) {
    	System.out.println("authHeader:"+authHeader);
    	IdToken it = lineAPIService.idToken("eyJraWQiOiI3MTU5ZTNlYWUwZjdmMmQ4NjhmM2MwOWI2ZGU5MzBlYzMzNjNlYzA0NTI2ZjQwY2FlYzliMWYwOGUwZjQzY2E2IiwidHlwIjoiSldUIiwiYWxnIjoiRVMyNTYifQ.eyJpc3MiOiJodHRwczovL2FjY2Vzcy5saW5lLm1lIiwic3ViIjoiVWNhOWM0OTVlYzc2ODBjNTRjM2Y3NTI3YTU0M2JiMDVhIiwiYXVkIjoiMjAwNzU4NTc1MiIsImV4cCI6MTc1MTAwMDE1NiwiaWF0IjoxNzUwOTk2NTU2LCJuYW1lIjoi44Kq44KmIiwicGljdHVyZSI6Imh0dHBzOi8vcHJvZmlsZS5saW5lLXNjZG4ubmV0LzBoRVluWTBlRnlHblpUSUFSTXJhWmxJUzVsRkJza0Rody1LMFZUR0hJa1JVOTNSUWtsYmhaVUVYZDBSeFVyRUYwalprOWNFaUoxVEJaNUR5OG9NRE1MUkRkNEZqQUhGajFTREVFeldIRnpLaUkyWmljbkNrQWlFaEpFRUNFNFV4aFdiaVF6Y25Cek14WURVQUpjRXlnZ2JEOVlQMEFBUmo1SUhCVSJ9.Cb7pEf2kYBsQRdG-zeBVipGJvmkj6R5KPsr8-g4QuuHFLdQMLaW2jSb0Drn3REgK6RFhoJ3081S9Pnn9-jDIxA");
    	return it;
    }
    @RequestMapping("api/verify")
    public Verify verify(HttpSession httpSession) {
        final AccessToken token = getAccessToken(httpSession);
        return lineAPIService.verify(token);
    }

    @RequestMapping("api/revoke")
    public void revoke(HttpSession httpSession) {
        final AccessToken token = getAccessToken(httpSession);
        lineAPIService.revoke(token);
    }

    private AccessToken getAccessToken(HttpSession httpSession) {
        return (AccessToken) httpSession.getAttribute(WebController.ACCESS_TOKEN);
    }

    private void setAccessToken(HttpSession httpSession, AccessToken accessToken) {
        httpSession.setAttribute(WebController.ACCESS_TOKEN, accessToken);
    }


}
