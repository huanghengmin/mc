drop table snmpoid

create table snmpoid( name varchar(200) NOT NULL, type VARCHAR(500), company varchar(500),cpuuse varchar(500), disktotal VARCHAR(500), diskuse varchar(500),memtotal varchar(500),memuse varchar(500),curconn varchar(500),snmpver varchar(200),PRIMARY KEY (name));