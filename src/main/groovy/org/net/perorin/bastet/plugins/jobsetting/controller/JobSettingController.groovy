package org.net.perorin.bastet.plugins.jobsetting.controller

import org.net.perorin.bastet.data.JobData
import org.net.perorin.bastet.util.SelenideUtil
import org.net.perorin.bastet.util.SqlUtil
import org.net.perorin.bastet.util.Util
import org.net.perorin.bastet.window.LoadingWindow

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXComboBox
import com.jfoenix.controls.JFXTextField
import com.jfoenix.controls.JFXTreeTableColumn
import com.jfoenix.controls.JFXTreeTableView
import com.jfoenix.controls.RecursiveTreeItem
import com.jfoenix.controls.JFXButton.ButtonType
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableCell
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Paint
import javafx.scene.text.Font

class JobSettingController {

	@FXML AnchorPane tableArea
	ObservableList<JobTableData> jobDatas

	@FXML Label jobLabel
	@FXML JFXTextField jobName
	@FXML JFXTextField jobCode
	@FXML JFXComboBox jobKind
	@FXML JFXButton jobRegist

	@FXML Label aliasLabel
	@FXML JFXTextField aliasName
	@FXML JFXComboBox jobSelect
	@FXML JFXButton aliasRegist

	@FXML Label teamSpiritLabel
	@FXML JFXButton teamSpiritBtn

	@FXML
	def initialize() {
		initTableArea()
		initJobArea()
		initAliasArea()
		initTeamSpiritArea()

		tableDataReload()
	}

	def initTableArea() {

		JFXTreeTableColumn<JobTableData, String> codeCol = new JFXTreeTableColumn<>("ジョブコード")
		Util.addTooltipToColumnCells(codeCol)
		codeCol.setCellValueFactory({param -> param.getValue().getValue().code})
		codeCol.setPrefWidth(100)
		JFXTreeTableColumn<JobTableData, String> nameCol = new JFXTreeTableColumn<>("ジョブ名")
		Util.addTooltipToColumnCells(nameCol)
		nameCol.setCellValueFactory({param -> param.getValue().getValue().name})
		nameCol.setPrefWidth(185)
		JFXTreeTableColumn<JobTableData, String> kindCol = new JFXTreeTableColumn<>("作業種類")
		Util.addTooltipToColumnCells(kindCol)
		kindCol.setCellValueFactory({param -> param.getValue().getValue().kind})
		kindCol.setPrefWidth(90)
		JFXTreeTableColumn<JobTableData, String> aliasCol = new JFXTreeTableColumn<>("エイリアス")
		Util.addTooltipToColumnCells(aliasCol)
		aliasCol.setCellValueFactory({param -> param.getValue().getValue().alias})
		aliasCol.setPrefWidth(185)
		JFXTreeTableColumn<JobTableData, String> delCol = new JFXTreeTableColumn<>("")
		delCol.setCellFactory({
			return new TreeTableCell<JobTableData, String>() {

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
									JobTableData job = jobDatas.get(getIndex())
									JobData rmJob = new JobData()
									rmJob.code = job.code.get()
									rmJob.name = job.name.get()
									rmJob.kind = job.kind.get()
									rmJob.alias = job.alias.get()
									SqlUtil.removeJobData(rmJob)
									jobDatas.remove(getIndex())
									tableDataReload()
								});
								setGraphic(btn);
								setText(null);
							}
						}
					}
		})
		jobDatas = FXCollections.observableArrayList();
		TreeItem<JobTableData> root = new RecursiveTreeItem<JobTableData>(jobDatas, {it -> it.getChildren()})
		JFXTreeTableView<JobTableData> table = new JFXTreeTableView<>()
		table.getColumns().addAll(codeCol, nameCol, kindCol, aliasCol, delCol)
		table.setRoot(root)
		table.setShowRoot(false)
		table.setLayoutX(10)
		table.setLayoutY(10)
		table.setPrefWidth(660)
		table.setPrefHeight(740)
		Label placeHolder = new Label("no data")
		placeHolder.setFont(Font.font("源ノ角ゴシック JP Normal", 16))
		placeHolder.setTextFill(Paint.valueOf("#a5dee4"))
		table.setPlaceholder(placeHolder)
		tableArea.getChildren().add(table)
	}

	def initJobArea() {

		jobLabel.setFont(Font.font("源ノ角ゴシック JP Normal", 24))
		jobLabel.setTextFill(Paint.valueOf("#2ea9df"))
		jobName.setFont(Font.font("源ノ角ゴシック JP Normal", 14))
		jobCode.setFont(Font.font("源ノ角ゴシック JP Normal", 14))

		jobKind.setStyle("-fx-font: 14 '源ノ角ゴシック JP Normal'");
		def list = []
		def alf = 'A'
		26.times {
			100.times{
				list << "${alf}-${String.format('%02d', it)}"
			}
			alf++
		}
		jobKind.setItems(FXCollections.observableArrayList(list))

		jobRegist.setFont(Font.font("源ノ角ゴシック JP Normal", 14))
	}

	def initAliasArea() {

		aliasLabel.setFont(Font.font("源ノ角ゴシック JP Normal", 24))
		aliasLabel.setTextFill(Paint.valueOf("#2ea9df"))
		aliasName.setFont(Font.font("源ノ角ゴシック JP Normal", 14))
		jobSelect.setStyle("-fx-font: 14 '源ノ角ゴシック JP Normal'");
		aliasRegist.setFont(Font.font("源ノ角ゴシック JP Normal", 14))
	}

	def initTeamSpiritArea() {

		teamSpiritLabel.setFont(Font.font("源ノ角ゴシック JP Normal", 24))
		teamSpiritLabel.setTextFill(Paint.valueOf("#2ea9df"))
		teamSpiritBtn.setFont(Font.font("源ノ角ゴシック JP Normal", 24))
		teamSpiritBtn.setOnMouseClicked({
			LoadingWindow.showLoadingWindow(teamSpiritBtn.getScene().getWindow(), "ジョブ取得中", {
				SelenideUtil.login()
				SelenideUtil.getAllJobCode()
				SelenideUtil.logout()
				LoadingWindow.appendTextLine("10秒後にこのウィンドウは")
				LoadingWindow.appendTextLine("自動的に閉じられます。")
				tableDataReload()
				Thread.sleep(10000)
			})
		})
	}

	def tableDataReload() {
		def list = []
		jobDatas.clear()
		SqlUtil.getJobData().each {
			jobDatas << new JobTableData(it.code, it.name, it.kind, it.alias)
			if(it.alias == "") {
				if(it.kind != "") {
					list << "[${it.kind}]${it.name}"
				}else {
					list << "${it.name}"
				}
			}
		}
		jobSelect.setItems(FXCollections.observableArrayList(list))
	}

	private class JobTableData extends RecursiveTreeObject<JobTableData> {
		StringProperty code
		StringProperty name
		StringProperty kind
		StringProperty alias

		public JobTableData(String code, String name, String kind, String alias) {
			this.code = new SimpleStringProperty(code)
			this.name = new SimpleStringProperty(name)
			this.kind = new SimpleStringProperty(kind)
			this.alias = new SimpleStringProperty(alias)
		}
	}
}
