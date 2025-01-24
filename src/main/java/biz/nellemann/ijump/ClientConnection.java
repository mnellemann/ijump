package biz.nellemann.ijump;

import java.util.Map;

public record ClientConnection(String publicHost, String username, String password, String privateKey, String internalHost, Map<Integer,Integer> portForwards) {
}
