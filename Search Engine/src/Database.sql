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
    CrawlDate Date NOT NULL,
    titles Text,
    paragraphs Text
);