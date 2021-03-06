package org.net.perorin.bastet.util;

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.net.perorin.bastet.data.JobData
import org.net.perorin.bastet.data.MonitoringData

import groovy.sql.Sql

class SqlUtil{

	static def sql;

	static synchronized def dbinit() {
		def tableList = []
		SqlUtil.getInstance().eachRow("select * from sqlite_master where type = 'table'"){
			tableList << it.name
		}
		if(!tableList.contains("Parameter")) {
			SqlUtil.getInstance().execute("""
				CREATE TABLE Parameter (CodeID PRIMARY KEY, Desc, Para1, Para2, Para3, Para4, Para5, Para6, Para7, Para8);
				INSERT INTO Parameter VALUES ('AES-Key-Path', 'AES key file path', '', '', '', '', '', '', '', '');
				INSERT INTO Parameter VALUES ('TeamSpiritMailAddress', 'TeamSpirit MailAddress', '', '', '', '', '', '', '', '');
				INSERT INTO Parameter VALUES ('TeamSpiritPassword', 'TeamSpirit Password', '', '', '', '', '', '', '', '');
				INSERT INTO Parameter VALUES ('TeamSpititURL', 'TeamSpirit URL', 'https://teamspirit-5640.cloudforce.com/home/home.jsp', '', '', '', '', '', '', '');
				INSERT INTO Parameter VALUES ('SelenideBrowser', 'SelenideBrowser(Configuration.browser)', 'chrome', '', '', '', '', '', '', '');
				INSERT INTO Parameter VALUES ('HolidayURL', 'URL for downloading holiday CSV', 'https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv', '', '', '', '', '', '', '');
			""")
		}
		if(!tableList.contains("JobData")) {
			SqlUtil.getInstance().execute("""
				CREATE TABLE JobData (Code, Name, Kind, Alias, PRIMARY KEY(Code, Name, Kind, Alias) );
			""")
		}
		if(!tableList.contains("WorkData")) {
			SqlUtil.getInstance().execute("""
				CREATE TABLE WorkData (Date, WorkTitle, WorkDetail, WorkStart, WorkEnd, JobCode, JobName, JobKind, JobAlias,
					PRIMARY KEY(Date, WorkTitle, WorkDetail, WorkStart, WorkEnd, JobCode, JobName, JobKind, JobAlias) );
			""")
		}
		if(!tableList.contains("MonitoringData")) {
			SqlUtil.getInstance().execute("""
				CREATE TABLE MonitoringData (Kind, Date, Name, Path, PRIMARY KEY(Kind, Date, Name, Path));
			""")
		}
	}

	static def getInstance() {
		if(sql == null) {
			def config = Util.getConfig()
			def jdbcDriver = 'org.sqlite.JDBC'
			sql = Sql.newInstance("jdbc:sqlite:" + config.sql.database.path + "", jdbcDriver);
			SqlUtil.dbinit()
		}
		return sql
	}

	/**
	 * コードパラメーター取得
	 * @param id コードID
	 * @return レコード
	 */
	static def getParameter(def id) {
		return SqlUtil.getInstance().firstRow("select * from Parameter where CodeId = ?", ["${id}"])
	}

	/**
	 * コードパラメーター設定
	 * @param id コードID
	 * @param para パラメーター（List）
	 */
	static def setParameter(def id, def para) {
		String sql = "update Parameter set "
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
				replace into JobData values(${it.code}, ${it.name}, ${it.kind}, ${it.alias})
			""")
		}
	}

	static def removeJobData(def jobDataList) {
		jobDataList.each {
			SqlUtil.getInstance().execute("""
				delete from JobData where Code = ${it.code} and Name = ${it.name} and Kind = ${it.kind} and Alias = ${it.alias}
			""")
		}
	}

	static def addAliasData(def aliasDataList) {
		SqlUtil.addJobData(aliasDataList)
	}

	static def getJobData() {
		def ret = []
		SqlUtil.getInstance().eachRow("select * from JobData where Alias = ''  order by Name, Kind"){
			def jobData = new JobData()
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
			jobData.code = it.Code
			jobData.name = it.Name
			jobData.kind = it.Kind
			jobData.alias = it.Alias
			ret << jobData
		}
		return ret
	}

	static def addWorkData(def workDataList) {
		Calendar cal = Calendar.getInstance()
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd")
		SqlUtil.getInstance().execute("""
				delete from WorkData where Date = ${sdf.format(cal.getTime())}
				""")
		workDataList.each {
			SqlUtil.getInstance().execute("""
				replace into WorkData values(${it.Date}, ${it.WorkTitle}, ${it.WorkDetail}, ${it.WorkStart}, ${it.WorkEnd}, ${it.JobCode}, ${it.JobName}, ${it.JobKind}, ${it.JobAlias})
			""")
		}
	}

	static def getWorkData(LocalDate date = LocalDate.now()) {
		def ret = []
		SqlUtil.getInstance().eachRow("select * from WorkData where Date = ${date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))} order by WorkStart"){
			def obj = new Object()
			obj.metaClass.workTitle = it.WorkTitle
			obj.metaClass.workDetail = it.WorkDetail
			obj.metaClass.workStart = it.WorkStart
			obj.metaClass.workEnd = it.WorkEnd
			obj.metaClass.jobCode = it.JobCode
			obj.metaClass.jobName = it.JobName
			obj.metaClass.jobKind = it.JobKind
			obj.metaClass.jobAlias = it.JobAlias
			ret << obj
		}
		return ret
	}

	static def addMonitoringData(MonitoringData md) {
		SqlUtil.getInstance().execute("""
				replace into MonitoringData values(${md.kind}, ${md.date}, ${md.title}, ${md.path})
			""")
	}
}