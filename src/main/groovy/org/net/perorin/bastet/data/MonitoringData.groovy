package org.net.perorin.bastet.data

class MonitoringData {

	int kind
	String date
	String title
	String path

	@Override
	public String toString() {
		return "[kind:${kind},date:${date},title:${title},path:${path}";
	}
}
