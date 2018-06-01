create table schema_scripts_version (key text PRIMARY KEY, executed timestamp);
insert into schema_scripts_version(key, executed) values ('0000-script1.cql', 0);
insert into schema_scripts_version(key, executed) values ('0000-script2.cql', 1);
insert into schema_scripts_version(key, executed) values ('0000-script3.cql', 2);
