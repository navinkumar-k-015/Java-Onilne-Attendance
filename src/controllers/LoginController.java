package controllers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import utils.ConnectionUtil;


public class LoginController implements Initializable {

    @FXML
    private Label lblErrors;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;

    @FXML
    private Button btnSignin;
    @FXML
    private ComboBox<String> txtid;

    /// -- 
    Connection con = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if (event.getSource() == btnSignin) {
            //login here
            if (logIn().equals("Success")) {
                try {

                    //add you loading or delays - ;-)
                    Node node = (Node) event.getSource();
                    Stage stage = (Stage) node.getScene().getWindow();
                    //stage.setMaximized(true);
                    stage.close();
                    if (txtid.getValue().toString().equals("Admin")) {
                    	System.out.println(txtid.getValue().toString());
                    	FXMLLoader loader = new FXMLLoader(
                    		    getClass().getResource("/fxml/Admin.fxml"));
                    	Scene scene = new Scene(loader.load());
                    	stage.setScene(scene);
                    	stage.show();
                    }
                    if(txtid.getValue().toString().equals("Faculty") ) {
                    	FXMLLoader loader = new FXMLLoader(
                    		    getClass().getResource("/fxml/Faculty.fxml"));
                    	Scene scene = new Scene(loader.load());
                    	stage.setScene(scene);
                    	FacultyController controller = loader.getController();
                        controller.initData(txtUsername.getText());
                    	stage.show();
                    }
                    if(txtid.getValue().toString().equals("Student")) {
                    	FXMLLoader loader = new FXMLLoader(
                    		    getClass().getResource("/fxml/Student.fxml"));
                    	Scene scene = new Scene(loader.load());
                    	stage.setScene(scene);
                    	StudentController controller = loader.getController();
                        controller.initData(txtUsername.getText());;
                    	stage.show();
                    }
                    
      
                    stage.show();

                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }

            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        if (con == null) {
            lblErrors.setTextFill(Color.TOMATO);
            lblErrors.setText("Server Error : Check");
        } else {
            lblErrors.setTextFill(Color.GREEN);
            lblErrors.setText("Server is up : Good to go");
        }
        txtid.getItems().addAll("Admin", "Faculty", "Student");
        txtid.getSelectionModel().select("Student");
    }

    public LoginController() {
        con = ConnectionUtil.conDB();
    }

    //we gonna use string to check for status
    private String logIn() {
        String status = "Success";
        String id = txtUsername.getText();
        String password = txtPassword.getText();
        if(id.isEmpty() || password.isEmpty()) {
            setLblError(Color.TOMATO, "Empty credentials");
            status = "Error";
        } else {
            //query
            String sql = "SELECT * FROM userdata Where idUserData = ? and password = ? and Type = ?";
            try {
                preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, txtid.getValue().toString());
                resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) {
                    setLblError(Color.TOMATO, "Enter Correct Email/Password");
                    status = "Error";
                } else {
                    setLblError(Color.GREEN, "Login Successful..Redirecting..");
                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
                status = "Exception";
            }
        }
        
        return status;
    }
    
    private void setLblError(Color color, String text) {
        lblErrors.setTextFill(color);
        lblErrors.setText(text);
        System.out.println(text);
    }
}
