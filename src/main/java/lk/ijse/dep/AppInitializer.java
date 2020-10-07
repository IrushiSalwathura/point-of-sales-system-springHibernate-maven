package lk.ijse.dep;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.ijse.dep.db.HibernateUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * AppInitializer is the main class of this pos system
 * @author Irushi Salwathura
 */

public class AppInitializer extends Application {
    /**
     * This is the starting point of the application
     * There is nothing to do just run the class
     * @params args No need to add parameters
     */
    public static void main(String[] args) {

        launch(args);
        HibernateUtil.getSessionFactory().close();

    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainForm.fxml"));
            Scene mainScene = new Scene(root);
            primaryStage.setScene(mainScene);
            primaryStage.setTitle("POS-System");
            primaryStage.centerOnScreen();
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
