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
import com.quickveggies.controller.DataBaseProfileSettingController;
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
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ComboBox;
import java.util.List;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
public class DataBaseProfileSettingController implements Initializable {

    
    
    @FXML
    private Button addProfile;
    @FXML
    private Button removeProfile;
    @FXML
    private Hyperlink returnToLogin;
  
    @FXML
    private TableView<DatabaseAdapter> profileCollection; 
    
    @FXML
    private TableColumn profileName;
    @FXML
    private TableColumn password;
    @FXML
    private TableColumn host;
    @FXML
    private TableColumn port;
    @FXML
    private TableColumn userName;
    @FXML
    private TableColumn instanceName;
    @FXML
    private TableColumn dataBaseName;
   
    public static class DatabaseAdapter {
        private SimpleStringProperty profileName;
        private SimpleStringProperty master;
        private SimpleStringProperty host;
        private SimpleStringProperty port;
        private SimpleStringProperty userName;
        private SimpleStringProperty password;
        private SimpleStringProperty instanceName;
        private SimpleStringProperty dataBaseName;
        public UserGlobalParameters.DataBaseInformation getDataBaseInfo() {
            return new UserGlobalParameters.DataBaseInformation(profileName.get(), Boolean.parseBoolean(master.get()), host.get(), port.get(), userName.get(), password.get(), instanceName.get(), dataBaseName.get());
        }
        public DatabaseAdapter(UserGlobalParameters.DataBaseInformation database) {
            this.profileName = new SimpleStringProperty(database.getProfileName());
            this.master = new SimpleStringProperty(database.getMaster().toString());
            this.host = new SimpleStringProperty(database.getHost());
            this.port = new SimpleStringProperty(database.getPort());
            this.userName = new SimpleStringProperty(database.getLogin());
            this.password = new SimpleStringProperty(database.getPassword());
            this.instanceName = new SimpleStringProperty(database.getInstance());
            this.dataBaseName = new SimpleStringProperty(database.getDatabaseName());
        }
        /**
         * @return the profileName
         */
        public String getProfileName() {
            return profileName.get();
        }

        /**
         * @param profileName the profileName to set
         */
        public void setProfileName(String profileName) {
            this.profileName.set(profileName);
        }

        /**
         * @return the master
         */
        public String getMaster() {
            return master.get();
        }

        /**
         * @param master the master to set
         */
        public void setMaster(String master) {
            this.master.set(master);
        }

        /**
         * @return the host
         */
        public String getHost() {
            return host.get();
        }

        /**
         * @param host the host to set
         */
        public void setHost(String host) {
            this.host.set(host);
        }

        /**
         * @return the port
         */
        public String getPort() {
            return port.get();
        }

        /**
         * @param port the port to set
         */
        public void setPort(String port) {
            this.port.set(port);
        }

        /**
         * @return the userName
         */
        public String getUserName() {
            return userName.get();
        }

        /**
         * @param userName the userName to set
         */
        public void setUserName(String userName) {
            this.userName.set(userName);
        }

        /**
         * @return the password
         */
        public String getPassword() {
            return this.password.get();
        }

        /**
         * @param password the password to set
         */
        public void setPassword(String password) {
            this.password.set(password);
        }

        /**
         * @return the instanceName
         */
        public String getInstanceName() {
            return this.instanceName.get();
        }

        /**
         * @param instanceName the instanceName to set
         */
        public void setInstanceName(String instanceName) {
            this.instanceName.set(instanceName);
        }

        /**
         * @return the dataBaseName
         */
        public String getDataBaseName() {
            return dataBaseName.get();
        }

        /**
         * @param dataBaseName the dataBaseName to set
         */
        public void setDataBaseName(String dataBaseName) {
            this.dataBaseName.set(dataBaseName);
        }
        
        
    }
   
