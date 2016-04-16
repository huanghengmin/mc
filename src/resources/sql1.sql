drop table device
create table device( id varchar(200) NOT NULL, name VARCHAR(500), deviceip varchar(200),deviceport varchar(200),devicesnmppwd varchar(200),devicetype varchar(200), devicecompany VARCHAR(500), available varchar(2), devicemode VARCHAR(500), snmpver varchar(40) ,auth varchar(40) ,authpassword varchar(200) ,common varchar(40) ,commonpassword varchar(200) ,PRIMARY KEY (id));
