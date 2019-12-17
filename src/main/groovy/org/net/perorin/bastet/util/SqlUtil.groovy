package org.net.perorin.bastet.util;

import org.net.perorin.bastet.data.JobData

import groovy.sql.Sql

class SqlUtil{

	static def sql;

	static def getInstance() {
		if(sql == null) {
			def config = Util.getConfig()
			def jdbcDriver = 'org.sqlite.JDBC'
			sql = Sql.newInstance("jdbc:sqlite:" + config.sql.database.path + "", jdbcDriver);
		}
		return sql
	}

	/**
	 * コードパラメーター取得
	 * @param id コードID
	 * @return レコード
	 */
	static def getCodeParameter(def id) {
		return SqlUtil.getInstance().firstRow("select * from CodeParameter where CodeId = ?", ["${id}"])
	}

	/**
	 * コードパラメーター設定
	 * @param id コードID
	 * @param para パラメーター（List）
	 */
	static def setCodeParameter(def id, def para) {
		String sql = "update CodeParameter set "
		for(int i = 1; i <= 8; i++) {
			if(para[i-1] != null) {
				sql += " Para${i} = '${para[i - 1]}'"
			}
		}
		sql += " where CodeId = '${id}'"
		SqlUtil.getInstance().execute(sql)
	}

	static def addJobData(def jobDataList) {
		jobDataList.each {
			SqlUtil.getInstance().execute("""
				replace into JobData values(${it.date}, ${it.code}, ${it.name}, ${it.kind}, ${it.alias})
			""")
		}
	}

	static def addAliasData(def aliasDataList) {
		SqlUtil.addJobData(aliasDataList)
	}

	static def getJobData() {
		def ret = []
		SqlUtil.getInstance().eachRow("select * from JobData where alias = '' order by Name, Kind"){
			def jobData = new JobData()
			jobData.date = it.Date
			jobData.code = it.Code
			jobData.name = it.Name
			jobData.kind = it.Kind
			jobData.alias = it.Alias
			ret << jobData
		}
		return ret
	}

	static def getAliasData() {
		def ret = []
		SqlUtil.getInstance().eachRow("select * from JobData where alias <> '' order by Alias"){
			def jobData = new JobData()
			jobData.date = it.Date
			jobData.code = it.Code
			jobData.name = it.Name
			jobData.kind = it.Kind
			jobData.alias = it.Alias
			ret << jobData
		}
		return ret
	}
}