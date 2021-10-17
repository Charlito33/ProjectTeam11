package org.adalovelacehackaton.teameleven.ecoscan.api;

public interface ItemExecutor {
    void execute(int responseCode, String data, Item item);
}
