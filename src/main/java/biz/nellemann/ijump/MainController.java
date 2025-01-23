package biz.nellemann.ijump;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.fxml.FXML;


public class MainController {

    private final static Logger log = LoggerFactory.getLogger(MainController.class);

    ConfigurationService configurationService = new ConfigurationService();


    @FXML
    private TextField publicHost;

    @FXML
    private TextField remoteHost;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private TextArea publicKey;

    @FXML
    private Button btnStart;

    @FXML
    private Button btnStop;

    @FXML
    private CheckBox progress;

    @FXML
    private Label labelMessage;

    @FXML
    private TextArea syslog;


    private final Map<Integer, Integer> mapOfPortsToForward = new HashMap<>();

    private ClientTask clientTask;


    @FXML
    public void initialize() {
        PrintStream printStream = new PrintStream(new MyCustomAppender(syslog));
        System.setOut(printStream);
        System.setErr(printStream);

        progress.visibleProperty().setValue(false);

        mapOfPortsToForward.put(23, 23);
        mapOfPortsToForward.put(446, 446);
        mapOfPortsToForward.put(449, 449);
        mapOfPortsToForward.put(2001, 2001);
        mapOfPortsToForward.put(2002, 2002);
        mapOfPortsToForward.put(2003, 2003);
        mapOfPortsToForward.put(2010, 2010);
        mapOfPortsToForward.put(2222, 22);
        mapOfPortsToForward.put(2300, 2300);
        mapOfPortsToForward.put(2301, 2301);
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
        mapOfPortsToForward.put(50000, 23);


        publicHost.setText(configurationService.get("publicHost", ""));
        remoteHost.setText(configurationService.get("privateHost", ""));
        username.setText(configurationService.get("username", ""));
        password.setText(configurationService.get("password", ""));
        publicKey.setText(configurationService.get("publicKey", ""));

        publicHost.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                configurationService.put("publicHost", publicHost.getText());
            }
        });

        remoteHost.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                configurationService.put("privateHost", remoteHost.getText());
            }
        });

        username.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                configurationService.put("username", username.getText());
            }
        });

        password.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                configurationService.put("password", password.getText());
            }
        });

        publicKey.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                configurationService.put("publicKey", publicKey.getText());
            }
        });

    }


    @FXML
    protected void onButtonStart() {
        syslog.setText("");
        ClientConnection connection = new ClientConnection(publicHost.getText(), username.getText(), password.getText(), publicKey.getText(), remoteHost.getText(), mapOfPortsToForward);
        clientTask = new ClientTask(connection);

        progress.selectedProperty().bind(clientTask.runningProperty());
        labelMessage.textProperty().bind(clientTask.messageProperty());

        clientTask.setOnCancelled(e -> {
            labelMessage.textProperty().unbind();
            stop();
        });

        clientTask.setOnFailed(e -> {
            labelMessage.textProperty().unbind();
            stop();
        });

        clientTask.setOnSucceeded(e -> {

            labelMessage.textProperty().unbind();
            stop();
        });

        new Thread(clientTask).start();
        progress.visibleProperty().setValue(true);
        btnStart.disableProperty().setValue(true);
        btnStop.disableProperty().setValue(false);
    }


    @FXML
    protected void onButtonStop() {
        clientTask.cancel(true);
        stop();
        labelMessage.setText("");
    }

    private void stop() {
        btnStart.disableProperty().setValue(false);
        btnStop.disableProperty().setValue(true);
        progress.visibleProperty().setValue(false);
    }

}
