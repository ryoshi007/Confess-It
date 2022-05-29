module com.example.confessit {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires mysql.connector.java;
    requires json.simple;
    requires me.xdrop.fuzzywuzzy;
    requires stanford.corenlp;

    opens com.confessit to javafx.fxml;
    exports com.confessit;
}