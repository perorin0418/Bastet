package org.net.perorin.bastet.window;

import java.util.concurrent.Executors

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextArea

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.stage.Stage
import javafx.stage.Window

class LoadingController {

	boolean isExpand = false
	@FXML Label loading
	@FXML JFXButton expand
	@FXML JFXTextArea logArea

	@FXML
	def initialize() {
	}

	@FXML
	def expand_onMouseClicked() {
		if(isExpand) {
			expand.setText("＞")
			Window w = expand.getScene().getWindow()
			w.setHeight(w.getHeight() - 200)
		}else {
			expand.setText("＜")
			Window w = expand.getScene().getWindow()
			w.setHeight(w.getHeight() + 200)
		}
		isExpand = !isExpand
	}

	def start(def closure) {
		def self = this
		def service = Executors.newSingleThreadExecutor()
		service.execute {
			LoadingWindow.loadingMap[LoadingWindow.tl.get()] = self
			closure()
			Platform.runLater({((Stage)expand.getScene().getWindow()).close()})
			LoadingWindow.loadingMap.remove(LoadingWindow.tl.get())
		}
		service.shutdown()
	}

	def setMessage(String message) {
		loading.setText(message)
	}

	def appendText(String text) {
		logArea.appendText(text)
	}
}
