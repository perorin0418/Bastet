package org.net.perorin.bastet.data;

class JobData {
	String code = ""
	String name = ""
	String kind = ""
	String alias = ""

	@Override
	public String toString() {
		return "[code:${code}, name:${name}, kind:${kind}, alias:${alias}]"
	}
}
