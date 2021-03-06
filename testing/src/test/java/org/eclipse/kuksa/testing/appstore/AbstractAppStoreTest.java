/*********************************************************************
 * Copyright (c)  2019 Expleo Germany GmbH [and others].
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Expleo Germany GmbH
 * @author: cnguyen
 **********************************************************************/

package  org.eclipse.kuksa.testing.appstore;

import org.apache.logging.log4j.Level;
import org.eclipse.kuksa.testing.AbstractTestCase;
import org.eclipse.kuksa.testing.client.Request;
import org.eclipse.kuksa.testing.config.AppStoreConfiguration;
import org.eclipse.kuksa.testing.model.Credentials;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@ContextConfiguration(classes = {AppStoreConfiguration.class})
public abstract class AbstractAppStoreTest extends AbstractTestCase {

    // AppStore: /user
    static final String JSON_PROPERTY_USER_USERNAME = "username";
    static final String JSON_PROPERTY_USER_USERNAME_VALUE = "test.user.username";

    static final String JSON_PROPERTY_USER_PASSWORD = "password";
    static final String JSON_PROPERTY_USER_PASSWORD_VALUE = "test.user.password";

    static final String JSON_PROPERTY_USER_USERTYPE = "userType";
    static final String JSON_PROPERTY_USER_USERTYPE_VALUE = "Normal";

    static final String JSON_PROPERTY_USER_ADMINUSER = "adminuser";
    static final boolean JSON_PROPERTY_USER_ADMINUSER_VALUE = false;

    // AppStore: /category
    static final String JSON_PROPERTY_CATEGORY_NAME = "name";
    static final String JSON_PROPERTY_CATEGORY_NAME_VALUE = "test.app.category";

    // AppStore: /app
    static final String JSON_PROPERTY_APP_NAME = "name";
    static final String JSON_PROPERTY_APP_NAME_VALUE = "test.app.name";

    static final String JSON_PROPERTY_APP_VERSION = "version";
    static final String JSON_PROPERTY_APP_VERSION_VALUE = "1.0";

    static final String JSON_PROPERTY_APP_HAWKBIT_NAME = "hawkbitname";
    static final String JSON_PROPERTY_APP_HAWKBIT_NAME_VALUE = "test.app.hawkbit.name";

    static final String JSON_PROPERTY_APP_DESCRIPTION = "description";
    static final String JSON_PROPERTY_APP_DESCRIPTION_VALUE = "test.app.description";

    static final String JSON_PROPERTY_APP_OWNER = "owner";
    static final String JSON_PROPERTY_APP_OWNER_VALUE = "test.app.owner";

    static final String JSON_PROPERTY_APP_DOWNLOADCOUNT = "downloadcount";
    static final int JSON_PROPERTY_DOWNLOADCOUNT_VALUE = 0;

    static final String JSON_PROPERTY_APP_PUBLISH_DATE = "publishdate";
    static final Long JSON_PROPERTY_APP_PUBLISH_DATE_VALUE = new Date().getTime(); // today

    static final String JSON_PROPERTY_APP_CATEGORY_NAME = "appcategory";
    static JSONObject JSON_PROPERTY_APP_CATEGORY_NAME_VALUE = new JSONObject();

    private Long userId;

    private Long categoryId;

    private Long appId;

    @Autowired
    private AppStoreConfiguration appstoreConfig;

    String address;

    Credentials credentials;

    @Override
    protected void testSetup() throws Exception {
        address = appstoreConfig.getAddress();
        credentials = new Credentials(appstoreConfig.getUsername(), appstoreConfig.getPassword());
    }

    Request.Builder getBaseRequestBuilder() {
        return new Request.Builder()
                .headers(getBaseRequestHeaders())
                .credentials(credentials);
    }

    String createUser() throws JSONException {
        Request request = getBaseRequestBuilder()
                .post()
                .url(buildUrl(address, "/api/1.0/user/"))
                .body(new JSONObject()
                        .put(JSON_PROPERTY_USER_ADMINUSER, JSON_PROPERTY_USER_ADMINUSER_VALUE)
                        .put(JSON_PROPERTY_USER_USERNAME, JSON_PROPERTY_USER_USERNAME_VALUE)
                        .put(JSON_PROPERTY_USER_PASSWORD, JSON_PROPERTY_USER_PASSWORD_VALUE)
                        .put(JSON_PROPERTY_USER_USERTYPE, JSON_PROPERTY_USER_USERTYPE_VALUE)
                )
                .build();

        ResponseEntity<String> response = executeApiCall(request);
        LOGGER.info("JUST CREATED " +  response.getBody());

        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.error(new Exception("Failed to create app store user."));
        }

