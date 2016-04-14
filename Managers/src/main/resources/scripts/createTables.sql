CREATE TABLE CAULDRON (
  ID BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  CAPACITY INTEGER,
  WATERTEMPERATURE INTEGER,
  HELLFLOOR INTEGER
);

CREATE TABLE SINNER (
    ID BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    FIRSTNAME VARCHAR(20),
    LASTNAME VARCHAR(20),
    SIN VARCHAR(50),
    RELEASEDATE DATE,
    SIGNEDCONTRACT BOOLEAN,
    CAULDRONID BIGINT
 );

ALTER TABLE SINNER
    ADD FOREIGN KEY (CAULDRONID)
      REFERENCES CAULDRON(ID);