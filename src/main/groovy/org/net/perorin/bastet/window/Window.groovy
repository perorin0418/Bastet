package org.net.perorin.bastet.window

import java.awt.SplashScreen
import java.util.concurrent.Executors

import org.net.perorin.bastet.tray.TaskTray
import org.net.perorin.bastet.util.Util

import javafx.application.Application
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle

class Window extends Application {

	static boolean show = true
	static boolean stopSleep = false

	@Override
	public void start(Stage primaryStage) {

		// ラベルの文字を強制アンチエイリアシングする
		System.setProperty("prism.lcdtext", "false");

		// スプラッシュを削除
		SplashScreen splash = SplashScreen.getSplashScreen();
		if(splash != null) {
			splash.close();
		}

		// タスクトレイ用のスレッド起動
		TaskTray.taskTray()

		// ウィンドウ非表示でもJavaFX Threadが終了しないように設定
		Platform.setImplicitExit(false);

		FXMLLoader loader = new FXMLLoader(Util.getResourceURL("fxml/Window.fxml"))
		Parent root = (Parent)loader.load()

		WindowController controller = loader.getController()
		controller.setStage(primaryStage)

		Scene scene = new Scene(root);
		scene.getStylesheets().add(Util.getResourceStr("css/application.css"));
		scene.setFill(Color.TRANSPARENT)

		primaryStage.getIcons().add(new Image(Util.getResourceStr("img/icon/mythology.png")))
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setTitle("Bastet")
		primaryStage.setScene(scene);
		primaryStage.metaClass.isForeground = false
		primaryStage.focusedProperty().addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> ov, Boolean onHidden, Boolean onShown) {
						if(onHidden) {
							primaryStage.isForeground = false
						}else if(onShown) {
							primaryStage.isForeground = true
						}
					}
				})
		primaryStage.show()

		controller.initBody()

		def service = Executors.newSingleThreadExecutor()
		service.execute {
			while(true) {
				if(!stopSleep) {
					Platform.runLater({

						// 表示中のウィンドウが無いか判定
						boolean active = Stage.getWindows()
								.stream()
								.filter({it.metaClass.hasProperty(it, "isForeground") != null})
								.map({it.isForeground})
								.reduce({it1, it2 ->
									it1 | it2
								})
								.get()
						if(!active) {
							Thread.sleep(200)
						}
					})
				}
				Thread.sleep(100)
			}
		}
		service.shutdown()
	}
}
