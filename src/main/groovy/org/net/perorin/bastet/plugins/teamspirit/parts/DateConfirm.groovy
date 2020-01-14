package org.net.perorin.bastet.plugins.teamspirit.parts

import java.time.LocalDate

import org.net.perorin.bastet.util.Util

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle

class DateConfirm {

	static def showDateConfirm(Stage owner, LocalDate date) {
		FXMLLoader loader = new FXMLLoader(Util.getResourceURL("fxml/DateConfirm.fxml"))
		loader.load()
		Parent root = loader.getRoot()

		DateConfirmController controller = loader.getController()

		Scene scene = new Scene(root)
		scene.getStylesheets().add(Util.getResourceStr("css/application.css"))
		scene.setFill(Color.TRANSPARENT)

		Stage dialog = new Stage(StageStyle.UTILITY)
		controller.stage = dialog
		controller.setDate(date)
		controller.onAfterShow()

		dialog.setX(owner.getX() + owner.getWidth()/2 - 340)
		dialog.setY(owner.getY() + owner.getHeight()/2 - 268)
		dialog.initStyle(StageStyle.TRANSPARENT)
		dialog.setScene(scene)
		dialog.initOwner(owner)
		dialog.setResizable(false)
		dialog.metaClass.isForeground = false
		dialog.focusedProperty().addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> ov, Boolean onHidden, Boolean onShown) {
						if(onHidden) {
							dialog.isForeground = false
						}else if(onShown) {
							dialog.isForeground = true
						}
					}
				})
		dialog.show()
	}
}
