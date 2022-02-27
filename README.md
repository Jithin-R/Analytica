# Analytica
This is a **Spark Stream Processing** project using **Apache Kafka** and **Apache Spark**. In this project the data from the *Web Application* is streamed by *Kafka* to *Spark* for processing, and the processed data is stored in the *MySQL database* for analysis.

## Dependencies
<ul>
  <li>Apache Spark</li>
  <li>Apache Kafka</li>
  <li>MySQL</li>
  <li>kafkajs</li>
</ul>

## Quick Start
1. Start the ZooKeeper
```bash
bin/zkServer.sh start-foreground
```
2. Check whether ZooKeeper is running or not
```bash
jps
```
3. Start the Kafka Server
```bash
bin/kafka-server-start.sh config/server.properties
```
4. Start the express-server to load the Web App
```bash
node index.js
```
5. Run the *Analytica.scala* file in intellij
6. Fill the form on the Web App and click Submit
7. The Data is send to Spark and the processed data is stored in database
8. Check the MySQL Database
```SQL
SELECT * FROM Analytica.user_data;
```
