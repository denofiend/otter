DROP TABLE RETL.RETL_BUFFER;
CREATE TABLE RETL.RETL_BUFFER 
(	
	ID NUMBER(11) NOT NULL,
	TABLE_ID NUMBER(11) NOT NULL,
	FULL_NAME varchar(512),
	TYPE CHAR(1) NOT NULL,
	PK_DATA VARCHAR(256) NOT NULL,
	GMT_CREATE DATE NOT NULL,
	GMT_MODIFIED DATE NOT NULL,
	CONSTRAINT RETL_BUFFER_ID PRIMARY KEY (ID) 
);

CREATE SEQUENCE RETL.SEQ_RETL_BUFFER MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 100 NOORDER NOCYCLE; 

DROP TABLE RETL.RETL_MARK;
CREATE TABLE RETL.RETL_MARK
(	
	ID NUMBER(11) NOT NULL,
	CHANNEL_INFO varchar(128),
	CHANNEL_ID NUMBER(11),
	CONSTRAINT RETL_MARK_ID PRIMARY KEY (ID) 
);
CREATE SEQUENCE RETL.SEQ_RETL_MARK MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 100 NOORDER NOCYCLE;

DROP TABLE RETL.XDUAL;
CREATE TABLE RETL.XDUAL (
  ID int(11) NOT NULL,
  X DATE NOT NULL,
  CONSTRAINT XDUAL_ID PRIMARY KEY (ID) 
);
CREATE SEQUENCE RETL.SEQ_XDUAL MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 100 NOORDER NOCYCLE;

CREATE TABLE IF NOT EXIST RETL.RETL_CLIENT;
( 
	ID NUMBER, 
	CLIENT_INFO varchar(64), 
	CLIENT_IDENTIFIER varchar(64), 
	CONSTRAINT RETL_CLIENT_ID PRIMARY KEY (ID)
);
CREATE SEQUENCE RETL.SEQ_RETL_CLIENT MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 100 NOORDER NOCYCLE;

--- 插入初始化数据
merge /*+ use_nl(a b)*/ into XDUAL a using (select 1 as id , sysdate as x from retl.xdual) b on (a.id=b.id) when matched then update set a.x = b.x when not matched then insert (a.id , a.x) values (b.id , b.x)