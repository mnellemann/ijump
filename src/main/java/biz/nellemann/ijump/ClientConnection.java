package biz.nellemann.ijump;

import java.util.Map;

public record ClientConnection(String remoteHost, String username, String password, String publicKey, String privateHost, Map<Integer,Integer> portForwards) {
}
