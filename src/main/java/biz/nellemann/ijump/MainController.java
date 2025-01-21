package biz.nellemann.ijump;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.util.net.SshdSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.PasswordField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class MainController {

    private final static Logger log = LoggerFactory.getLogger(MainController.class);


    @FXML
    private TextField publicHost;

    @FXML
    private TextField remoteHost;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button btnStart;

    @FXML
    private Button btnStop;

    @FXML
    public TextArea textAreaLog;

    private final Map<Integer, Integer> mapOfPortsToForward = new HashMap<>();

    private ClientTask clientTask;


    @FXML
    public void initialize() throws Exception {
        PrintStream printStream = new PrintStream(new MyCustomAppender(textAreaLog));
        System.setOut(printStream);
        System.setErr(printStream);

        mapOfPortsToForward.put(23, 23);
        mapOfPortsToForward.put(446, 446);
        mapOfPortsToForward.put(449, 449);
        mapOfPortsToForward.put(2001, 2001);
        mapOfPortsToForward.put(2002, 2002);
        mapOfPortsToForward.put(2003, 2003);
        mapOfPortsToForward.put(2010, 2010);
        mapOfPortsToForward.put(2222, 22);
        mapOfPortsToForward.put(2300, 2300);
        mapOfPortsToForward.put(2323, 2323);
        mapOfPortsToForward.put(3001, 3001);
        mapOfPortsToForward.put(3002, 3002);
        mapOfPortsToForward.put(4026, 4026);
        mapOfPortsToForward.put(8470, 8470);
        mapOfPortsToForward.put(8471, 8471);
        mapOfPortsToForward.put(8472, 8472);
        mapOfPortsToForward.put(8473, 8473);
        mapOfPortsToForward.put(8474, 8474);
        mapOfPortsToForward.put(8475, 8475);
        mapOfPortsToForward.put(8476, 8476);
        mapOfPortsToForward.put(9470, 9470);
        mapOfPortsToForward.put(9471, 9471);
        mapOfPortsToForward.put(9472, 9472);
        mapOfPortsToForward.put(9473, 9473);
        mapOfPortsToForward.put(9474, 9474);
        mapOfPortsToForward.put(9475, 9475);
        mapOfPortsToForward.put(9476, 9476);
    }


    @FXML
    protected void onButtonStart() {
        textAreaLog.setText("");
        ClientConnection connection = new ClientConnection(publicHost.getText(), username.getText(), password.getText(), remoteHost.getText(), mapOfPortsToForward);
        clientTask = new ClientTask(connection);
        new Thread(clientTask).start();
        btnStart.disableProperty().setValue(true);
        btnStop.disableProperty().setValue(false);
    }


    @FXML
    protected void onStopButtonClick() {
        clientTask.isRunning();
        clientTask.cancel();
        btnStart.disableProperty().setValue(false);
        btnStop.disableProperty().setValue(true);
    }


}
