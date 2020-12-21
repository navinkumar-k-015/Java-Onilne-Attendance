/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import utils.ConnectionUtil;

public class FacultyController implements Initializable{

	@FXML
    private TextField txtuserid;
	@FXML
    private TextField txtsubject;
    @FXML
    private TextField txtfromtime;
    @FXML
    private TextField txttotime;
    
    private String faculty;
    @FXML
    private DatePicker txtDOB;
    @FXML
    private Button btnSave;
    @FXML
    private ComboBox<String> status;
    @FXML
    Label lblStatus;
    @FXML
    private Button btnLogout;

    @FXML
    TableView tblData;

    /**
     * Initializes the controller class.
     */
    PreparedStatement preparedStatement;
    Connection connection;

    public FacultyController() {
        connection = (Connection) ConnectionUtil.conDB();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        status.getItems().addAll("Present", "Absent","Leave");
        status.getSelectionModel().select("Present");
    }
    public void initData(String string) {
		faculty = string;
		System.out.println(faculty);
		fetColumnList();
        fetRowList();
	}
    @FXML
    private void HandleLogout(MouseEvent event) {
		try {
			Node node = (Node) event.getSource();
	        Stage stage = (Stage) node.getScene().getWindow();
	        //stage.setMaximized(true);
	        stage.close();
	        FXMLLoader loader = new FXMLLoader(
	    		    getClass().getResource("/fxml/Login.fxml"));
	    	Scene scene;
			scene = new Scene(loader.load());
			stage.setScene(scene);
	    	stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }

    @FXML
    private void HandleEvents(MouseEvent event) {
        //check if not empty
        if (txtfromtime.getText().isEmpty() || txttotime.getText().isEmpty() || txtDOB.getValue().equals(null)) {
            lblStatus.setTextFill(Color.TOMATO);
            lblStatus.setText("Enter all details");
        } else {
            saveData();
        }

    }

    private void clearFields() {
    	txtfromtime.clear();
    	txttotime.clear();
        txtuserid.clear();
        txtsubject.clear();
    }

    private String saveData() {

        try {
            String st = "INSERT INTO attendance (id, Subject, Fromtime, Totime, Faculty, Status, Date) VALUES (?,?,?,?,?,?,?)";
            preparedStatement = (PreparedStatement) connection.prepareStatement(st);
            preparedStatement.setString(1, txtuserid.getText());
            preparedStatement.setString(2, txtsubject.getText());
            preparedStatement.setString(3, txtfromtime.getText());
            preparedStatement.setString(4, txttotime.getText());
            preparedStatement.setString(5, faculty);
            preparedStatement.setString(6, status.getValue().toString());
            preparedStatement.setString(7, txtDOB.getValue().toString());

            preparedStatement.executeUpdate();
            lblStatus.setTextFill(Color.GREEN);
            lblStatus.setText("Added Successfully");

            fetRowList();
            //clear fields
            clearFields();
            return "Success";

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            lblStatus.setTextFill(Color.TOMATO);
            lblStatus.setText(ex.getMessage());
            return "Exception";
        }
    }

    private ObservableList<ObservableList> data;
    String SQL = "SELECT * from attendance Where Faculty=?";

    //only fetch columns
    private void fetColumnList() {

        try {
        	preparedStatement = connection.prepareStatement(SQL);
        	preparedStatement.setString(1, faculty);
            ResultSet rs = preparedStatement.executeQuery();

            //SQL FOR SELECTING ALL OF CUSTOMER
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1).toUpperCase());
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                tblData.getColumns().removeAll(col);
                tblData.getColumns().addAll(col);

                System.out.println("Column [" + i + "] ");

            }

        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());

        }
    }

    //fetches rows and data from the list
    private void fetRowList() {
        data = FXCollections.observableArrayList();
        try {
        	preparedStatement = connection.prepareStatement(SQL);
        	preparedStatement.setString(1, faculty);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                //Iterate Row
                ObservableList row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                System.out.println("Row [1] added " + row);
                data.add(row);

            }

            tblData.setItems(data);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

	

}
