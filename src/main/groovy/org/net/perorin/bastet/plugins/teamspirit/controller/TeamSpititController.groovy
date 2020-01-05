package org.net.perorin.bastet.plugins.teamspirit.controller

import java.awt.Toolkit

import org.net.perorin.bastet.util.SqlUtil
import org.net.perorin.bastet.util.Util

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXPasswordField
import com.jfoenix.controls.JFXTextField

import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.control.Alert.AlertType
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.stage.DirectoryChooser
import javafx.stage.StageStyle

class TeamSpititController {

	@FXML Label accountLabel
	@FXML JFXTextField mailaddress
	@FXML JFXPasswordField password
	@FXML JFXButton accountRegist

	@FXML
	def initialize() {
		initAccount()
	}

	def initAccount() {

		accountLabel.setFont(Font.font("源ノ角ゴシック JP Normal", 24))
		accountLabel.setTextFill(Paint.valueOf("#2ea9df"))
		mailaddress.setFont(Font.font("源ノ角ゴシック JP Normal", 14))
		mailaddress.setText(SqlUtil.getParameter("TeamSpiritMailAddress").Para1 != null ?
				SqlUtil.getParameter("TeamSpiritMailAddress").Para1 : "")
		password.setFont(Font.font("源ノ角ゴシック JP Normal", 14))
		accountRegist.setFont(Font.font("源ノ角ゴシック JP Normal", 14))
		accountRegist.setOnMouseClicked({
			accountSave()
		})
	}

	def accountSave() {

		// 入力チェック
		if(mailaddress.getText() == "" || password.getText() == "") {
			return
		}

		// AES暗号鍵取得
		File aesKey = new File(SqlUtil.getParameter("AES-Key-Path").Para1)
		if(!aesKey.exists()) {

			// アラート表示
			Alert alert = new Alert( AlertType.NONE, "", ButtonType.OK,ButtonType.CANCEL )
			alert.initStyle(StageStyle.UTILITY)
			alert.setTitle("確認")
			alert.getDialogPane().setContentText("パスワードの暗号鍵を作成します。\n保存先を指定してください。")
			Toolkit.getDefaultToolkit().beep()
			ButtonType button = alert.showAndWait().orElse( ButtonType.CANCEL )
			if(button == ButtonType.OK) {

				// フォルダー選択表示
				DirectoryChooser fc = new DirectoryChooser();
				fc.setTitle("フォルダ選択");
				fc.setInitialDirectory(new File(System.getProperty("user.home")))
				File file = fc.showDialog(null);
				if (file != null) {
					File dir = new File(Util.execPowerShell("powershell\\FileSystemAccessRule.ps1", ["${file.getAbsolutePath()}"]))
					if(dir.exists()) {
						Util.createKey(dir)
					}
				}
			}
		}

		// メールアドレスをDBに登録
		SqlUtil.setParameter("TeamSpiritMailAddress", [mailaddress.getText()])

		// パスワードを暗号化
		def encryptoPass = Util.encrypt(mailaddress.getText(), password.getText())

		// 暗号化したパスワードをDBに登録
		SqlUtil.setParameter("TeamSpiritPassword", [encryptoPass])

		Alert alert = new Alert( AlertType.NONE, "", ButtonType.OK )
		alert.initStyle(StageStyle.UTILITY)
		alert.setTitle("情報")
		alert.getDialogPane().setContentText("アカウントの保存に成功しました。")
		Toolkit.getDefaultToolkit().beep()
		ButtonType button = alert.showAndWait()
	}
}
