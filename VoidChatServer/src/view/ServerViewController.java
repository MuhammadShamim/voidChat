package view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.User;
/**
 * FXML Controller class
 *
 * @author Mostafa
 */
public class ServerViewController implements Initializable {

    @FXML
    private Tab WelcomTap;
    @FXML
    private Tab SendTab;
    @FXML

    private Button sendBtn;
    @FXML
    private TextArea announcement;
    @FXML
    private Button btnToggle;
    @FXML
    private GridPane gridView;
    @FXML
    private PieChart pieChart;
    @FXML
    private AnchorPane userContent;

    @FXML
    private TextField username;

    @FXML
    private ServerView serverView;
    
    @FXML ImageView sponser ; 
    
    @FXML
    private Button SendButton;

    @FXML
    private ToggleButton start;


    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

       
            serverView = ServerView.getInstance();
            serverView.setServerViewController(this);
            
        try {
            //set sponser
            sponser.setImage(new Image(getClass().getResource("..//resources//Voidlogo.png").openStream()));
        } catch (IOException ex) {
            Logger.getLogger(ServerViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
            
            ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
                    new PieChart.Data("Online", 50),
                    new PieChart.Data("Offline", 30),
                    new PieChart.Data("Busy", 20));
            System.out.println("chart");
            pieChart.setData(data);
            pieChart.setLegendSide(Side.LEFT);
     
    }
    
    private void addTooltipToChartSlice(PieChart chart){
        double total = 0;
        for(PieChart.Data d : chart.getData()){
            total += d.getPieValue();
        }
        for(PieChart.Data d : chart.getData()){
            Node slice = d.getNode();
            double sliceValue = d.getPieValue();
            double precent = (sliceValue / total)* 100;
            
            String tip = d.getName() + "=" +String.format("%.2f", precent)+ "%";
            
            Tooltip tt = new Tooltip(tip);
            Tooltip.install(slice, tt);
        }
    }


       

    

    @FXML
    private void ToggleButtonAction(ActionEvent event) {
        System.out.println("serverViewController");

        if (start.isSelected()) {
            System.out.println("serverViewController start");
            start.setText("Stop");
            serverView.startServer();
        } else {
            start.setText("Start");
            System.out.println("serverViewController stop");
            serverView.stopServer();
        }

    }

    /**
     * get user data from data base
     */
    public void getUserData() {
        User user = serverView.getUserInfo(username.getText());
        username.setText("");
        if (user == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("no User with this username");
            alert.showAndWait();
        } else {

            try {
                userContent.getChildren().clear();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UserInfoView.fxml"));
                fxmlLoader.setController(new UserInfoController(user));
                Pane pane = fxmlLoader.load();
                userContent.getChildren().add(pane);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void sendAnnouncement() {
        serverView.sendAnnouncement(announcement.getText());
        announcement.setText("");
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Announcement send to all online users");
        alert.showAndWait();
    }
    
    public void setSponser(ActionEvent event){
        try {
            Stage st = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg")
            );
            
            //Show save file dialog
            File file = fileChooser.showOpenDialog(st);
            
            //no file choosen
            if (file == null) {
                return;
            }
            
            
            
            if(file.length() > 1024 * 1024){
                System.out.println("So big File");
                return;
            }
            
            sponser.setImage(new Image(file.toURI().toURL().toExternalForm()));
                
            //send to server controller
            FileInputStream in = new FileInputStream(file);
            byte[] data = new byte[1024 * 1024];
            int dataLength = in.read(data);

            serverView.sendSponser(data , dataLength);
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(ServerViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
