
package biz.nellemann.ijump;

import java.io.OutputStream;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class MyCustomAppender extends OutputStream {

    private final TextArea textArea;


    public MyCustomAppender(TextArea textArea) {
        this.textArea = textArea;
    }


    @Override
    public void write(int b) {
        Platform.runLater( () -> {
            textArea.appendText(String.valueOf((char) b));
        });
    }

}
