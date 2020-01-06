package org.net.perorin.bastet.window

import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.image.BufferedImage

import javax.imageio.ImageIO

import org.net.perorin.bastet.util.Util

import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle

class Window extends Application {

	private TrayIcon trayIcon

	@Override
	public void start(Stage primaryStage) {
		Platform.setImplicitExit(false);

		FXMLLoader loader = new FXMLLoader(Util.getResourceURL("fxml/Window.fxml"))
		Parent root = (Parent)loader.load()

		WindowController controller = loader.getController()
		controller.setStage(primaryStage)

		Scene scene = new Scene(root);
		scene.getStylesheets().add(Util.getResourceStr("css/application.css"));
		scene.setFill(Color.TRANSPARENT)

		initSystemTray(primaryStage);
		primaryStage.getIcons().add(new Image(Util.getResourceStr("img/icon/mythology.png")))
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setTitle("Bastet")
		primaryStage.setScene(scene);
		primaryStage.setOnShown({
			controller.handleWindowShowEvent()
		})
		primaryStage.show()
	}

	def initSystemTray(Stage primaryStage) {

		if (!SystemTray.isSupported()) {
			return;
		}

		BufferedImage img = ImageIO.read(Util.getResourceURL("img/icon/mythology.png"));
		this.trayIcon = new TrayIcon(img);
		this.trayIcon.setImageAutoSize(true);
		this.trayIcon.addActionListener({
			Platform.runLater({
				primaryStage.show();
			});
		});
		this.trayIcon.setToolTip("show/hide");
		SystemTray systemTray = SystemTray.getSystemTray();
		systemTray.add(trayIcon);
	}
}
