package org.net.perorin.bastet.data

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

class WorkData extends RecursiveTreeObject<WorkData> {
	StringProperty title
	StringProperty work
	StringProperty detail
	StringProperty start
	StringProperty end

	public WorkData(String title, String work, String detail, String start, String end) {
		this.title = new SimpleStringProperty(title)
		this.work = new SimpleStringProperty(work)
		this.detail = new SimpleStringProperty(detail)
		this.start = new SimpleStringProperty(start)
		this.end = new SimpleStringProperty(end)
	}
}
