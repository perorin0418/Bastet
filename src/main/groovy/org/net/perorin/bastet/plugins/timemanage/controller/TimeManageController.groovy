package org.net.perorin.bastet.plugins.timemanage.controller

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

import org.net.perorin.bastet.data.WorkData
import org.net.perorin.bastet.plugins.timemanage.parts.EditDialog
import org.net.perorin.bastet.util.SmoothishScrollpane
import org.net.perorin.bastet.util.Util

import com.calendarfx.model.Entry
import com.calendarfx.view.DayView
import com.calendarfx.view.TimeScaleView
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXComboBox
import com.jfoenix.controls.JFXSpinner
import com.jfoenix.controls.JFXTextArea
import com.jfoenix.controls.JFXTextField
import com.jfoenix.controls.JFXTimePicker
import com.jfoenix.controls.JFXTreeTableColumn
import com.jfoenix.controls.JFXTreeTableView
import com.jfoenix.controls.RecursiveTreeItem
import com.jfoenix.controls.JFXButton.ButtonType

import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextFormatter
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableCell
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.util.Duration

class TimeManageController {

	// 時計
	@FXML JFXSpinner hour // 時
	@FXML JFXSpinner minute // 分
	@FXML JFXSpinner second // 秒
	@FXML Label day // 日
	@FXML Label weekAndMonth // 月

	// 作業記録
	@FXML AnchorPane workArea
	JFXTimePicker workFrom
	JFXTimePicker workTo
	@FXML JFXTextField workTitle
	@FXML JFXComboBox workKind
	@FXML JFXTextArea workDetail
	@FXML JFXButton workRec

	// 予定表
	@FXML ScrollPane agendaScroll
	@FXML BorderPane agendaPane
	DayView dayView
	def entryList = []

	// テーブル
	@FXML AnchorPane tablePane
	ObservableList<WorkData> workDatas

	@FXML
	def initialize(){
		initClock()
		initWork()
		initAgenda()
		initTable()
	}

