package biz.nellemann.jump;

import javafx.concurrent.Task;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.session.SessionHeartbeatController;
import org.apache.sshd.common.util.net.SshdSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.time.Duration;


public class ClientTask extends Task<Boolean> {

    private final static Logger log = LoggerFactory.getLogger(ClientTask.class);

    private final ClientConnection clientConnection;


    public ClientTask(ClientConnection connection) {
        this.clientConnection = connection;
    }


    @Override
    public Boolean call() throws IOException {
        updateMessage("Connecting ...");

        try(SshClient sshClient = SshClient.setUpDefaultClient()) {
            sshClient.setForwardingFilter(new org.apache.sshd.server.forward.AcceptAllForwardingFilter());
            sshClient.start();

            try (ClientSession session = sshClient.connect(clientConnection.username(), clientConnection.publicHost(), 22).verify(5000).getSession()) {
                session.addPasswordIdentity(clientConnection.password()); // for password-based authentication
                session.auth().verify(2500);

                clientConnection.portForwards().forEach( (localPort, remotePort) -> {
                    try {
                        log.info("Creating port forward for localhost:{} to {}:{}", localPort, clientConnection.internalHost(), remotePort);
                        SshdSocketAddress localSocket = new SshdSocketAddress(localPort);
                        SshdSocketAddress remoteSocket = new SshdSocketAddress(clientConnection.internalHost(), remotePort);
                        session.startLocalPortForwarding(localSocket, remoteSocket);
                    } catch (IOException e) {
                        log.debug("Port forwarding {} to {} on {} - {}", localPort, clientConnection.internalHost(), remotePort, e.getMessage());
                    }
                });

                /*
                Map<String, ?> env = new HashMap<>();
                PtyChannelConfiguration ptyConfig = new PtyChannelConfiguration();
                ptyConfig.setPtyType(PtyChannelConfigurationHolder.DUMMY_PTY_TYPE);

                // The same code can be used when opening a ChannelExec in order to run a single interactive command
                try (ClientChannel channel = session.createShellChannel(ptyConfig, env)) {
                    try {
                        channel.open().verify(2500);

                        channel.setErr(System.err);
                        channel.setOut(System.out);
                        //spawnStdinThread(channel.getInvertedIn());
                        //spawnStdoutThread(channel.getInvertedOut());
                        //spawnStderrThread(channel.getInvertedErr());

                        // Wait (forever) for the channel to close - signalling shell exited
                        channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 0L);
                    } finally {
                        // ... stop the pumping threads ...
                    }
                }*/

                session.setSessionHeartbeat(SessionHeartbeatController.HeartbeatType.IGNORE, Duration.ofMinutes(1));
                updateValue(true);
                updateMessage("Connected.");

                while(session.isOpen() && !isCancelled()) {
                    Thread.sleep(500);
                }

            } catch (Exception e) {
                updateMessage(e.getMessage());
                return null;
            } finally {
                sshClient.stop();
            }

        }

        updateMessage("");
        return null;
    }


}
