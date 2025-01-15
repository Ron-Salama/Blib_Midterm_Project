-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: blib
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `books`
--

DROP TABLE IF EXISTS `books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `books` (
  `ISBN` varchar(13) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `Subject` varchar(100) NOT NULL,
  `ShortDescription` text NOT NULL,
  `NumCopies` int NOT NULL,
  `ShelfLocation` varchar(50) NOT NULL,
  `AvailableCopiesNum` int NOT NULL,
  `ReservedCopiesNum` int NOT NULL,
  PRIMARY KEY (`ISBN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='This table holds the information of the books in BLib library''s system.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `books`
--

LOCK TABLES `books` WRITE;
/*!40000 ALTER TABLE `books` DISABLE KEYS */;
INSERT INTO `books` VALUES ('0011','The Hobbit','Fantasy','A thrilling adventure featuring a wizard dwarves and dragons.',7,'A1-01',0,7),('0022','The Catcher in the Rye','Fiction','A coming-of-age story about rebellion and self-discovery.',5,'A1-02',0,1),('0033','To Kill a Mockingbird','Fiction','A profound tale of justice racism and childhood innocence.',4,'A2-01',4,0),('0044','1984','Fiction','A dystopian world filled with surveillance and control.',6,'A2-02',6,0),('0055','Brave New World','Fiction','A futuristic society shaped by technology and control.',2,'A3-01',3,0),('0066','Moby-Dick','Fiction','An epic tale of obsession revenge and the sea.',0,'A3-02',2,0),('0077','War and Peace','History','An intricate story of life love and war during Napoleonic times.',1,'A4-01',1,0),('0088','Pride and Prejudice','Romance','A witty exploration of love and class in 19th-century England.',5,'A4-02',5,0),('0099','The Great Gatsby','Fiction','A tragic tale of love wealth and the American dream.',6,'A5-01',6,0),('0100','Jane Eyre','Fiction','A story of resilience and love featuring an independent heroine.',4,'A5-02',0,3),('0111','The Odyssey','Epic','A mythical journey filled with gods monsters and heroism.',0,'B1-01',0,0),('0122','The Iliad','Epic','An epic recounting the heroics and tragedy of the Trojan War.',2,'B1-02',2,0),('0133','Hamlet','Drama','A gripping tale of revenge tragedy and moral dilemmas.',6,'B2-01',6,0),('0144','Macbeth','Drama','A dark exploration of ambition prophecy and guilt.',4,'B2-02',0,4),('0155','A Tale of Two Cities','Fiction','A story of sacrifice and love set during the French Revolution.',5,'B3-01',5,0),('0166','Les Misérables','Fiction','A moving tale of justice redemption  and love in 19th-century France.',2,'B3-02',2,0),('0177','The Alchemist','Fiction','A philosophical journey to fulfill one’s dreams and destiny.',7,'B4-01',7,0),('0188','The Book Thief','Historical','A poignant tale set during WWII centered on love for books.',5,'B4-02',5,0),('0199','Harry Potter and the Sorcerer’s Stone','Fantasy','A magical story of friendship and adventure in a wizarding school.',8,'C1-01',8,0),('0200','Harry Potter and the Chamber of Secrets','Fantasy','An enthralling mystery filled with magical creatures and danger.',7,'C1-02',7,0),('0211','Harry Potter and the Prisoner of Azkaban','Fantasy','A gripping tale of time travel and hidden truths.',6,'C2-01',6,0),('0222','Harry Potter and the Goblet of Fire','Fantasy','An intense adventure featuring tournaments and dark secrets.',5,'C2-02',5,0),('0233','The Chronicles of Narnia: The Lion the Witch and the Wardrobe','Fantasy','A magical journey into a world of bravery and adventure.',7,'C3-01',7,0),('0244','The Chronicles of Narnia: Prince Caspian','Fantasy','A return to Narnia to reclaim a throne and bring peace.',6,'C3-02',6,0),('0255','The Chronicles of Narnia: The Voyage of the Dawn Treader','Fantasy','An adventurous journey across the seas to unknown lands.',5,'C4-01',5,0),('0266','The Chronicles of Narnia: The Silver Chair','Fantasy','A heroic quest to rescue a prince from darkness.',4,'C4-02',4,0),('0277','The Lord of the Rings: The Fellowship of the Ring','Fantasy','A quest to destroy evil and unite the forces of good.',8,'C5-01',8,0),('0288','The Lord of the Rings: The Two Towers','Fantasy','A battle against darkness with loyalty and courage.',7,'C5-02',7,0),('0299','The Lord of the Rings: The Return of the King','Fantasy','A triumphant story of victory and the cost of bravery.',6,'C6-01',6,0),('0300','Animal Farm','Fiction','A biting satire on power and corruption in society.',5,'C6-02',5,0),('0311','Of Mice and Men','Fiction','A touching tale of friendship dreams and hardship.',6,'D1-01',6,0),('0322','The Grapes of Wrath','Fiction','An epic story of resilience and hope during the Great Depression.',4,'D1-02',4,0),('0333','The Kite Runner','Fiction','A moving story of friendship betrayal and redemption.',5,'D2-01',5,0),('0344','A Thousand Splendid Suns','Fiction','A heartbreaking tale of family love and survival.',3,'D2-02',3,0),('0355','The Road','Fiction','A haunting story of survival in a post-apocalyptic world.',4,'D3-01',4,0),('0366','Life of Pi','Fiction','An incredible journey of survival spirituality and adventure.',6,'D3-02',6,0),('0377','The Da Vinci Code','Mystery','A gripping mystery filled with art religion and secrets.',7,'D4-01',7,0),('0388','Angels and Demons','Mystery','A thrilling story of conspiracy and science.',5,'D4-02',5,0),('0399','Inferno','Mystery','A dangerous quest involving art and history.',4,'D5-01',4,0),('0400','Digital Fortress','Thriller','An exciting tale of cryptography and danger.',3,'D5-02',3,0),('0411','The Hunger Games','Dystopian','A fight for survival and revolution in a broken society.',8,'E1-01',8,0),('0422','Catching Fire','Dystopian','A gripping sequel of danger politics and hope.',7,'E1-02',7,0),('0433','Mockingjay','Dystopian','A powerful conclusion about war and freedom.',6,'E2-01',6,0),('0444','Divergent','Dystopian','A story of choice identity and courage.',5,'E2-02',5,0),('0455','Insurgent','Dystopian','A battle for truth rebellion and hope.',4,'E3-01',4,0),('0466','Allegiant','Dystopian','A journey of sacrifice and love in a divided world.',3,'E3-02',3,0),('0477','The Maze Runner','Dystopian','A thrilling adventure to escape a deadly maze.',8,'E4-01',8,0),('0488','The Scorch Trials','Dystopian','A gripping fight against disease and betrayal.',7,'E4-02',7,0);
/*!40000 ALTER TABLE `books` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `borrowed_books`
--

DROP TABLE IF EXISTS `borrowed_books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `borrowed_books` (
  `borrow_id` int NOT NULL AUTO_INCREMENT,
  `subscriber_id` int DEFAULT NULL,
  `Name` varchar(45) DEFAULT NULL,
  `Subject` varchar(45) DEFAULT NULL,
  `Borrowed_Time` varchar(45) DEFAULT NULL,
  `Return_Time` varchar(45) DEFAULT NULL,
  `ISBN` varchar(35) DEFAULT NULL,
  PRIMARY KEY (`borrow_id`),
  KEY `subscriber_id_idx` (`subscriber_id`),
  CONSTRAINT `subscriber_id` FOREIGN KEY (`subscriber_id`) REFERENCES `subscriber` (`subscriber_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `borrowed_books`
--

LOCK TABLES `borrowed_books` WRITE;
/*!40000 ALTER TABLE `borrowed_books` DISABLE KEYS */;
INSERT INTO `borrowed_books` VALUES (1,1,'Yuval','temp','14-01-2025','temp','0066'),(3,1,'Brave New World','temp','15-01-2025','temp','0055');
/*!40000 ALTER TABLE `borrowed_books` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detailed_subscription_history`
--

DROP TABLE IF EXISTS `detailed_subscription_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detailed_subscription_history` (
  `detailed_subscription_history` int NOT NULL,
  `history` varchar(1500) DEFAULT NULL,
  PRIMARY KEY (`detailed_subscription_history`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detailed_subscription_history`
--

LOCK TABLES `detailed_subscription_history` WRITE;
/*!40000 ALTER TABLE `detailed_subscription_history` DISABLE KEYS */;
INSERT INTO `detailed_subscription_history` VALUES (1,'01-01-2025 10:30,0011,The Hobbit,Fantasy,Borrowed successfully;10-01-2025 15:00,0011,The Hobbit,Fantasy,Returned successfully;05-01-2025 14:20,0054,1984,Dystopian,Borrowed successfully;20-01-2025 17:45,0054,1984,Dystopian,Returned late, Late by: 5 days;10-01-2025,0032,The Great Gatsby,Classics,Lost;12-01-2025 11:00,0048,To Kill a Mockingbird,Fiction,Borrowed successfully;18-01-2025 13:15,0048,To Kill a Mockingbird,Fiction,Returned successfully;15-01-2025 09:30,0066,Moby Dick,Adventure,Reserved successfully;20-01-2025 16:00,0077,Pride and Prejudice,Romance,Borrowed successfully;22-01-2025 10:00,0083,War and Peace,Historical Fiction,Reserved successfully;14-01-2025,0066,Moby-Dick,Borrowed successfully;'),(2,'01-01-2025 10:30,0011,The Hobbit,Fantasy,Borrowed successfully;10-01-2025 15:00,0011,The Hobbit,Fantasy,Returned successfully;05-01-2025 14:20,0054,1984,Dystopian,Borrowed successfully;20-01-2025 17:45,0054,1984,Dystopian,Returned late, Late by: 5 days;10-01-2025,0032,The Great Gatsby,Classics,Lost;12-01-2025 11:00,0048,To Kill a Mockingbird,Fiction,Borrowed successfully;18-01-2025 13:15,0048,To Kill a Mockingbird,Fiction,Returned successfully;15-01-2025 09:30,0066,Moby Dick,Adventure,Reserved successfully;20-01-2025 16:00,0077,Pride and Prejudice,Romance,Borrowed successfully;22-01-2025 10:00,0083,War and Peace,Historical Fiction,Reserved successfully;'),(3,NULL);
/*!40000 ALTER TABLE `detailed_subscription_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `librarian`
--

DROP TABLE IF EXISTS `librarian`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `librarian` (
  `librarian_id` int NOT NULL,
  `librarian_name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`librarian_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `librarian`
--

LOCK TABLES `librarian` WRITE;
/*!40000 ALTER TABLE `librarian` DISABLE KEYS */;
INSERT INTO `librarian` VALUES (11,'Timmy Turner'),(22,'Bob'),(33,'Other Bob');
/*!40000 ALTER TABLE `librarian` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `requests`
--

DROP TABLE IF EXISTS `requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `requests` (
  `requestType` varchar(45) DEFAULT NULL,
  `requestedByID` varchar(45) DEFAULT NULL,
  `requestedByName` varchar(45) DEFAULT NULL,
  `bookName` varchar(45) DEFAULT NULL,
  `bookId` varchar(45) DEFAULT NULL,
  `borrowTime` varchar(45) DEFAULT NULL,
  `returnTime` varchar(45) DEFAULT NULL,
  `extendTime` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `requests`
--

LOCK TABLES `requests` WRITE;
/*!40000 ALTER TABLE `requests` DISABLE KEYS */;
INSERT INTO `requests` VALUES ('Request For Register','5','55','555','5555','temp','temp','temp'),('Borrow For Subscriber','1','Yuval','The Catcher in the Rye','0022','temp','temp','temp'),('Request For Register','214','24','24','42','temp','temp','temp'),('Reserve For Subscriber','1','Yuval','The Catcher in the Rye','0022','temp','temp','temp'),('Reserve For Subscriber','1','Yuval','The Catcher in the Rye','0022','temp','temp','temp'),('Reserve For Subscriber','1','Yuval','The Catcher in the Rye','0022','temp','temp','temp'),('Reserve For Subscriber','1','Yuval','The Catcher in the Rye','0022','temp','temp','temp'),('Reserve For Subscriber','1','Yuval','The Catcher in the Rye','0022','temp','temp','temp'),('Reserve For Subscriber','1','Yuval','Jane Eyre','0100','temp','temp','temp'),('Reserve For Subscriber','1','Yuval','Macbeth','0144','temp','temp','temp'),('Reserve For Subscriber','1','Yuval','Macbeth','0144','temp','temp','temp'),('Reserve For Subscriber','1','Yuval','Macbeth','0144','temp','temp','temp'),('Reserve For Subscriber','1','Yuval','Macbeth','0144','temp','temp','temp'),('Borrow For Subscriber','1','Yuval','Moby-Dick','0066','14-01-2025','28-01-2025','temp'),('Reserve For Subscriber','1','Yuval','The Catcher in the Rye','0022','temp','temp','temp'),('Return For Subscriber','1','Yuval','Yuval','1','temp','0','temp'),('Borrow For Subscriber','1','Yuval','Divergent','0444','15-01-2025','29-01-2025','temp'),('Return For Subscriber','1','Yuval','Divergent','2','temp','0','temp'),('Borrow For Subscriber','1','Yuval','Brave New World','0055','15-01-2025','29-01-2025','temp');
/*!40000 ALTER TABLE `requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscriber`
--

DROP TABLE IF EXISTS `subscriber`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscriber` (
  `subscriber_id` int NOT NULL,
  `subscriber_name` varchar(255) NOT NULL,
  `detailed_subscription_history` int DEFAULT NULL,
  `subscriber_phone_number` varchar(20) NOT NULL,
  `subscriber_email` varchar(255) NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`subscriber_id`),
  KEY `detailed_subscription_history_idx` (`detailed_subscription_history`),
  CONSTRAINT `detailed_subscription_history` FOREIGN KEY (`detailed_subscription_history`) REFERENCES `detailed_subscription_history` (`detailed_subscription_history`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='This table stores the information on each and every subscriber in the library.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscriber`
--

LOCK TABLES `subscriber` WRITE;
/*!40000 ALTER TABLE `subscriber` DISABLE KEYS */;
INSERT INTO `subscriber` VALUES (1,'Yuval',1,'0523567891','b@gmail.com','Not Frozen'),(2,'Yaniv',2,'0541234567','111@gmail.com','Frozen at:05-01-2025'),(3,'Ron',3,'0542345678','c@gmail.com','Frozen at:05-01-2025');
/*!40000 ALTER TABLE `subscriber` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-15 15:15:18
