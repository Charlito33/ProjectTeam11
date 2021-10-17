package org.adalovelacehackaton.teameleven.ecoscan.api;

import org.json.JSONException;
import org.json.JSONObject;

public class Item {
    private int ID;
    private String scancode;
    private String name;
    private ItemType type;
    private int weight;
    private int pointsValue;

    public Item(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            ID = jsonObject.getInt("ID");
            scancode = jsonObject.getString("scancode");
            name = jsonObject.getString("name");
            type = ItemType.get(jsonObject.getString("type"));
            weight = jsonObject.getInt("weight");
            pointsValue = jsonObject.getInt("points_value");
        } catch (JSONException e) {
            System.err.println("Can't create new User !");
            e.printStackTrace();
        }
    }

    public Item(int ID, String scancode, String name, ItemType type, int weight, int pointsValue) {
        this.ID = ID;
        this.scancode = scancode;
        this.name = name;
        this.type = type;
        this.weight = weight;
        this.pointsValue = pointsValue;
    }

    public int getID() {
        return ID;
    }

    public String getScancode() {
        return scancode;
    }

    public String getName() {
        return name;
    }

    public ItemType getType() {
        return type;
    }

    public int getWeight() {
        return weight;
    }

    public int getPointsValue() {
        return pointsValue;
    }
}
