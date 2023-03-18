package org.marketcetera.ui;

import java.io.IOException;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        PhotonApp.setRoot("secondary");
    }
}
