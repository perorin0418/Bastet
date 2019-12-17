
DROP TABLE CodeParameter;
CREATE TABLE CodeParameter (CodeID PRIMARY KEY, Desc, Para1, Para2, Para3, Para4, Para5, Para6, Para7, Para8);
INSERT INTO CodeParameter VALUES ('AES-Key-Path', 'AES key file path', '', '', '', '', '', '', '', '');
INSERT INTO CodeParameter VALUES ('TeamSpiritMailAddress', 'TeamSpirit MailAddress', '', '', '', '', '', '', '', '');
INSERT INTO CodeParameter VALUES ('TeamSpiritPassword', 'TeamSpirit Password', '', '', '', '', '', '', '', '');
INSERT INTO CodeParameter VALUES ('TeamSpititURL', 'TeamSpirit URL', 'https://teamspirit-5640.cloudforce.com/home/home.jsp', '', '', '', '', '', '', '');
INSERT INTO CodeParameter VALUES ('SelenideBrowser', 'SelenideBrowser(Configuration.browser)', 'chrome', '', '', '', '', '', '', '');
SELECT * FROM CodeParameter ORDER BY CodeID;


DROP TABLE JobData;
CREATE TABLE JobData (Date, Code, Name, Kind, Alias, PRIMARY KEY(Date, Code, Name, Kind, Alias) );
SELECT * FROM JobData ORDER BY Date, Code, Alias;
