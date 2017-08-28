package com.quickveggies.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.quickveggies.misc.CryptDataHandler;
import com.quickveggies.controller.CustomDialogController;
import com.quickveggies.controller.LoginController;
import com.quickveggies.controller.SessionDataController;
import com.quickveggies.CosmeticStyles;
import com.quickveggies.GeneralMethods;
import com.quickveggies.dao.DatabaseClient;
import com.quickveggies.Main;
import com.quickveggies.UserGlobalParameters;
import com.quickveggies.entities.User;
import java.io.IOException;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ComboBox;
import java.util.List;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.SingleSelectionModel;
public class LoginController implements Initializable {

    @FXML
    private PasswordField pass;

    @FXML
    private TextField username;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink linkToProfileSetting;
    @FXML
    private Hyperlink register;
    @FXML
    private ComboBox sqlProfile;
    public void initialize(URL location, ResourceBundle resources) {
        try {
            UserGlobalParameters.load();
           List<UserGlobalParameters.DataBaseInformation> databasesProfile =  UserGlobalParameters.getStoredDatabaseInformations();
           List<String> profileNames = new ArrayList<String>();
           for (UserGlobalParameters.DataBaseInformation info : databasesProfile) {
                profileNames.add(info.getProfileName());
            }
           sqlProfile.setItems(FXCollections.observableArrayList(profileNames));
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        CosmeticStyles.addHoverEffect(loginButton);
      //  sqlserver.setText("localhost");  
      
        //check if there is already a user logged into the database
        //if so, let the user to unregister and let another user register the program to his name
      
        linkToProfileSetting.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new Main().replaceSceneContent("/databaseProfileSetting.fxml"); //To change body of generated methods, choose Tools | Templates.
            }
        });
              
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (!username.getText().equals("") && !pass.getText().equals("")) {
                	
//                	UserGlobalParameters.SQLURL="jdbc:sqlserver://localhost\\ROOT;instanceName=ROOT;databaseName=qvdb;integratedSecurity=true";
//                	UserGlobalParameters.SQLINSTANCE="ROOT";
//                	UserGlobalParameters.SQLUSER="root";
//                	UserGlobalParameters.SQLPASS="1234";
                    SingleSelectionModel<String> profile =   sqlProfile.getSelectionModel();
                    UserGlobalParameters.DataBaseInformation condiateProfile = null;
                    for (UserGlobalParameters.DataBaseInformation information : UserGlobalParameters.getStoredDatabaseInformations()) {
                        if(information.getProfileName().equals(profile.getSelectedItem())) {
                            condiateProfile = information;
                            break;
                        }
                    }
                	  DatabaseClient dbclient=DatabaseClient.getInstance(condiateProfile);
                	if(UserGlobalParameters.SQLURL.equals(""))UserGlobalParameters.SQLURL="localhost";
                	if(UserGlobalParameters.SQLUSER.equals(""))UserGlobalParameters.SQLUSER="root";
                	if(UserGlobalParameters.SQLPASS.equals(""))UserGlobalParameters.SQLPASS="1234";
                	
                	
                	if(dbclient==null){GeneralMethods.errorMsg("Wrong SQL server address/login parameters"); return;}
                	
                	 try {
                         User user = dbclient.getUserByName(username.getText());
                         String password = pass.getText();
                         if (password.equals(CryptDataHandler.getInstance().decrypt(user.getPassword()))) {
                             SessionDataController.getInstance().setCurrentUser(user);
                             //add a check to prevent closing the dash with unsaved windows still open
//                             ((Stage)loginButton.getScene().getWindow()).setOnCloseRequest(new EventHandler<WindowEvent>() {
//                            	    @Override
//                            	    public void handle(WindowEvent event) {
//                            	       if(SessionDataController.getInstance().getUnsavedWindows()>0)
//                            	    	   //TODO add here a ok-cancel confirmation dialog
//                            	    	   event.consume();
//                            	    }
//                            	});
                             new Main().replaceSceneContent("/dashboardz.fxml");
                         } else {
                             throw new NoSuchElementException();
                         }

                 } catch (SQLException e) {
                     e.printStackTrace();
                 } catch (NoSuchElementException e){
                     Alert alert = new Alert(Alert.AlertType.WARNING);
                     alert.setTitle("Error!");
                     alert.setHeaderText(null);
                     alert.setContentText("Wrong login \\ password!");
                     alert.showAndWait();
                 }
                }else GeneralMethods.errorMsg("Not all fields were properly filled!");

            }
        });
    }
}
