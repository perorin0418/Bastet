package org.net.perorin.bastet.plugins.teamspirit.parts

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

import org.net.perorin.bastet.plugins.timemanage.parts.WorkDataTable
import org.net.perorin.bastet.plugins.timemanage.parts.WorkDataTable_Data
import org.net.perorin.bastet.util.SqlUtil

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextArea
import com.jfoenix.controls.JFXTreeTableView

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Font
import javafx.stage.Stage

class DailyReportController {

	Stage stage
	LocalDate date

	@FXML AnchorPane header
	@FXML Label title
	@FXML JFXButton closeButton
	@FXML AnchorPane workArea
	@FXML JFXTextArea textarea
	@FXML JFXButton refresh
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

		refresh.setOnMouseClicked({
			totalyUpWork()
		})

		regist.setOnMouseClicked({
			println "hoge"
		})
	}

	def initTable() {
		JFXTreeTableView<WorkDataTable_Data> table = WorkDataTable.createWorkDataTable({workArea.getScene().getWindow()})
		table.setLayoutX(10)
		table.setLayoutY(432)
		table.setPrefWidth(660)
		table.setPrefHeight(220)
		workArea.getChildren().add(table)

		def workDatas = table.workDatas
		workDatas.clear()
		def workList = SqlUtil.getWorkData(date).each {

			// テーブルの設定
			def workKind
			if(it.jobAlias == "") {
				if(it.jobKind != "") {
					workKind = "[${it.jobKind}]${it.jobName}"
				}else {
					workKind = "${it.jobName}"
				}
			}
			WorkDataTable_Data data = new WorkDataTable_Data(
					it.workTitle,
					workKind,
					it.workDetail,
					it.workStart,
					it.workEnd)
			data.metaClass.jobCode = it.jobCode
			data.metaClass.jobName = it.jobName
			data.metaClass.jobKind = it.jobKind
			data.metaClass.jobAlias = it.jobAlias
			workDatas << data
		}
	}

	def onAfterShow() {
		totalyUpWork()
		initTable()
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
				buf.metaClass.jobName = w.jobName
				buf.metaClass.jobCode = w.jobCode
				buf.metaClass.jobKind = w.jobKind
				totalList[w.workTitle + w.jobCode + w.jobKind] = buf
			}
		}
		def loader = new GroovyClassLoader()
		def template = loader.parseClass(new File("resource/template/DailyReportTemplate.groovy")).newInstance()
		textarea.setText(template.getDateConfirmText(totalList.values(), totalList.values()))
		loader.clearCache()
	}

	public void setDate(LocalDate date) {
		this.date = date
		this.title.setText("日報入力 [" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "]")
	}

	private class Delta {
		double x, y;
	}

}
