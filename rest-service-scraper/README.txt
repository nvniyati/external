1) download maven from http://maven.apache.org/download.cgi
2) clone the project
3) cd to folder rest-service-scraper
4) mvn clean install
5) To run test cases only: mvn test
6) scraper.log file is created under the dir: rest-service-scraper
7) to start container for rest service: mvn spring-boot:run
8) http://localhost:8080/reviews/overlyPositive => default 5 pages
9) http://localhost:8080/reviews/overlyPositive?pages=1 
9) http://localhost:8080/reviews/offensive?pages=10 => pages if not provided default =5
10) http://localhost:8080/reviews/offensive/sort?pages=8&topCount=3  => default pages=5, default topCount=3
11) To stop the container: CTRL + C



Next TO DO
-----------------
1) enhance exception handling
2) cover more scenarios in test cases
