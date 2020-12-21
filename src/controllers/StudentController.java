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

public class StudentController implements Initializable {

	@FXML
    private DatePicker txtfrom;
	@FXML
    private DatePicker txtto;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnLogout;
    @FXML
    private String id;
    @FXML
    Label lblStatus;
    @FXML
    Label attendancestatus;
    

    @FXML
    TableView tblData;

    /**
     * Initializes the controller class.
     */
    PreparedStatement preparedStatement;
    Connection connection;

    public StudentController() {
        connection = (Connection) ConnectionUtil.conDB();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    public void initData(String string) {
		id = string;
		System.out.println(id);
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
        if (txtfrom.getValue().equals(null) || txtto.getValue().equals(null)) {
            lblStatus.setTextFill(Color.TOMATO);
            lblStatus.setText("Select Dates");
        } else {
            saveData();
        }

    }

    private void clearFields() {
       
    }

    private String saveData() {

        String FSQL =" and DATE(Date) BETWEEN ? AND ?";
		SQL = SQL + FSQL;
		PSQL = SQL + FSQL;
		ASQL = ASQL + FSQL;
		LSQL = LSQL + FSQL;

		lblStatus.setTextFill(Color.GREEN);
		lblStatus.setText("Filtered Successfully");
		fetRowList();
		//clear fields
		clearFields();
		return "Success";
    }

    private ObservableList<ObservableList> data;
    String id1 = "";
    String SQL ="SELECT * FROM attendance WHERE id=?";
    String PSQL =  "SELECT COUNT(Status) FROM attendance WHERE Status= 'Present'";
    String ASQL =  "SELECT COUNT(Status) FROM attendance WHERE Status= 'Absent'";
    String LSQL =  "SELECT COUNT(Status) FROM attendance WHERE Status= 'Leave'";
    //only fetch columns
    private void fetColumnList() {
    	ResultSet rs;
        try {
        	if(txtfrom.getValue() == null) {
        		System.out.println(id);
            	preparedStatement = connection.prepareStatement(SQL);
            	preparedStatement.setString(1, id);
                rs = preparedStatement.executeQuery();
        	}
        	else {
        		System.out.println(id);
            	preparedStatement = connection.prepareStatement(SQL);
            	preparedStatement.setString(1, id);
            	preparedStatement.setString(2, txtfrom.getValue().toString());
                preparedStatement.setString(3, txtto.getValue().toString());
                rs = preparedStatement.executeQuery();
        	}
        	
 
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
        ResultSet rs;
         
        try {
        	if(txtfrom.getValue() == null) {
        		System.out.println(id);
            	preparedStatement = connection.prepareStatement(SQL);
            	preparedStatement.setString(1, id);
                rs = preparedStatement.executeQuery();
                ResultSet p = connection.createStatement().executeQuery(ASQL);
                p.next();
                System.out.println(p.getInt(1));
        	}
        	else {
        		System.out.println(id);
            	preparedStatement = connection.prepareStatement(SQL);
            	preparedStatement.setString(1, id);
            	preparedStatement.setString(2, txtfrom.getValue().toString());
                preparedStatement.setString(3, txtto.getValue().toString());
                rs = preparedStatement.executeQuery();
               
        	}

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