        return response.getBody();
    }

    void removeUser(Long userId) {
        Request request = getBaseRequestBuilder()
                .delete()
                .url(buildUrl(address, "/api/1.0/user/" + userId))
                .build();

        ResponseEntity<String> response = executeApiCall(request);

        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.error(new Exception("Failed to remove app store user."));
        }
    }

    void removeOEM(Long oemId) {
        Request request = getBaseRequestBuilder()
                .delete()
                .url(buildUrl(address, "/api/1.0/oem/" + oemId))
                .build();

        ResponseEntity<String> response = executeApiCall(request);

        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.error(new Exception("Failed to remove app store iem."));
        }
    }

    String createCategory() throws JSONException {
        Request request = getBaseRequestBuilder()
                .post()
                .url(buildUrl(address, "/api/1.0/appcategory/"))
                .body(new JSONObject()
                        .put(JSON_PROPERTY_CATEGORY_NAME, JSON_PROPERTY_CATEGORY_NAME_VALUE)
                )
                .build();

        ResponseEntity<String> response = executeApiCall(request);
        LOGGER.log(Level.INFO, "JUST CREATED " +  response.getBody());

        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.error(new Exception("Failed to create app store category."));
        }

        return response.getBody();
    }

    void removeCategory(Long categoryId) {
        Request request = getBaseRequestBuilder()
                .delete()
                .url(buildUrl(address, "/api/1.0/appcategory/" + categoryId))
                .build();

        ResponseEntity<String> response = executeApiCall(request);

        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.error(new Exception("Failed to remove app store category."));
        }
    }

    String createApp(Long categoryId) throws JSONException {
        try {
            JSON_PROPERTY_APP_CATEGORY_NAME_VALUE = new JSONObject().put("id", categoryId ).put("name", "test.app.category");
        } catch (JSONException e) {
            e.printStackTrace();
        }

            System.out.println(JSON_PROPERTY_APP_CATEGORY_NAME_VALUE);

        Request request = getBaseRequestBuilder()
                .post()
                .url(buildUrl(address, "/api/1.0/app/"))
                .body(new JSONObject()
                        .put(JSON_PROPERTY_APP_NAME, JSON_PROPERTY_APP_NAME_VALUE)
                        .put(JSON_PROPERTY_APP_VERSION, JSON_PROPERTY_APP_VERSION_VALUE)
                        .put(JSON_PROPERTY_APP_DESCRIPTION, JSON_PROPERTY_APP_DESCRIPTION_VALUE)
                        .put(JSON_PROPERTY_APP_HAWKBIT_NAME, JSON_PROPERTY_APP_HAWKBIT_NAME_VALUE)
                        .put(JSON_PROPERTY_APP_OWNER, JSON_PROPERTY_APP_OWNER_VALUE )
                        .put(JSON_PROPERTY_APP_CATEGORY_NAME, JSON_PROPERTY_APP_CATEGORY_NAME_VALUE )
                        .put(JSON_PROPERTY_APP_DOWNLOADCOUNT, JSON_PROPERTY_DOWNLOADCOUNT_VALUE )
                        .put(JSON_PROPERTY_APP_PUBLISH_DATE, JSON_PROPERTY_APP_PUBLISH_DATE_VALUE)
                )
                .build();

        ResponseEntity<String> response = executeApiCall(request);
        System.out.println("JUST CREATED " +  response.getBody());

        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.error(new Exception("Failed to create app store app."));
        }
        return response.getBody();
    }

    void removeApp(Long appId) {
        Request request = getBaseRequestBuilder()
                .delete()
                .url(buildUrl(address, "/api/1.0/app/" + appId))
                .build();

        ResponseEntity<String> response = executeApiCall(request);

        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.error(new Exception("Failed to remove app store app."));
        }
    }

    public Long checkUsernameExists() throws Exception  {
        Request request = getBaseRequestBuilder()
                .get()
                .url(buildUrl(address, "/api/1.0/user"))
                .build();

        ResponseEntity<String> response = executeApiCall(request);

        int totalPages =  new JSONObject(response.getBody()).getInt("totalPages");

        for(int i = 0; i < totalPages; i++) {
            Request requestPage = getBaseRequestBuilder()
                    .get()
                    .url(buildUrl(address, "/api/1.0/user?page=" + i))
                    .build();

            ResponseEntity<String> responsePage = executeApiCall(requestPage);

            JSONArray array = new JSONObject(responsePage.getBody()).getJSONArray("content");
            for (int j = 0; j < array.length(); j++) {
                JSONObject row = array.getJSONObject(j);
                if(row.getString("username").equals(JSON_PROPERTY_USER_USERNAME_VALUE)) {

                    userId = row.getLong("id");
                }
            }
        }

        if(userId != null) {
            return userId;
        } else {
            return null;
        }
    }

    public Long checkCategoryExists() throws Exception {

        Request request = getBaseRequestBuilder()
                .get()
                .url(buildUrl(address, "/api/1.0/appcategory"))
                .build();

        ResponseEntity<String> response = executeApiCall(request);

        int totalPages =  new JSONObject(response.getBody()).getInt("totalPages");

        for(int i = 0; i < totalPages; i++) {
            Request requestPage = getBaseRequestBuilder()
                    .get()
                    .url(buildUrl(address, "/api/1.0/appcategory?page=" + i))
                    .build();

            ResponseEntity<String> responsePage = executeApiCall(requestPage);

            JSONArray array = new JSONObject(responsePage.getBody()).getJSONArray("content");
            for (int j = 0; j < array.length(); j++) {
                JSONObject row = array.getJSONObject(j);
                if(row.getString("name").equals(JSON_PROPERTY_CATEGORY_NAME_VALUE)) {

                    categoryId = row.getLong("id");
                }
            }
        }

        if(categoryId != null) {
            return categoryId;
        } else {
            return null;
        }
    }

    public Long checkAppExists() throws Exception {
        Request request = getBaseRequestBuilder()
                .get()
                .url(buildUrl(address, "/api/1.0/app"))
                .build();

        ResponseEntity<String> response = executeApiCall(request);

        int totalPages =  new JSONObject(response.getBody()).getInt("totalPages");

        for(int i = 0; i < totalPages; i++) {
            Request requestPage = getBaseRequestBuilder()
                    .get()
                    .url(buildUrl(address, "/api/1.0/app?page=" + i))
                    .build();

            ResponseEntity<String> responsePage = executeApiCall(requestPage);

            JSONArray array = new JSONObject(responsePage.getBody()).getJSONArray("content");
            for (int j = 0; j < array.length(); j++) {
                JSONObject row = array.getJSONObject(j);
                if(row.getString("name").equals(JSON_PROPERTY_APP_NAME_VALUE)) {

                    appId = row.getLong("id");
                }
            }
        }

        if(appId != null) {
            return appId;
        } else {
            return null;
        }
    }

}