	def initClock() {

		day.setFont(Font.loadFont(Util.getResourceStr("font/SourceHanSansJP-Normal.otf"), 64))
		weekAndMonth.setFont(Font.loadFont(Util.getResourceStr("font/SourceHanSansJP-Normal.otf"), 18))
		double hourCurrent = 0
		double minCurrent = 0
		double secCurrent = 0
		Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, {
			Calendar c = Calendar.getInstance()
			double hourProgress = Double.parseDouble(new SimpleDateFormat("hh").format(c.getTime())) * (100 / 12) / 100
			double minProgress = Double.parseDouble(new SimpleDateFormat("mm").format(c.getTime())) * (100 / 60) / 100
			double secProgress = Double.parseDouble(new SimpleDateFormat("ss").format(c.getTime())) * (100 / 60) / 100

			if(hourCurrent != hourProgress) {
				new Timeline(
						new KeyFrame(
						Duration.ZERO,
						new KeyValue(hour.progressProperty(), hourCurrent)
						),
						new KeyFrame(
						Duration.millis(100),
						new KeyValue(hour.progressProperty(), hourProgress)
						)
						).play()
				hourCurrent = hourProgress
			}

			if(minCurrent != minProgress) {
				new Timeline(
						new KeyFrame(
						Duration.ZERO,
						new KeyValue(minute.progressProperty(), minCurrent)
						),
						new KeyFrame(
						Duration.millis(100),
						new KeyValue(minute.progressProperty(), minProgress)
						)
						).play()
				minCurrent = minProgress
			}

			if(secCurrent != secProgress) {
				new Timeline(
						new KeyFrame(
						Duration.ZERO,
						new KeyValue(second.progressProperty(), secCurrent)
						),
						new KeyFrame(
						Duration.millis(100),
						new KeyValue(second.progressProperty(), secProgress)
						)
						).play()
				secCurrent = secProgress
			}

			day.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd", new Locale("en","US"))))
			weekAndMonth.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("E, MMM", new Locale("en","US"))))
		}),new KeyFrame(Duration.seconds(1)));
		clock.setCycleCount(Animation.INDEFINITE);
		clock.play();
	}

	def initWork() {

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

			if(workTitle.getText() == "" || workKind.getValue() == null || workKind.getValue() == "") {
				return
			}

			// 開始時刻
			LocalTime startTime = null
			if(workFrom.getValue() != null) {
				startTime = workFrom.getValue()
			}else if(entryList.empty) {
				startTime = dayView.getStartTime()
			}else {
				startTime = entryList.stream()
						.map { it.getEndTime() }
						.sorted({it1, it2 -> it2.toSecondOfDay() - it1.toSecondOfDay()})
						.collect().first()
			}

			// 終了時刻
			LocalTime endTime = null
			if(workTo.getValue() != null) {
				endTime = workTo.getValue()
			}else {
				endTime = LocalTime.now()
			}

			Entry<String> entry = new Entry()
			entry.setTitle(workTitle.getText())
			entry.setInterval(startTime, endTime)
			entryList << entry

			datasetAgenda()
		})
	}

	def initAgenda() {

		SmoothishScrollpane.setSmoothish(agendaScroll)
		agendaScroll.setVvalue(0.57)

		TimeScaleView tsv = new TimeScaleView();
		tsv.setStartTime(LocalTime.of(8, 30))
		tsv.setEndTime(LocalTime.of(17, 30))

		agendaPane.setLeft(tsv)

		datasetAgenda()
	}

	def datasetAgenda() {

		agendaPane.setCenter(null)

		// 時間割
		dayView = new DayView()
		entryList.forEach({
			it.setCalendar(dayView.getCalendarSources().first().getCalendars().first())
		})

		// コンテキストメニューを削除
		dayView.setContextMenuCallback({})
		dayView.setEntryContextMenuCallback({})
		dayView.setEntryDetailsCallback({})
		dayView.setEntryDetailsPopOverContentCallback({})
		dayView.setDateDetailsCallback({})

		// TODO 時間を変更できるようにする
		// 開始時刻設定
		dayView.setStartTime(LocalTime.of(8, 30))
		dayView.setEndTime(LocalTime.of(17, 30))

		dayView.setStyle("-fx-background-color: transparent;")

		agendaPane.setCenter(dayView)
	}

	def initTable() {


		JFXTreeTableColumn<WorkData, String> titleCol = new JFXTreeTableColumn<>("タイトル")
		titleCol.setCellValueFactory({param -> param.getValue().getValue().title})
		titleCol.setPrefWidth(190)
		JFXTreeTableColumn<WorkData, String> workCol = new JFXTreeTableColumn<>("作業種類")
		workCol.setCellValueFactory({param -> param.getValue().getValue().work})
		workCol.setPrefWidth(190)
		JFXTreeTableColumn<WorkData, String> startCol = new JFXTreeTableColumn<>("開始時間")
		startCol.setCellValueFactory({param -> param.getValue().getValue().start})
		startCol.setPrefWidth(78)
		JFXTreeTableColumn<WorkData, String> endCol = new JFXTreeTableColumn<>("終了時間")
		endCol.setCellValueFactory({param -> param.getValue().getValue().end})
		endCol.setPrefWidth(78)
		JFXTreeTableColumn<WorkData, String> editCol = new JFXTreeTableColumn<>("")
		editCol.setCellFactory({
			return new TreeTableCell<WorkData, String>() {

						final JFXButton btn = new JFXButton("編集");

						@Override
						public void updateItem(String item, boolean empty) {
							super.updateItem(item, empty);
							if (empty) {
								setGraphic(null);
								setText(null);
							} else {
								btn.setRipplerFill(Paint.valueOf("#a5dee4"))
								btn.setTextFill(Paint.valueOf("#2ea9df"))
								btn.setButtonType(ButtonType.FLAT)
								btn.setStyle("-fx-background-color: #08192D;")
								btn.setOnAction( {event ->
									EditDialog.showEditDialog(tablePane.getScene().getWindow(), workDatas.get(getIndex()).title.get(), {
										println "fuga"
									})
								});
								setGraphic(btn);
								setText(null);
							}
						}
					}
		})
		JFXTreeTableColumn<WorkData, String> delCol = new JFXTreeTableColumn<>("")
		delCol.setCellFactory({
			return new TreeTableCell<WorkData, String>() {

						final JFXButton btn = new JFXButton("削除");

						@Override
						public void updateItem(String item, boolean empty) {
							super.updateItem(item, empty);
							if (empty) {
								setGraphic(null);
								setText(null);
							} else {
								btn.setRipplerFill(Paint.valueOf("#a5dee4"))
								btn.setTextFill(Paint.valueOf("#2ea9df"))
								btn.setButtonType(ButtonType.FLAT)
								btn.setStyle("-fx-background-color: #08192D;")
								btn.setOnAction( {event ->
									workDatas.remove(getIndex())
								});
								setGraphic(btn);
								setText(null);
							}
						}
					}
		})

		workDatas = FXCollections.observableArrayList();
		50.times {
			WorkData hoge = new WorkData("title${it}", "work${it}", "", "${it}:00", "${it}:00")
			workDatas.addAll(hoge)
		}
		TreeItem<WorkData> root = new RecursiveTreeItem<WorkData>(workDatas, {it -> it.getChildren()})
		JFXTreeTableView<WorkData> table = new JFXTreeTableView<>()
		table.getColumns().addAll(titleCol, workCol, startCol, endCol, editCol, delCol)
		table.setRoot(root)
		table.setShowRoot(false)
		table.setLayoutX(10)
		table.setLayoutY(10)
		table.setPrefWidth(660)
		table.setPrefHeight(220)
		tablePane.getChildren().add(table)
	}
}
