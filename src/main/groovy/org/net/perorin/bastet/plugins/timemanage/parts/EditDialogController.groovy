package org.net.perorin.bastet.plugins.timemanage.parts

import org.net.perorin.bastet.util.Util

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXComboBox
import com.jfoenix.controls.JFXTextArea
import com.jfoenix.controls.JFXTextField
import com.jfoenix.controls.JFXTimePicker

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.TextFormatter
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.stage.Stage

class EditDialogController {

	Stage stage
	def onClose

	@FXML AnchorPane header
	@FXML Label title
	@FXML JFXButton closeButton

	@FXML AnchorPane workArea
	JFXTimePicker workFrom
	JFXTimePicker workTo
	@FXML JFXTextField workTitle
	@FXML JFXComboBox workKind
	@FXML JFXTextArea workDetail
	@FXML JFXButton workRec

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

		// ×ボタンで最小化
		closeButton.setOnMouseClicked({
			stage.close()
			stage.hide()
		})

		workFrom = new JFXTimePicker()
		workFrom.setLayoutX(25)
		workFrom.setLayoutY(20)
		workFrom.setPrefWidth(120)
		workFrom.setDefaultColor(Paint.valueOf("#2ea9df"))
		workFrom.getEditor().setFont(Font.loadFont(Util.getResourceStr("font/SourceHanSansJP-Normal.otf"), 14))

		workTo = new JFXTimePicker()
		workTo.setLayoutX(190)
		workTo.setLayoutY(20)
		workTo.setPrefWidth(120)
		workTo.setDefaultColor(Paint.valueOf("#2ea9df"))
		workTo.getEditor().setFont(Font.loadFont(Util.getResourceStr("font/SourceHanSansJP-Normal.otf"), 14))
		workArea.getChildren().addAll(0, workFrom, workTo)

		workTitle.setFont(Font.loadFont(Util.getResourceStr("font/SourceHanSansJP-Normal.otf"), 14))
		workKind.setStyle("-fx-font: 14 '源ノ角ゴシック JP Normal'");
		workDetail.setFont(Font.loadFont(Util.getResourceStr("font/SourceHanSansJP-Normal.otf"), 14))

		workDetail.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
					@Override
					public void handle(KeyEvent event) {
						if (event.getCode() == KeyCode.TAB) {
							if(!event.isShiftDown()) {
								workFrom.requestFocus()
							}
						}
					}
				});
		workDetail.setTextFormatter(new TextFormatter({
			String newStr = it.getText().replace("\t", "");
			it.setText(newStr);
			return it;
		}))

		workRec.setOnMouseClicked({
			onClose()
			stage.close()
			stage.hide()
		})
	}

	def setTitle(String title) {
		this.title.setText("編集［${title}］")
	}

	private class Delta {
		double x, y;
	}
}
