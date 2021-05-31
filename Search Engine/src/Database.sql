DROP DATABASE IF EXISTS SearchEngine;
CREATE DATABASE SearchEngine;
DROP TABLE IF EXISTS SearchEngine.UncrawledURLs;
CREATE TABLE SearchEngine.UncrawledURLs(
    id INT AUTO_INCREMENT PRIMARY KEY,
    URL varchar(2048) NOT NULL
);
DROP TABLE IF EXISTS SearchEngine.CrawledURLs;
CREATE TABLE SearchEngine.CrawledURLs(
    id INT AUTO_INCREMENT PRIMARY KEY,
    URL varchar(2048) NOT NULL,
    Title varchar(1000),
    CrawlDate Date NOT NULL,
    Paragraph Text
);