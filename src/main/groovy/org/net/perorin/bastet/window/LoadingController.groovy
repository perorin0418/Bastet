package org.net.perorin.bastet.window;

import java.util.concurrent.Executors

import org.net.perorin.bastet.util.Util

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextArea

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

class LoadingController {

	Stage stage

	@FXML Label title
	@FXML AnchorPane header
	@FXML JFXButton closeBtn

	@FXML ImageView loadingImg
	@FXML JFXTextArea logArea

	@FXML
	def initialize() {

		// ヘッダーをドラッグ移動できるようにする
		final Delta dragDelta = new Delta();
		header.setOnMousePressed({mouseEvent ->
			dragDelta.x = stage.getX() - mouseEvent.getScreenX();
			dragDelta.y = stage.getY() - mouseEvent.getScreenY();
		})
		header.setOnMouseDragged({mouseEvent ->
			stage.setX(mouseEvent.getScreenX() + dragDelta.x);
			stage.setY(mouseEvent.getScreenY() + dragDelta.y);
		})

		// ×ボタンで閉じる
		closeBtn.setOnMouseClicked({
			stage.close()
			stage.hide()
		})

		loadingImg.setImage(new Image(Util.getResourceStr("img/loading/loading.gif")))

	}

	def start(def closure) {
		def self = this
		def service = Executors.newSingleThreadExecutor()
		service.execute {
			LoadingWindow.loadingMap[LoadingWindow.tl.get()] = self
			closure()
			Platform.runLater({stage.close()})
			LoadingWindow.loadingMap.remove(LoadingWindow.tl.get())
		}
		service.shutdown()
	}

	def setStage(Stage stage) {
		this.stage = stage
	}

	def setTitle(String title) {
		this.title.setText(title)
	}

	def appendText(String text) {
		logArea.appendText(text)
	}

	private class Delta {
		double x, y;
	}
}
