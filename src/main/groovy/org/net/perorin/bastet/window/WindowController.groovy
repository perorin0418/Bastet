package org.net.perorin.bastet.window

import org.net.perorin.bastet.tray.TaskTray
import org.net.perorin.bastet.util.Util

import com.jfoenix.controls.JFXButton

import javafx.animation.FadeTransition
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.util.Duration

class WindowController {

	def bodyMap = [:]
	Stage stage

	// ヘッダー
	@FXML AnchorPane header

	// サイドパネル
	@FXML VBox side
	@FXML JFXButton timeManage
	@FXML JFXButton jobSetting
	@FXML JFXButton teamSpirit
	@FXML JFXButton dashBoard
	@FXML JFXButton machineLearning
	@FXML JFXButton tasks

	@FXML ImageView timeManage_img
	@FXML ImageView jobSetting_img
	@FXML ImageView teamSpirit_img
	@FXML ImageView dashBoard_img
	@FXML ImageView machineLearning_img
	@FXML ImageView tasks_img

	// ボディ
	@FXML AnchorPane body

	// 閉じるボタン
	@FXML JFXButton closeButton

	@FXML
	def initialize() {

		initWindow()
		initSide()

	}

	def initBody() {
		body.setTopAnchor(bodyMap[timeManage.bodyName()], 10.0);
		body.setLeftAnchor(bodyMap[timeManage.bodyName()], 12.0);
		body.getChildren().add(bodyMap[timeManage.bodyName()])
	}

	def initWindow() {

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
			stage.hide()
			TaskTray.show = true
			Window.show = false
			while(true) {
				if(Window.show) {
					stage.show()
					break
				}
				Thread.sleep(1000)
			}
		})

		timeManage_img.setImage(new Image(Util.getResourceStr("img/apply/Programming-Watch-icon.png")))
		jobSetting_img.setImage(new Image(Util.getResourceStr("img/apply/Very-Basic-News-icon.png")))
		teamSpirit_img.setImage(new Image(Util.getResourceStr("img/custom/teamspirit.png")))
		dashBoard_img.setImage(new Image(Util.getResourceStr("img/apply/Transport-Speedometer-icon.png")))
		machineLearning_img.setImage(new Image(Util.getResourceStr("img/apply/Healthcare-Brain-icon.png")))
		tasks_img.setImage(new Image(Util.getResourceStr("img/apply/Business-Todo-List-icon.png")))
	}

	def initSide() {
		timeManage.metaClass.bodyName = {"TimeManage"}
		jobSetting.metaClass.bodyName = {"JobSetting"}
		teamSpirit.metaClass.bodyName = {"TeamSpirit"}
		dashBoard.metaClass.bodyName = {"DashBoard"}
		machineLearning.metaClass.bodyName = {"MachineLearning"}
		tasks.metaClass.bodyName = {"Tasks"}

		timeManage.setFont(Font.font("源ノ角ゴシック JP Normal", 24))
		jobSetting.setFont(Font.font("源ノ角ゴシック JP Normal", 22))
		teamSpirit.setFont(Font.font("源ノ角ゴシック JP Normal", 20))
		dashBoard.setFont(Font.font("源ノ角ゴシック JP Normal", 19))
		machineLearning.setFont(Font.font("源ノ角ゴシック JP Normal", 24))
		tasks.setFont(Font.font("源ノ角ゴシック JP Normal", 24))

		def list = [timeManage, jobSetting, teamSpirit, dashBoard, machineLearning, tasks]
		list.each({button ->
			FXMLLoader loader = new FXMLLoader(Util.getResourceURL("fxml/" + button.bodyName() + ".fxml"));
			bodyMap[button.bodyName()] = loader.load()
			def controller = loader.getController()
			button.setOnMouseClicked({
				changeBody(button.bodyName())
				controller.onPanelChanged()
			})
		})
	}

	def changeBody(def windowName) {

		// 現在のウィンドウを取得
		def before = body.getChildren()[0]

		// 現在のウィンドウをフェードアウト
		FadeTransition beforeTransition = new FadeTransition()
		beforeTransition.setDuration(Duration.millis(150))
		beforeTransition.setNode(before)
		beforeTransition.setFromValue(1)
		beforeTransition.setToValue(0)

		// フェードアウト完了後の処理
		beforeTransition.setOnFinished({

			// 現在のウィンドウをクリア
			body.getChildren().clear()

			// 変更後のウィンドウ
			def after  = bodyMap[windowName]

			// 変更後のボディの位置を設定
			body.setTopAnchor(after, 10.0);
			body.setLeftAnchor(after, 12.0);

			// 変更後のウィンドウをボディに設定
			body.getChildren().add(after)

			// 変更後のウィンドウをフェードイン
			FadeTransition afterTransition = new FadeTransition()
			afterTransition.setDuration(Duration.millis(150))
			afterTransition.setNode(after)
			afterTransition.setFromValue(0)
			afterTransition.setToValue(1)
			afterTransition.play()
		})
		beforeTransition.play()
	}

	private class Delta {
		double x, y;
	}

}
