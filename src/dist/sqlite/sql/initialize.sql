
DROP TABLE Parameter;
CREATE TABLE Parameter (CodeID PRIMARY KEY, Desc, Para1, Para2, Para3, Para4, Para5, Para6, Para7, Para8);
INSERT INTO Parameter VALUES ('AES-Key-Path', 'AES key file path', '', '', '', '', '', '', '', '');
INSERT INTO Parameter VALUES ('TeamSpiritMailAddress', 'TeamSpirit MailAddress', '', '', '', '', '', '', '', '');
INSERT INTO Parameter VALUES ('TeamSpiritPassword', 'TeamSpirit Password', '', '', '', '', '', '', '', '');
INSERT INTO Parameter VALUES ('TeamSpititURL', 'TeamSpirit URL', 'https://teamspirit-5640.cloudforce.com/home/home.jsp', '', '', '', '', '', '', '');
INSERT INTO Parameter VALUES ('SelenideBrowser', 'SelenideBrowser(Configuration.browser)', 'chrome', '', '', '', '', '', '', '');
INSERT INTO Parameter VALUES ('HolidayURL', 'URL for downloading holiday CSV', 'https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv', '', '', '', '', '', '', '');
SELECT * FROM Parameter ORDER BY CodeID;


DROP TABLE JobData;
CREATE TABLE JobData (Code, Name, Kind, Alias, PRIMARY KEY(Code, Name, Kind, Alias) );
SELECT * FROM JobData ORDER BY Code, Alias;

DROP TABLE WorkData;
CREATE TABLE WorkData (Date, WorkTitle, WorkDetail, WorkStart, WorkEnd, JobCode, JobName, JobKind, JobAlias,
       PRIMARY KEY(Date, WorkTitle, WorkDetail, WorkStart, WorkEnd, JobCode, JobName, JobKind, JobAlias) );
SELECT * FROM WorkData ORDER BY WorkStart;
