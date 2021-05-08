## A suggested work plan (not mandatory)

*For technical details look up an already existing project or just ask*

Note: 
* Database will grow in each step so be patient
* Some Libraries are uploaded
* Use mysql (Xampp or Oracle)
* Use Intellij idea

1. Create a database table for "crawled URLs" and another for "URLs to be crawled"
2. Create a crawler that removes from the first table and adds to the second one

Technical Notes:
* A crawler is a class that has a function that takes a URL as a parameter and using a library called jsoup, it catches the URL as an HTML document and extracts HTML tags from it.
* For crawling we extract any HTML tag that has attribute href which is basically a function in jsoup
* jsoup library has the following objects to use: Document, Element, URL,.........

3. Create a table that says which word was found in which URL how many times (word - URL - frequency)
4. Create a simple indexer that takes a URL from the crawled ones and split it into words and stores it in an array
5. Create a simple ranker that takes an array of words and convert it to a data structure sorted by frequency
6. Add words, URL and frequencies to the above table

Technical Notes:
* indexer is a class that uses jsoup to convert the document into strings then stores them in arrays after that it calls the word processing class that filters out the array (we can neglect this part for now) then the filtered array is passed to the ranker
* the ranker is a class that take an array of strings and analyze it according to frequency (and HTML tag which we can also neglect for now)

7. Create a simple query engine that takes a search word and creates a heap 
8. Find the word in the "words" table in the database, extract URLs then add them to the heap
9. print the heap to the UI

---
The above is the base anything else is an addition that comes later.....

10. Create a function that takes a URL and tell you which URLs are robot safe and which aren't, add them to an array

Technical Notes:
* robot.txt is a text file that we read normally and analyze as string
* the crawler uses the output array during crawling to avoid not allowed sites

11. Instead of passing a list of words for ranker to rank according to freq pass different lists according to HTML tag
12. Filter words before storing them in the database (wordProcessor i.e. Stemmer )
13. Store the title and first paragraph in any URL as text in DB to show in search results
14. Make the indexer store images in Separate tables with alt tags as words to search for and create an image query engine
15. Use threading
16. Create a query table that store searched terms with frequency and geo-location to offer suggestions