    public void initialize(URL location, ResourceBundle resources) {
        addProfile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UserGlobalParameters.DataBaseInformation information = new UserGlobalParameters.DataBaseInformation("New Database profile "+new Date().toLocaleString(),false,"127.0.0.1","1433","sa","password","MSSQLEXPRESS","QVDB");
                profileCollection.getItems().add(new DatabaseAdapter(information));
                        }
        });
        removeProfile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UserGlobalParameters.DataBaseInformation information = new UserGlobalParameters.DataBaseInformation("New Database profile1",false,"127.0.0.1","1433","sa","password","MSSQLEXPRESS","QVDB");
                profileCollection.getItems().remove(profileCollection.getSelectionModel().getSelectedItem());
                        }
        });
        profileCollection.setEditable(true);
        profileName.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<DatabaseAdapter, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<DatabaseAdapter, String> t) {
                    ((DatabaseAdapter) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                        ).setProfileName(t.getNewValue());
                }
            }
        );
        password.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<DatabaseAdapter, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<DatabaseAdapter, String> t) {
                    ((DatabaseAdapter) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                        ).setPassword(t.getNewValue());
                }
            }
        );
        host.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<DatabaseAdapter, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<DatabaseAdapter, String> t) {
                    ((DatabaseAdapter) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                        ).setHost(t.getNewValue());
                }
            }
        );
        port.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<DatabaseAdapter, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<DatabaseAdapter, String> t) {
                    ((DatabaseAdapter) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                        ).setPort(t.getNewValue());
                }
            }
        );
        userName.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<DatabaseAdapter, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<DatabaseAdapter, String> t) {
                    ((DatabaseAdapter) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                        ).setUserName(t.getNewValue());
                }
            }
        );
        instanceName.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<DatabaseAdapter, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<DatabaseAdapter, String> t) {
                    ((DatabaseAdapter) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                        ).setInstanceName(t.getNewValue());
                }
            }
        );
        dataBaseName.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<DatabaseAdapter, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<DatabaseAdapter, String> t) {
                    ((DatabaseAdapter) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                        ).setDataBaseName(t.getNewValue());
                }
            }
        );
        profileName.setCellFactory(TextFieldTableCell.forTableColumn());
        profileName.setCellValueFactory(new PropertyValueFactory<DatabaseAdapter,String>("profileName"));
        password.setCellValueFactory(new PropertyValueFactory<DatabaseAdapter,String>("password"));
        password.setCellFactory(TextFieldTableCell.forTableColumn());
        host.setCellValueFactory(new PropertyValueFactory<DatabaseAdapter,String>("host"));
        host.setCellFactory(TextFieldTableCell.forTableColumn());
        port.setCellValueFactory(new PropertyValueFactory<DatabaseAdapter,String>("port"));
        port.setCellFactory(TextFieldTableCell.forTableColumn());
        userName.setCellValueFactory(new PropertyValueFactory<DatabaseAdapter,String>("userName"));
        userName.setCellFactory(TextFieldTableCell.forTableColumn());
        instanceName.setCellValueFactory(new PropertyValueFactory<DatabaseAdapter,String>("instanceName"));
        instanceName.setCellFactory(TextFieldTableCell.forTableColumn());
        dataBaseName.setCellValueFactory(new PropertyValueFactory<DatabaseAdapter,String>("dataBaseName"));
        dataBaseName.setCellFactory(TextFieldTableCell.forTableColumn());
        List<DatabaseAdapter> currentDatabases = new ArrayList<DatabaseAdapter>();
        try {
            UserGlobalParameters.load();
          //  profileCollection.getItems().clear();
            List<UserGlobalParameters.DataBaseInformation> profiles =  UserGlobalParameters.getStoredDatabaseInformations();
            for(UserGlobalParameters.DataBaseInformation info : profiles) {
                currentDatabases.add(new  DatabaseAdapter((info)));
            }
        } catch (IOException ex) {
            Logger.getLogger(DataBaseProfileSettingController.class.getName()).log(Level.SEVERE, null, ex);
        }
        profileCollection.setItems(FXCollections.observableArrayList(currentDatabases));
      
        returnToLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final ObservableList<DatabaseAdapter> items = profileCollection.getItems();
                List<UserGlobalParameters.DataBaseInformation> toStore = new ArrayList<UserGlobalParameters.DataBaseInformation>();
                for (DatabaseAdapter adapter : items) {
                    toStore.add(adapter.getDataBaseInfo());
                }
                try {
                    UserGlobalParameters.storeDataBasesProfiles(toStore);
                } catch (IOException ex) {
                    Logger.getLogger(DataBaseProfileSettingController.class.getName()).log(Level.SEVERE, null, ex);
                }
                new Main().replaceSceneContent("/login.fxml"); //To change body of generated methods, choose Tools | Templates.
            }
        });
        
    }
    
    
    
}
