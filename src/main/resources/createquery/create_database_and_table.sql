
-- 데이터베이스 생성
create database project;
-- 데이터베이스 사용
use project;

-- memo 테이블생성
create table memo(
	id INT not null primary key auto_increment,
    text VARCHAR(50) NOT NULL
);


