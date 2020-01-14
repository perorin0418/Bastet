package org.net.perorin.bastet.plugins.timemanage.controller

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import org.net.perorin.bastet.data.WorkData
import org.net.perorin.bastet.plugins.timemanage.parts.EditDialog
import org.net.perorin.bastet.util.SmoothishScrollpane
import org.net.perorin.bastet.util.SqlUtil
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
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject

import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
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
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.util.Duration

class TimeManageController {

	// 時計
	@FXML JFXSpinner hour // 時
	@FXML JFXSpinner minute // 分
	@FXML JFXSpinner second // 秒
	@FXML Label day // 日
	@FXML Label weekAndMonth // 月
	@FXML Label clockLabel // 現在時間

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
	def agendaEntryList = []

	// テーブル
	@FXML AnchorPane tablePane
	ObservableList<WorkTableData> workDatas

	@FXML
	def initialize(){
		initClock()
		initWork()
		initAgenda()
		initTable()
		onPanelChanged()
	}

	def initClock() {

		day.setFont(Font.font("源ノ角ゴシック JP Normal", 64))
		weekAndMonth.setFont(Font.font("源ノ角ゴシック JP Normal", 18))
		clockLabel.setFont(Font.font("源ノ角ゴシック JP Normal", 18))
		double hourCurrent = 0
		double minCurrent = 0
		double secCurrent = 0
		Timeline clock = new Timeline(1d, new KeyFrame(Duration.ZERO, {
			Calendar c = Calendar.getInstance()
			double hourProgress = Double.parseDouble(new SimpleDateFormat("hh").format(c.getTime())) * (100 / 12) / 100
			double minProgress = Double.parseDouble(new SimpleDateFormat("mm").format(c.getTime())) * (100 / 60) / 100
			double secProgress = Double.parseDouble(new SimpleDateFormat("ss").format(c.getTime())) * (100 / 60) / 100

			if(hourCurrent != hourProgress) {
				if(Util.getConfig().animation.clock.smooth) {
					new Timeline(30d,
							new KeyFrame(
							Duration.ZERO,
							new KeyValue(hour.progressProperty(), hourCurrent)
							),
							new KeyFrame(
							Duration.millis(100),
							new KeyValue(hour.progressProperty(), hourProgress)
							)
							).play()
				}else {
					hour.setProgress(hourProgress)
				}
				hourCurrent = hourProgress

				// 時間毎にテーブルを更新
				onPanelChanged()
			}

			if(minCurrent != minProgress) {
				if(Util.getConfig().animation.clock.smooth) {
					new Timeline(30d,
							new KeyFrame(
							Duration.ZERO,
							new KeyValue(minute.progressProperty(), minCurrent)
							),
							new KeyFrame(
							Duration.millis(100),
							new KeyValue(minute.progressProperty(), minProgress)
							)
							).play()
				}else {
					minute.setProgress(minProgress)
				}
				minCurrent = minProgress
			}

			if(secCurrent != secProgress) {
				if(Util.getConfig().animation.clock.smooth) {
					new Timeline(30d,
							new KeyFrame(
							Duration.ZERO,
							new KeyValue(second.progressProperty(), secCurrent)
							),
							new KeyFrame(
							Duration.millis(100),
							new KeyValue(second.progressProperty(), secProgress)
							)
							).play()
				}else {
					second.setProgress(secProgress)
				}
				secCurrent = secProgress
			}

			day.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd", new Locale("en","US"))))
			weekAndMonth.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("E, MMM", new Locale("en","US"))))
			clockLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")))
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
		workFrom.getEditor().setFont(Font.font("源ノ角ゴシック JP Normal", 14))
		workTo = new JFXTimePicker()
		workTo.setLayoutX(190)
		workTo.setLayoutY(20)
		workTo.setPrefWidth(120)
		workTo.setDefaultColor(Paint.valueOf("#2ea9df"))
		workTo.getEditor().setFont(Font.font("源ノ角ゴシック JP Normal", 14))
		workArea.getChildren().addAll(0, workFrom, workTo)

		workTitle.setFont(Font.font("源ノ角ゴシック JP Normal", 14))
		workKind.setStyle("-fx-font: 14 '源ノ角ゴシック JP Normal'");
		workDetail.setFont(Font.font("源ノ角ゴシック JP Normal", 14))

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

			// 予定表の設定

			// 開始時刻
			LocalTime startTime = null
			if(workFrom.getValue() != null) {
				startTime = workFrom.getValue()
			}else if(agendaEntryList.empty) {
				startTime = dayView.getStartTime()
			}else {
				startTime = agendaEntryList.stream()
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
			agendaEntryList << entry
			datasetAgenda()

			// テーブルの設定
			WorkTableData data = new WorkTableData(
					workTitle.getText(),
					workKind.getValue(),
					workDetail.getText(),
					DateTimeFormatter.ofPattern("H:mm").format(startTime),
					DateTimeFormatter.ofPattern("H:mm").format(endTime))
			data.metaClass.jobCode = workKind.getValue().jobCode
			data.metaClass.jobName = workKind.getValue().jobName
			data.metaClass.jobKind = workKind.getValue().jobKind
			data.metaClass.jobAlias = workKind.getValue().jobAlias
			workDatas << data
			putDatabase()
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
		agendaEntryList.forEach({
			it.setCalendar(dayView.getCalendarSources().first().getCalendars().first())
		})

		// TODO 時間を変更できるようにする
		// 開始時刻設定
		dayView.setStartTime(LocalTime.of(8, 30))
		dayView.setEndTime(LocalTime.of(17, 30))

		dayView.setStyle("-fx-background-color: transparent;")

		// 透明の四角形を重ねてクリックできないようにする。
		agendaPane.setCenter(new StackPane(dayView, new Rectangle(248, 1680, Color.TRANSPARENT)))
	}

	def initTable() {

		JFXTreeTableColumn<WorkTableData, String> titleCol = new JFXTreeTableColumn<>("タイトル")
		Util.addTooltipToColumnCells(titleCol)
		titleCol.setCellValueFactory({param -> param.getValue().getValue().title})
		titleCol.setPrefWidth(185)
		JFXTreeTableColumn<WorkTableData, String> workCol = new JFXTreeTableColumn<>("作業種類")
		Util.addTooltipToColumnCells(workCol)
		workCol.setCellValueFactory({param -> param.getValue().getValue().work})
		workCol.setPrefWidth(185)
		JFXTreeTableColumn<WorkTableData, String> startCol = new JFXTreeTableColumn<>("開始時間")
		Util.addTooltipToColumnCells(startCol)
		startCol.setCellValueFactory({param -> param.getValue().getValue().start})
		startCol.setPrefWidth(78)
		JFXTreeTableColumn<WorkTableData, String> endCol = new JFXTreeTableColumn<>("終了時間")
		Util.addTooltipToColumnCells(endCol)
		endCol.setCellValueFactory({param -> param.getValue().getValue().end})
		endCol.setPrefWidth(78)
		JFXTreeTableColumn<WorkTableData, String> editCol = new JFXTreeTableColumn<>("")
		editCol.setPrefWidth(55)
		editCol.setCellFactory({
			return new TreeTableCell<WorkTableData, String>() {

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
								btn.setOnAction( {
									EditDialog.showEditDialog(
											tablePane.getScene().getWindow(),
											toWorkData(workDatas.get(getIndex()), new WorkData()), { WorkData wd ->

												// テーブルの更新
												workDatas.get(getIndex()).title.set(wd.title)
												workDatas.get(getIndex()).work.set(wd.work)
												workDatas.get(getIndex()).detail.set(wd.detail)
												workDatas.get(getIndex()).start.set(wd.start)
												workDatas.get(getIndex()).end.set(wd.end)
												putDatabase()

												// 予定表の更新
												agendaEntryList.get(getIndex()).setTitle(wd.title)
												LocalTime startTime = LocalTime.ofInstant(new SimpleDateFormat("H:mm").parse(wd.start).toInstant(), ZoneId.systemDefault())
												LocalTime endTime = LocalTime.ofInstant(new SimpleDateFormat("H:mm").parse(wd.end).toInstant(), ZoneId.systemDefault())
												agendaEntryList.get(getIndex()).setInterval(startTime, endTime)
												datasetAgenda()
											})
								});
								setGraphic(btn);
								setText(null);
							}
						}
					}
		})
		JFXTreeTableColumn<WorkTableData, String> delCol = new JFXTreeTableColumn<>("")
		delCol.setPrefWidth(55)
		delCol.setCellFactory({
			return new TreeTableCell<WorkTableData, String>() {

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
								btn.setOnAction( {

									// テーブルの更新
									workDatas.remove(getIndex())
									putDatabase()

									// 予定表の更新
									agendaEntryList.get(getIndex()).setCalendar(null)
									agendaEntryList.remove(getIndex())
									datasetAgenda()
								});
								setGraphic(btn);
								setText(null);
							}
						}
					}
		})

		workDatas = FXCollections.observableArrayList();
		TreeItem<WorkTableData> root = new RecursiveTreeItem<WorkTableData>(workDatas, {it -> it.getChildren()})
		JFXTreeTableView<WorkTableData> table = new JFXTreeTableView<>()
		table.getColumns().addAll(titleCol, workCol, startCol, endCol, editCol, delCol)
		table.setRoot(root)
		table.setShowRoot(false)
		table.setLayoutX(10)
		table.setLayoutY(10)
		table.setPrefWidth(660)
		table.setPrefHeight(220)
		Label placeHolder = new Label("no data")
		placeHolder.setFont(Font.font("源ノ角ゴシック JP Normal", 16))
		placeHolder.setTextFill(Paint.valueOf("#a5dee4"))
		table.setPlaceholder(placeHolder)
		tablePane.getChildren().add(table)
	}

	def putDatabase() {
		Calendar cal = Calendar.getInstance()
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd")
		def list = []
		workDatas.each {
			def obj = new Object()
			obj.metaClass.Date = sdf.format(cal.getTime())
			obj.metaClass.WorkTitle = it.title.get()
			obj.metaClass.WorkDetail = it.detail.get()
			obj.metaClass.WorkStart = it.start.get()
			obj.metaClass.WorkEnd = it.end.get()
			obj.metaClass.JobCode = it.jobCode
			obj.metaClass.JobName = it.jobName
			obj.metaClass.JobKind = it.jobKind
			obj.metaClass.JobAlias = it.jobAlias
			list << obj
		}
		SqlUtil.addWorkData(list)
	}

	def onPanelChanged() {

		// 作業種類の更新
		def list = FXCollections.observableArrayList()
		SqlUtil.getJobData().each {
			if(it.alias == "") {
				if(it.kind != "") {
					def data = "[${it.kind}]${it.name}"
					data.metaClass.jobCode = "${it.code}"
					data.metaClass.jobName = "${it.name}"
					data.metaClass.jobKind = "${it.kind}"
					data.metaClass.jobAlias = "${it.alias}"
					list << data
				}else {
					def data = "${it.name}"
					data.metaClass.jobCode = "${it.code}"
					data.metaClass.jobName = "${it.name}"
					data.metaClass.jobKind = "${it.kind}"
					data.metaClass.jobAlias = "${it.alias}"
					list << data
				}
			}
		}
		workKind.setItems(list)

		agendaEntryList.clear()
		workDatas.clear()
		def workList = SqlUtil.getWorkData().each {

			LocalTime startTime = LocalTime.ofInstant(new SimpleDateFormat("H:mm").parse(it.workStart).toInstant(), ZoneId.systemDefault())
			LocalTime endTime = LocalTime.ofInstant(new SimpleDateFormat("H:mm").parse(it.workEnd).toInstant(), ZoneId.systemDefault())

			// 予定表の更新
			Entry<String> entry = new Entry()
			entry.setTitle(it.workTitle)
			entry.setInterval(startTime, endTime)
			agendaEntryList << entry
			datasetAgenda()

			// テーブルの設定
			def workKind
			if(it.jobAlias == "") {
				if(it.jobKind != "") {
					workKind = "[${it.jobKind}]${it.jobName}"
				}else {
					workKind = "${it.jobName}"
				}
			}
			WorkTableData data = new WorkTableData(
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

		// TODO 現在時刻に合わせて予定表のスクロール位置を調整する。
	}

	static WorkData toWorkData(WorkTableData wtd, WorkData wd){
		wd.title = wtd.title.get()
		wd.work = wtd.work.get()
		wd.detail = wtd.detail.get()
		wd.start = wtd.start.get()
		wd.end = wtd.end.get()
		return wd
	}

	static WorkTableData toWorkTableData(WorkData work, WorkTableData wtd) {
		wtd.title.set(work.title)
		wtd.work.set(work.work)
		wtd.detail.set(work.detail)
		wtd.start.set(work.start)
		wtd.end.set(work.end)
		return wtd
	}

	private class WorkTableData extends RecursiveTreeObject<WorkTableData> {
		StringProperty title
		StringProperty work
		StringProperty detail
		StringProperty start
		StringProperty end

		public WorkTableData(String title, String work, String detail, String start, String end) {
			this.title = new SimpleStringProperty(title)
			this.work = new SimpleStringProperty(work)
			this.detail = new SimpleStringProperty(detail)
			this.start = new SimpleStringProperty(start)
			this.end = new SimpleStringProperty(end)
		}
	}

}
