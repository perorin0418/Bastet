package org.net.perorin.bastet.util;

import static com.codeborne.selenide.Condition.*
import static com.codeborne.selenide.Selenide.*

import org.net.perorin.bastet.data.JobData
import org.net.perorin.bastet.window.LoadingWindow
import org.openqa.selenium.By

import com.codeborne.selenide.Configuration
import com.codeborne.selenide.SelenideElement

class SelenideUtil {

	static def config() {

		// タイムアウトの時間
		Configuration.timeout = 30000

		// テスト実行後にブラウザを開いたままにする
		Configuration.holdBrowserOpen = true

		// 使用するブラウザ
		Configuration.browser = SqlUtil.getParameter("SelenideBrowser").Para1

		// 失敗時にHTMLを保存しない
		Configuration.savePageSource = false

		// 失敗時にpngを保存しない
		Configuration.screenshots = false

		// 高速入力
		Configuration.fastSetValue = true;

		// HeadLessモード指定
		Configuration.headless = true;
	}

	static def login() {
		SelenideUtil.config()
		LoadingWindow.appendTextLine("Selenide設定完了")
		LoadingWindow.appendTextLine("WebDriver起動中")
		open(SqlUtil.getParameter("TeamSpititURL").Para1)
		LoadingWindow.appendTextLine("WebDriver起動完了")
		LoadingWindow.appendTextLine("ログイン画面展開")

		String mailaddress = SqlUtil.getParameter("TeamSpiritMailAddress").Para1
		$("#username").setValue(mailaddress)
		$("#password").setValue(Util.decrypt(mailaddress, SqlUtil.getParameter("TeamSpiritPassword").Para1))
		$("#Login").click()
		LoadingWindow.appendTextLine("ログイン成功")
	}

	static def getAllJobCode() {
		SelenideUtil.config()

		// 勤務表タブタブクリック
		$(By.className("wt-勤務表")).waitUntil(visible, 10000).click()
		LoadingWindow.appendTextLine("勤務表表示")

		// 実績画面を開く
		SelenideElement ele = $(By.cssSelector("td.vjob div.png-add")).waitUntil(visible, 10000)
		while(!ele.exists()) {
			$('#nextMonthButton').waitUntil(visible, 10000).click()
			ele = $(By.cssSelector("td.vjob div.png-add")).waitUntil(visible, 10000)
		}
		ele.waitUntil(visible, 10000).click()
		LoadingWindow.appendTextLine("工数実績表示")

		// ジョブアサインをクリック
		$("#empWorkPlus").waitUntil(visible, 10000).click()
		LoadingWindow.appendTextLine("ジョブアサイン表示")

		// テーブルのtrの数を調べる
		int trSize = $("#empJobRightTable").waitUntil(visible, 10000).find(By.tagName("tbody")).findAll(By.tagName("tr")).size()
		LoadingWindow.appendTextLine("ジョブ数：${trSize}")

		int k = 10
		def jobDataList = []
		for(int i = 0; i < trSize; i++) {
			SelenideElement e = $("#empJobRightTableRow" + i).waitUntil(visible, 10000)
			if(e.exists()) {
				JobData jobData = new JobData()
				jobData.code = e.find(By.cssSelector("td :nth-child(2)")).text
				jobData.name = e.find(By.cssSelector("td :nth-child(3)")).text
				jobData.kind = e.find(By.cssSelector("td :nth-child(4)")).find(By.tagName("select")).getValue()
				jobDataList << jobData
				if( i != 0 && i % Math.floor(trSize / 10) == 0 ) {
					LoadingWindow.appendText("${k--}..")
				}
			}
		}
		LoadingWindow.appendTextLine("")

		// DBに登録
		SqlUtil.addJobData(jobDataList)
		LoadingWindow.appendTextLine("DBに登録")
	}

	static def dateConfirm(String text) {
		
	}


	static def logout() {
		LoadingWindow.appendTextLine("ログアウト")
		close()
	}

}
