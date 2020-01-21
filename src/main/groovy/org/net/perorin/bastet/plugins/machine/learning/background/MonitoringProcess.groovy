package org.net.perorin.bastet.plugins.machine.learning.background

import java.util.concurrent.Executors

import org.net.perorin.bastet.data.MonitoringData
import org.net.perorin.bastet.util.SqlUtil

class MonitoringProcess {

	static def activeWindowMonitor() {
		MonitoringProcess.commonMonitor(".\\powershell\\ActiveWindow.ps1", 10, {obj ->
			MonitoringData md = new MonitoringData()
			md.kind = 0
			md.date = obj.date
			md.title = obj.title
			md.path = obj.path
			SqlUtil.addMonitoringData(md)
		})
	}

	static def explorerListMonitor() {
		MonitoringProcess.commonMonitor(".\\powershell\\ExplorerList.ps1", 10, {obj ->
			MonitoringData md = new MonitoringData()
			md.kind = 1
			md.date = obj.date
			md.title = obj.title
			md.path = obj.path
			SqlUtil.addMonitoringData(md)
		})
	}

	static def processStatusMonitor() {
		MonitoringProcess.commonMonitor(".\\powershell\\ProcessStatus.ps1", 60, {obj ->
			MonitoringData md = new MonitoringData()
			md.kind = 2
			md.date = obj.date
			md.title = obj.title
			md.path = obj.path
			SqlUtil.addMonitoringData(md)
		})
	}

	static def commonMonitor(String ps1, int sec, def insertClosure) {
		int pid = ProcessHandle.current().pid()
		def service = Executors.newSingleThreadExecutor()
		service.execute {
			def msg = []
			def proc = "powershell -NoProfile -ExecutionPolicy Unrestricted ${ps1} ${sec} ${pid}".execute()
			def thread = Thread.start{
				int mode = -1
				Object obj = new Object()
				obj.metaClass.date = ""
				obj.metaClass.title = ""
				obj.metaClass.path = ""
				proc.in.eachLine {
					if(it.contains("*****")) {
						if(mode > 0) {
							insertClosure(obj)
						}
						mode = 0
						obj.date = ""
						obj.title = ""
						obj.path = ""
					}else {
						switch(mode) {
							case 0:
								obj.date = it
								mode++
								break
							case 1:
								obj.title = it
								mode++
								break
							case 2:
								obj.path = it.replaceAll("\\\\", "/")
								mode++
								break
							default:
								break
						}
					}
				}
			}
		}
	}
}
