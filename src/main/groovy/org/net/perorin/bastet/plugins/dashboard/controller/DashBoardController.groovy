package org.net.perorin.bastet.plugins.dashboard.controller

import java.time.LocalDate

import com.jfoenix.controls.JFXDatePicker

import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.chart.PieChart
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Paint
import javafx.scene.text.Font

class DashBoardController {

	@FXML AnchorPane monitorPiePane
	@FXML PieChart monitorPieChart
	JFXDatePicker monitorDatePicker

	@FXML
	def initialize() {

		initMonitorPiePane()
	}

	def initMonitorPiePane() {

		monitorDatePicker = new JFXDatePicker()
		monitorDatePicker.setValue(LocalDate.now())
		monitorDatePicker.setLayoutX(10)
		monitorDatePicker.setLayoutY(10)
		monitorDatePicker.setPrefWidth(170)
		monitorDatePicker.setDefaultColor(Paint.valueOf("#2ea9df"))
		monitorDatePicker.getEditor().setFont(Font.font("源ノ角ゴシック JP Normal", 20))
		monitorDatePicker.getEditor().setAlignment(Pos.CENTER)
		monitorPiePane.getChildren().add(monitorDatePicker)
	}
}
