package org.net.perorin.bastet.plugins.teamspirit.parts

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

import org.net.perorin.bastet.util.SqlUtil

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextArea

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Font
import javafx.stage.Stage

class DateConfirmController {

	Stage stage
	LocalDate date

	@FXML AnchorPane header
	@FXML Label title
	@FXML JFXButton closeButton
	@FXML JFXTextArea textarea
	@FXML JFXButton regist

	@FXML
	def initialize(){

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
		closeButton.setOnMouseClicked({
			stage.close()
			stage.hide()
		})

		textarea.setFont(Font.font("源ノ角ゴシック JP Normal", 14))
	}

	def onAfterShow() {
		totalyUpWork()
	}

	def totalyUpWork() {
		def totalList = [:]
		def workList = SqlUtil.getWorkData(date)
		for(def w : workList) {
			if(totalList[w.workTitle + w.jobCode + w.jobKind] != null) {
				def buf = totalList[w.workTitle + w.jobCode + w.jobKind]
				if(w.workDetail != "") {
					buf.detail += "\n" + w.workDetail
				}
				def start = LocalTime.ofInstant(new SimpleDateFormat("HH:mm").parse(w.workStart).toInstant(), ZoneId.systemDefault())
				def end = LocalTime.ofInstant(new SimpleDateFormat("HH:mm").parse(w.workEnd).toInstant(), ZoneId.systemDefault())
				buf.minute += ChronoUnit.MINUTES.between(start, end)
			}else {
				def buf = new Object()
				buf.metaClass.title = w.workTitle
				buf.metaClass.detail = w.workDetail
				def start = LocalTime.ofInstant(new SimpleDateFormat("HH:mm").parse(w.workStart).toInstant(), ZoneId.systemDefault())
				def end = LocalTime.ofInstant(new SimpleDateFormat("HH:mm").parse(w.workEnd).toInstant(), ZoneId.systemDefault())
				buf.metaClass.minute = ChronoUnit.MINUTES.between(start, end)
				totalList[w.workTitle + w.jobCode + w.jobKind] = buf
			}
		}
		def loader = new GroovyClassLoader()
		def template = loader.parseClass(new File("resource/template/DateConfirmTemplate.groovy")).newInstance()
		textarea.setText(template.getDateConfirmText(totalList.values(), totalList.values()))
		loader.clearCache()
	}

	public void setDate(LocalDate date) {
		this.date = date
		this.title.setText("日次確定 [" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "]")
	}

	private class Delta {
		double x, y;
	}

}
