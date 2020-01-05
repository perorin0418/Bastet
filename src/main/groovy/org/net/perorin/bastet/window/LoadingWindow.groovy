package org.net.perorin.bastet.window;

import java.security.SecureRandom
import java.util.logging.Logger

import org.net.perorin.bastet.util.Util

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle

class LoadingWindow {

	static ThreadLocal<Integer> tl = new ThreadLocal<Integer>() {

		@Override
		protected Integer initialValue() {
			SecureRandom r = SecureRandom.getInstance("SHA1PRNG");
			return new Integer(r.nextInt());
		}
	}

	static def loadingMap = [:]

	static def showLoadingWindow(Stage owner, String title, def closure) {
		FXMLLoader loader = new FXMLLoader(Util.getResourceURL("fxml/LoadingWindow.fxml"));
		loader.load();
		Parent root = loader.getRoot();

		LoadingController controller = loader.getController();
		controller.start(closure)
		controller.setTitle(title)

		Scene scene = new Scene(root);
		scene.getStylesheets().add(Util.getResourceStr("css/application.css"));
		scene.setFill(Color.TRANSPARENT)

		Stage dialog = new Stage(StageStyle.UTILITY);
		controller.setStage(dialog)
		dialog.setX(owner.getX() + owner.getWidth()/2 - 100)
		dialog.setY(owner.getY() + owner.getHeight()/2 - 150)
		dialog.initStyle(StageStyle.TRANSPARENT);
		dialog.setScene(scene);
		dialog.initOwner(owner);
		dialog.setResizable(false);
		dialog.show()
	}

	static def appendTextLine(String text) {
		LoadingWindow.appendText(text + "\n")
	}

	static def appendText(String text) {
		if(loadingMap[tl.get()] != null) {
			Logger.getLogger("").info "[${loadingMap[tl.get()]}]${text.replace("\n","")}"
			loadingMap[tl.get()].appendText(text)
		}
	}
}
