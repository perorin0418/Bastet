package org.net.perorin.bastet.plugins.timemanage.parts

import java.text.SimpleDateFormat

import org.net.perorin.bastet.data.WorkData
import org.net.perorin.bastet.util.SqlUtil
import org.net.perorin.bastet.util.Util

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTreeTableColumn
import com.jfoenix.controls.JFXTreeTableView
import com.jfoenix.controls.RecursiveTreeItem
import com.jfoenix.controls.JFXButton.ButtonType
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.Label
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableCell
import javafx.scene.paint.Paint
import javafx.scene.text.Font

class WorkDataTable {

	static def createWorkDataTable(def stage, def editClosure = {it1,it2 ->}, def delClosure = {it ->}) {

		ObservableList<WorkDataTable_Data> workDatas = null
		JFXTreeTableColumn<WorkDataTable_Data, String> titleCol = new JFXTreeTableColumn<>("タイトル")
		Util.addTooltipToColumnCells(titleCol)
		titleCol.setCellValueFactory({param -> param.getValue().getValue().title})
		titleCol.setPrefWidth(185)
		JFXTreeTableColumn<WorkDataTable_Data, String> workCol = new JFXTreeTableColumn<>("作業種類")
		Util.addTooltipToColumnCells(workCol)
		workCol.setCellValueFactory({param -> param.getValue().getValue().work})
		workCol.setPrefWidth(185)
		JFXTreeTableColumn<WorkDataTable_Data, String> startCol = new JFXTreeTableColumn<>("開始時間")
		Util.addTooltipToColumnCells(startCol)
		startCol.setCellValueFactory({param -> param.getValue().getValue().start})
		startCol.setPrefWidth(78)
		JFXTreeTableColumn<WorkDataTable_Data, String> endCol = new JFXTreeTableColumn<>("終了時間")
		Util.addTooltipToColumnCells(endCol)
		endCol.setCellValueFactory({param -> param.getValue().getValue().end})
		endCol.setPrefWidth(78)
		JFXTreeTableColumn<WorkDataTable_Data, String> editCol = new JFXTreeTableColumn<>("")
		editCol.setPrefWidth(55)
		editCol.setCellFactory({
			return new TreeTableCell<WorkDataTable_Data, String>() {

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
											stage(),
											toWorkData(workDatas.get(getIndex()), new WorkData()),
											{ WorkData wd ->

												// テーブルの更新
												workDatas.get(getIndex()).title.set(wd.title)
												workDatas.get(getIndex()).work.set(wd.work)
												workDatas.get(getIndex()).detail.set(wd.detail)
												workDatas.get(getIndex()).start.set(wd.start)
												workDatas.get(getIndex()).end.set(wd.end)
												putDatabase(workDatas)

												editClosure(getIndex(), wd)
											})
								});
								setGraphic(btn);
								setText(null);
							}
						}
					}
		})
		JFXTreeTableColumn<WorkDataTable_Data, String> delCol = new JFXTreeTableColumn<>("")
		delCol.setPrefWidth(55)
		delCol.setCellFactory({
			return new TreeTableCell<WorkDataTable_Data, String>() {

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
									putDatabase(workDatas)

									delClosure(getIndex())

								});
								setGraphic(btn);
								setText(null);
							}
						}
					}
		})

		workDatas = FXCollections.observableArrayList();
		TreeItem<WorkDataTable_Data> root = new RecursiveTreeItem<WorkDataTable_Data>(workDatas, {it -> it.getChildren()})
		JFXTreeTableView<WorkDataTable_Data> table = new JFXTreeTableView<>()
		table.getColumns().addAll(titleCol, workCol, startCol, endCol, editCol, delCol)
		table.setRoot(root)
		table.setShowRoot(false)
		Label placeHolder = new Label("no data")
		placeHolder.setFont(Font.font("源ノ角ゴシック JP Normal", 16))
		placeHolder.setTextFill(Paint.valueOf("#a5dee4"))
		table.setPlaceholder(placeHolder)
		table.metaClass.workDatas = workDatas

		return table
	}

	static def putDatabase(def workDatas) {
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

	static WorkData toWorkData(WorkDataTable_Data wtd, WorkData wd){
		wd.title = wtd.title.get()
		wd.work = wtd.work.get()
		wd.detail = wtd.detail.get()
		wd.start = wtd.start.get()
		wd.end = wtd.end.get()
		return wd
	}

	static WorkDataTable_Data toWorkTableData(WorkData work, WorkDataTable_Data wtd) {
		wtd.title.set(work.title)
		wtd.work.set(work.work)
		wtd.detail.set(work.detail)
		wtd.start.set(work.start)
		wtd.end.set(work.end)
		return wtd
	}
}

class WorkDataTable_Data extends RecursiveTreeObject<WorkDataTable_Data> {
	StringProperty title
	StringProperty work
	StringProperty detail
	StringProperty start
	StringProperty end

	public WorkDataTable_Data(String title, String work, String detail, String start, String end) {
		this.title = new SimpleStringProperty(title)
		this.work = new SimpleStringProperty(work)
		this.detail = new SimpleStringProperty(detail)
		this.start = new SimpleStringProperty(start)
		this.end = new SimpleStringProperty(end)
	}
}
