import java.time.temporal.ChronoUnit

class DailyReportTemplate{

	/**
	 *
	 * @param plans :LinkedList<Object>
	 * plans.each{
	 *     it.title // 作業タイトル
	 *     it.jobName // ジョブ名称
	 *     it.jobCode // ジョブコード
	 *     it.jobKind // ジョブ種類
	 * }
	 * @param results :LinkedList<Object>
	 * plans.each{
	 *     it.title // 作業タイトル
	 *     it.detail // 作業詳細
	 *     it.minute // 作業時間
	 *     it.jobName // ジョブ名称
	 *     it.jobCode // ジョブコード
	 *     it.jobKind // ジョブ種類
	 * }
	 * @param tasks ＊未実装＊
	 * @return
	 */
	static def getDateConfirmText(def plans = [], def results = [], def tasks = []) {
		StringBuilder ret = new StringBuilder()
		ret.metaClass.al = { it ->
			ret.append(it.toString() + "\n")
		}
		ret.al("□:予定 ○:予定外 ■:完了 ▲:未完了 ×:未実施")
		ret.al("")
		ret.al("＜予定していた作業＞")
		plans.each {
			ret.al("□：${it.title}")
		}
		ret.al("")
		ret.al("＜作業の実績＞")
		results.each {
			ret.al("■：${it.title}（${it.minute}min）")
			it.detail.split("\n").each { it2 ->
				ret.al("　　${it2}")
			}
		}
		ret.al("")
		ret.al("＜残されているタスク＞")
		ret.al("not implemented")
		ret.al("")
		return ret.toString()
	}
}