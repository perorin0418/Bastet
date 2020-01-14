import java.time.temporal.ChronoUnit

class DateConfirmTemplate{

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