package biz.nellemann.ijump;

import javafx.concurrent.Task;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.session.SessionHeartbeatController;
import org.apache.sshd.common.util.net.SshdSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.security.KeyPair;
import java.time.Duration;
import java.util.EnumSet;


public class ClientTask extends Task<Boolean> {

    private final static Logger log = LoggerFactory.getLogger(ClientTask.class);

    private final ClientConnection clientConnection;


    public ClientTask(ClientConnection connection) {
        this.clientConnection = connection;
    }


    @Override
    public Boolean call() throws InterruptedException {
        updateMessage("Connecting ...");
        SshClient sshClient = SshClient.setUpDefaultClient();
        sshClient.start();

        try (ClientSession session = sshClient.connect(clientConnection.username(), clientConnection.remoteHost(), 22).verify(5000).getSession()) {
            session.addPasswordIdentity(clientConnection.password()); // for password-based authentication
            session.auth().verify(5000);

            clientConnection.portForwards().forEach( (localPort, remotePort) -> {
                try {
                    SshdSocketAddress localSocket = new SshdSocketAddress("localhost", localPort);
                    SshdSocketAddress remoteSocket = new SshdSocketAddress(clientConnection.privateHost(), remotePort);
                    session.startLocalPortForwarding(localSocket, remoteSocket);
                } catch (IOException e) {
                    updateMessage(e.getMessage());
                    updateValue(false);
                    log.warn(e.getMessage());
                }
            });

            session.setSessionHeartbeat(SessionHeartbeatController.HeartbeatType.IGNORE, Duration.ofMinutes(1));
            updateValue(true);
            updateMessage("Connected.");

            while(session.isOpen() && !isCancelled()) {
                Thread.sleep(500);
            }

        } catch (Exception e) {
            updateMessage(e.getMessage());
        }

        return null;
    }


}
