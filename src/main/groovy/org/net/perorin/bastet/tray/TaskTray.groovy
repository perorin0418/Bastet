package org.net.perorin.bastet.tray

import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.image.BufferedImage
import java.util.concurrent.Executors

import javax.imageio.ImageIO

import org.net.perorin.bastet.util.Util
import org.net.perorin.bastet.window.Window

class TaskTray {

	static boolean show = false

	static TrayIcon trayIcon
	static SystemTray systemTray = SystemTray.getSystemTray();

	static def taskTray() {

		if (!SystemTray.isSupported()) {
			return;
		}

		BufferedImage img = ImageIO.read(Util.getResourceURL("img/icon/mythology.png"));
		trayIcon = new TrayIcon(img);
		trayIcon.setImageAutoSize(true);
		trayIcon.addActionListener({
			Window.show = true
			TaskTray.show = false
			new Object().notifyAll()
		});
		trayIcon.setToolTip("show/hide");

		def service = Executors.newSingleThreadExecutor()
		service.execute {
			while(true) {
				if(TaskTray.show) {
					if(!systemTray.getTrayIcons().contains(trayIcon)) {
						systemTray.add(trayIcon)
					}
				}else {
					if(systemTray.getTrayIcons().contains(trayIcon)) {
						systemTray.remove(trayIcon)
					}
				}
				Thread.sleep(1000)
			}
		}
		service.shutdown()
	}
}
