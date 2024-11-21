package org.utleon.muestra_elzarape;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.http.client.UserTokenHandler;
import org.utleon.muestra_elzarape.model.Ciudad;
import org.utleon.muestra_elzarape.model.Sucursal;

import java.util.List;

public class SucursalesController {

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @FXML
    private TableColumn<Sucursal, String> colDireccion;

    @FXML
    private TableColumn<Sucursal, Integer> colEstatus;

    @FXML
    private TableColumn<Sucursal, Integer> colIdSucursal;

    @FXML
    private TableColumn<Sucursal, String> colNombre;

    @FXML
    private TableView<Sucursal> tblSucursales;

    @FXML
    private TextField txtCalle;

    @FXML
    private ComboBox<Ciudad> txtCiudad;

    @FXML
    private TextField txtColonia;

    @FXML
    private CheckBox txtEstatus;

    @FXML
    private TextField txtFoto;

    @FXML
    private TextField txtHorarios;

    @FXML
    private TextField txtIdSucursal;

    @FXML
    private TextField txtLatitud;

    @FXML
    private TextField txtLongitud;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtNumCalle;

    @FXML
    private TextField txtUrl;

    Globals globals = new Globals();
    ObservableList<Sucursal> sucursales;
    ObservableList<Ciudad> ciudades;
    Sucursal sucursalSelected = null;

    @FXML
    public void initialize() {
        initColumns();
        txtCiudad.setConverter(new StringConverter<Ciudad>() {
            @Override
            public String toString(Ciudad ciudad) {
                return ciudad != null ? ciudad.getNombre() : "";
            }

            @Override
            public Ciudad fromString(String nombre) {
                // Este método no se usará en este caso
                return null;
            }
        });
        txtCiudad.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Ciudad seleccionada: " + newValue.getNombre());
            }
        });
        btnCancelar.setOnAction(event -> cleanForm());
        loadSucursales();
        loadCiudades("");
        tblSucursales.setItems(sucursales);
        txtCiudad.setItems(ciudades);

        tblSucursales.setOnMouseClicked(event -> {
            showSucursalSelected();
        });
        btnGuardar.setOnAction(event -> {
            Sucursal s = new Sucursal();
            s.setNombre(txtNombre.getText());
            s.setLatitud(txtLatitud.getText());
            s.setLongitud(txtLongitud.getText());
            s.setFoto(txtFoto.getText());
            s.setUrlWeb(txtUrl.getText());
            s.setHorarios(txtHorarios.getText());
            s.setCalle(txtCalle.getText());
            s.setNumCalle(txtNumCalle.getText());
            s.setColonia(txtColonia.getText());
            s.setCiudad(txtCiudad.getSelectionModel().getSelectedItem());
            System.out.println(s.toString());

            String salida = enviarSucursal(s);
            loadSucursales();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Guardar Sucursal");
            alert.setContentText("Correcto");
            alert.showAndWait();


        });

    }

    private void initColumns() {
        colIdSucursal.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getIdSucursal()));
        colNombre.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getNombre()));
        colDireccion.setCellValueFactory(col -> new SimpleObjectProperty<>(
                col.getValue().getCalle() + " #" +
                            col.getValue().getNumCalle() + ", " +
                            col.getValue().getColonia() + "." ));
        colEstatus.setCellValueFactory(col -> new SimpleObjectProperty<>(col.getValue().getActivo()));
    }

    private void loadSucursales() {
        new Thread(() ->{
            HttpResponse<String> response = Unirest.get(globals.BASE_URL+"sucursal/getAll").asString();
            Platform.runLater(() ->{
                Gson gson = new Gson();
                sucursales = FXCollections.observableArrayList(List.of(gson.fromJson(response.getBody(), Sucursal[].class)));
                sucursales.forEach(sucursal -> System.out.println("ID "+sucursal.getCiudad().getIdCiudad()));
                tblSucursales.setItems(sucursales);
                tblSucursales.refresh();
            });
        }).start();
    }

    private void cleanForm(){
        txtCalle.setText("");
        txtColonia.setText("");
        txtFoto.setText("");
        txtHorarios.setText("");
        txtIdSucursal.setText("");
        txtLatitud.setText("");
        txtLongitud.setText("");
        txtNombre.setText("");
        txtNumCalle.setText("");
        txtUrl.setText("");
        btnGuardar.setDisable(false);
    }

    private void loadCiudades(String filtro) {
        new Thread(() ->{
            HttpResponse<String> response = Unirest.get(globals.BASE_URL+"sucursal/getAllCiudades?filtro="+filtro).asString();
            //System.out.println(response.getBody());
            Platform.runLater(() ->{
                //txtRespuesta.setText(response.getBody());
                Gson gson = new Gson();
                ciudades = FXCollections.observableArrayList(List.of(gson.fromJson(response.getBody(), Ciudad[].class)));
                txtCiudad.setItems(ciudades);
            });
        }).start();
    }

    public void showSucursalSelected(){

        sucursalSelected = tblSucursales.getSelectionModel().getSelectedItem();
        txtCalle.setText(sucursalSelected.getCalle());
        txtColonia.setText(sucursalSelected.getColonia());
        txtFoto.setText(sucursalSelected.getFoto());
        txtHorarios.setText(sucursalSelected.getHorarios());
        txtIdSucursal.setText(String.valueOf(sucursalSelected.getIdSucursal()));
        txtLatitud.setText(sucursalSelected.getLatitud().toString());
        txtLongitud.setText(sucursalSelected.getLongitud());
        txtNombre.setText(sucursalSelected.getNombre());
        txtNumCalle.setText(sucursalSelected.getNumCalle());
        txtUrl.setText(sucursalSelected.getUrlWeb());
        //txtCiudad.getSelectionModel().select(findCiudadById(sucursalSelected.getCiudad().getIdCiudad()));
        txtCiudad.getSelectionModel().select(findCiudadById(sucursalSelected.getCiudad().getIdCiudad()));
        txtEstatus.setSelected(sucursalSelected.getActivo() == 1);
        btnGuardar.setText("Modificar");
        System.out.println(sucursalSelected.getCiudad().getIdCiudad());
    }

    public Ciudad findCiudadById(int id) {
        Ciudad ciudad = null;
        for (Ciudad item : ciudades) {
            if(item.getIdCiudad() == id) {
                return item;
            }
        }
        return null;
    }

    public String enviarSucursal(Sucursal sucursal) {
        try {
            Gson gson = new Gson();
            String sucursalJson = gson.toJson(sucursal);
            System.out.println(sucursalJson);

            HttpResponse<String> response = Unirest.post(globals.BASE_URL + "sucursal/save")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("sucursal",sucursalJson) // Enviar el JSON por el body de la petición
                    .asString();

            // Validar la respuesta
            if (response.getStatus() == 200) {
                System.out.println("Sucursal enviada exitosamente.");
                cleanForm();
                return response.getBody(); // El servidor responde con algún cuerpo JSON vacío
            } else {
                System.err.println("Error al enviar sucursal: " + response.getStatus() + " - " + response.getBody());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
