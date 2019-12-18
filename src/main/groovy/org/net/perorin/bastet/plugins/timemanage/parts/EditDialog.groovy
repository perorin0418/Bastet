package org.net.perorin.bastet.plugins.timemanage.parts

import org.net.perorin.bastet.util.Util

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle

class EditDialog {

	static def showEditDialog(Stage owner, String title, def closure) {
		FXMLLoader loader = new FXMLLoader(Util.getResourceURL("fxml/EditDialog.fxml"));
		loader.load();
		Parent root = loader.getRoot();

		EditDialogController controller = loader.getController();
		controller.onClose = closure
		controller.setTitle(title)

		Scene scene = new Scene(root);
		scene.getStylesheets().add(Util.getResourceStr("css/application.css"));
		scene.setFill(Color.TRANSPARENT)

		Stage dialog = new Stage(StageStyle.UTILITY);
		controller.stage = dialog

		dialog.setX(owner.getX() + owner.getWidth()/2 + 50)
		dialog.setY(owner.getY() + owner.getHeight()/2 + 50)
		dialog.initStyle(StageStyle.TRANSPARENT);
		dialog.setScene(scene);
		dialog.initOwner(owner);
		dialog.setResizable(false);
		dialog.show()
	}
}
