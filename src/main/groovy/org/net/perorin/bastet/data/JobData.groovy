package org.net.perorin.bastet.data;

class JobData {
	String date = ""
	String code = ""
	String name = ""
	String kind = ""
	String alias = ""

	@Override
	public String toString() {
		return "[date:${date}, code:${code}, name:${name}, kind:${kind}, alias:${alias}]"
	}
}
