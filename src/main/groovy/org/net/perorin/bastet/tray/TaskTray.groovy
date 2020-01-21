package org.net.perorin.bastet.tray

import java.awt.Font
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.TrayIcon.MessageType
import java.util.concurrent.Executors

import javax.imageio.ImageIO

import org.net.perorin.bastet.util.Util
import org.net.perorin.bastet.window.Window

import javafx.application.Platform

class TaskTray {

	static boolean show = false

	static TrayIcon trayIcon
	static SystemTray systemTray = SystemTray.getSystemTray();

	static def taskTray() {

		if (!SystemTray.isSupported()) {
			return
		}

		PopupMenu popup = new PopupMenu()
		trayIcon = new TrayIcon(ImageIO.read(Util.getResourceURL("img/icon/mythology.png")), "Bastet", popup)
		trayIcon.setImageAutoSize(true)
		trayIcon.addActionListener({
			Window.show = true
			TaskTray.show = false
		});

		MenuItem showHide = new MenuItem("　表示　")
		showHide.setFont(new Font("源ノ角ゴシック JP Normal", Font.PLAIN, 14))
		showHide.addActionListener({
			Window.show = true
			TaskTray.show = false
		})
		MenuItem exit = new MenuItem("　終了　")
		exit.setFont(new Font("源ノ角ゴシック JP Normal", Font.PLAIN, 14))
		exit.addActionListener({
			Platform.exit()
			System.exit(0)
		})
		popup.add(showHide)
		popup.add(exit)

		def service = Executors.newSingleThreadExecutor()
		service.execute {
			while(true) {
				if(TaskTray.show) {
					if(!systemTray.getTrayIcons().contains(trayIcon)) {
						systemTray.add(trayIcon)
						trayIcon.displayMessage("Bastet", "タスクトレイに縮小しました。", MessageType.INFO)
					}
				}else {
					if(systemTray.getTrayIcons().contains(trayIcon)) {
						systemTray.remove(trayIcon)
					}
				}
				Thread.sleep(500)
			}
		}
		service.shutdown()
	}
}
