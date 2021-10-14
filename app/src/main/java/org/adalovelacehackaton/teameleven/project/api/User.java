package org.adalovelacehackaton.teameleven.project.api;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private int ID;
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private int accessLevel;
    private int groupId;
    private int points;

    public User(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            ID = jsonObject.getInt("ID");
            username = jsonObject.getString("username");
            email = jsonObject.getString("email");
            firstname = jsonObject.getString("firstname");
            lastname = jsonObject.getString("lastname");
            accessLevel = jsonObject.getInt("access_level");
            groupId = jsonObject.getInt("group_id");
            points = jsonObject.getInt("points");
        } catch (JSONException e) {
            System.err.println("Can't create new User !");
            e.printStackTrace();
        }
    }

    public int getID() {
        return ID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getPoints() {
        return points;
    }
}
