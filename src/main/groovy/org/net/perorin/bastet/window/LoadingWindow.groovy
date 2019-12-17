package org.net.perorin.bastet.window;

import java.security.SecureRandom
import java.util.logging.Logger

import org.net.perorin.bastet.util.Util

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
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

	static def showLoadingWindow(Stage owner, String title, String message, def closure) {
		FXMLLoader loader = new FXMLLoader(Util.getResourceURL("fxml/LoadingWindow.fxml"));
		loader.load();
		Parent root = loader.getRoot();

		LoadingController controller = loader.getController();
		controller.start(closure)
		controller.setMessage(message)

		Scene scene = new Scene(root);
		Stage dialog = new Stage(StageStyle.UTILITY);
		dialog.setX(owner.getX() + owner.getWidth()/2 - 100)
		dialog.setY(owner.getY() + owner.getHeight()/2 - 150)
		dialog.setScene(scene);
		dialog.initOwner(owner);
		dialog.setResizable(false);
		dialog.setTitle(title);
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
