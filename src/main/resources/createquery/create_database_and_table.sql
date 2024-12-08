
-- 데이터베이스 생성
create database project;
-- 데이터베이스 사용
use project;

-- memo 테이블생성
create table memo(
	id INT not null primary key auto_increment,
    text VARCHAR(50) NOT NULL
);

-- diary 테이블생성
create table diary(
    id INT not null primary key auto_increment,
    weather VARCHAR(50) NOT NULL,
    icon VARCHAR(50) NOT NULL,
    temperature DOUBLE NOT NULL,
    text VARCHAR(500) NOT NULL,
    date DATE NOT NULL
);

-- date_weather(날씨정보저장) 테이블생성
create table date_weather (
    date DATE not null primary key,
    weather VARCHAR(50) NOT NULL,
    icon VARCHAR(50) NOT NULL,
    temperature DOUBLE NOT NULL
);

