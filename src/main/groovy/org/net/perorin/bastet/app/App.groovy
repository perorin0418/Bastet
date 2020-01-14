package org.net.perorin.bastet.app

import java.awt.SplashScreen

import org.net.perorin.bastet.window.Window

class App {
	static void main(String[] args) {

		// ラベルの文字を強制アンチエイリアシングする
		System.setProperty("prism.lcdtext", "false");
		Window.launch(Window.class)

		SplashScreen splash = SplashScreen.getSplashScreen();
		if(splash != null) {
			splash.close();
		}

		// TODO メモリ使用量の調査
		//		Timer timer = new Timer()
		//		TimerTask task = new TimerTask() {
		//			public void run() {
		//				System.gc()
		//			}
		//		}
		//		timer.scheduleAtFixedRate(task, 60000, 3000)
	}
}
