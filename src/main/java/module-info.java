module org.utleon.muestra_elzarape {
    requires javafx.controls;
    requires javafx.fxml;
    requires unirest.java;
    requires com.google.gson;
    requires org.apache.httpcomponents.httpclient;

    // opens org.utleon.muestra_elzarape.model to javafx.fxml;
    opens org.utleon.muestra_elzarape.model to com.google.gson;

    opens org.utleon.muestra_elzarape to javafx.fxml;
    exports org.utleon.muestra_elzarape;
}