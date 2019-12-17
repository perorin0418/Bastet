package org.net.perorin.bastet.window

import org.net.perorin.bastet.util.Util

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle

class Window extends Application {

	@Override
	public void start(Stage primaryStage) {

		FXMLLoader loader = new FXMLLoader(Util.getResourceURL("fxml/Window.fxml"))
		Parent root = (Parent)loader.load()

		WindowController controller = loader.getController()
		controller.setStage(primaryStage)

		Scene scene = new Scene(root);
		scene.getStylesheets().add(Util.getResourceStr("css/application.css"));
		scene.setFill(Color.TRANSPARENT)

		primaryStage.getIcons().add(new Image(Util.getResourceStr("img/icon/mythology.png")))
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setTitle("Bastet")
		primaryStage.setScene(scene);
		primaryStage.setOnShown({
			controller.handleWindowShowEvent()
		})
		primaryStage.show()

	}
}
