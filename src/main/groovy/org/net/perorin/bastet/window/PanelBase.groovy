package org.net.perorin.bastet.window

import javafx.fxml.FXMLLoader

interface PanelBase {

	String SideBarTitle
	String SideBarIcon
	FXMLLoader loader
	def controller

	def component();
	def controller();
}
