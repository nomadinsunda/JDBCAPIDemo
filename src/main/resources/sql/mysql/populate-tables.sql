-- Employees
INSERT INTO employees VALUES (10001, 'Axel', 'Washington', '1943-08-28', 5);
INSERT INTO employees VALUES (10083, 'Arvid', 'Sharma', '1954-11-24', NULL);
INSERT INTO employees VALUES (10120, 'Jonas', 'Ginsberg', '1969-01-01', NULL);
INSERT INTO employees VALUES (10005, 'Florence', 'Wojokowski', '1971-07-04', 12);
INSERT INTO employees VALUES (10099, 'Sean', 'Washington', '1966-09-21', NULL);
INSERT INTO employees VALUES (10035, 'Elizabeth', 'Yamaguchi', '1959-12-24', NULL);

-- Cars
INSERT INTO cars VALUES (5, 'Honda', 'Civic DX', 1996);
INSERT INTO cars VALUES (12, 'Toyota', 'Corolla', 1999);

-- Projects (선택사항, 미리 employees와 연결될 수 있게 데이터 준비)
INSERT INTO projects (Project_Name, Employee_ID) VALUES ('Database Migration', 10001);
INSERT INTO projects (Project_Name, Employee_ID) VALUES ('HR Automation', 10005);