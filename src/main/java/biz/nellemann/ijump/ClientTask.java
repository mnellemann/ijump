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
import java.time.Duration;
import java.util.EnumSet;


public class ClientTask extends Task<Void> {

    private final static Logger log = LoggerFactory.getLogger(ClientTask.class);

    private final ClientConnection clientConnection;


    public ClientTask(ClientConnection connection) {
        this.clientConnection = connection;
    }


    @Override
    public Void call() throws InterruptedException {
        updateMessage("Starting");
        SshClient sshClient = SshClient.setUpDefaultClient();
        sshClient.start();

        // using the client for multiple sessions...
        try (ClientSession session = sshClient.connect(clientConnection.username(), clientConnection.remoteHost(), 22).verify(5000).getSession()) {

            session.addPasswordIdentity(clientConnection.password()); // for password-based authentication
            // or
            //session.addPublicKeyIdentity(...key-pair...); // for password-less authentication
            // Note: can add BOTH password AND public key identities - depends on the client/server security setup

            session.auth().verify(5000);
            // start using the session to run commands, do SCP/SFTP, create local/remote port forwarding, etc...

            clientConnection.portForwards().forEach( (localPort, remotePort) -> {
                try {
                    SshdSocketAddress localSocket = new SshdSocketAddress("localhost", localPort);
                    SshdSocketAddress remoteSocket = new SshdSocketAddress(clientConnection.privateHost(), remotePort);
                    session.startLocalPortForwarding(localSocket, remoteSocket);
                } catch (IOException e) {
                    log.warn(e.getMessage());
                }

            });

            session.setSessionHeartbeat(SessionHeartbeatController.HeartbeatType.IGNORE, Duration.ofMinutes(1));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        while(!isCancelled()) {
            Thread.sleep(500);
        }

        return null;
    }


}
