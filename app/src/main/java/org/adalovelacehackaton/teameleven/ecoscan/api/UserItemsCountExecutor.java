package org.adalovelacehackaton.teameleven.ecoscan.api;

import org.json.JSONObject;

public interface UserItemsCountExecutor {
    void execute(int responseCode, String data, JSONObject jsonData);
}
