# Echolodean - Craft Your Own Vibes

Echolodean is an avant-garde music player application that grants users the creative freedom to craft unique audio tracks from their favorite songs. It’s more than just a player—it’s an artistic tool that bridges the gap between the known and the unknown in music.

## Overview

Developed with Java and Spring Boot, Echolodean interacts with a sophisticated music generation API to breathe new life into familiar tunes. It’s designed for those who seek a personalized music listening experience, where they can not only generate but also save and manipulate tracks effortlessly.

## Goal

Our primary ambition with Echolodean is to transform how users engage with music. By enabling them to generate and modify audio tracks derived from beloved songs or albums, we’re catering to the audiophiles who love to dive deeper into the creative abyss of musical renditions.

## Features

- **Music Generation**: Input the name of a song or album and let Echolodean generate new, similar tracks related to your Spotify data.

## Technologies Used

- **Backend**: Spring Boot lays the groundwork for RESTful APIs and manages the server-side wizardry.
- **Database**: MySQL stands guard over your user data and track metadata.
- **Frontend**: Vaadin
- **API Integration**: Third Party Unofficial API.

## Prerequisites

Before you begin, ensure you have met the following requirements:

- **Java JDK 21**: The project uses Java 21, which you will need to install from the [Oracle website](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html) or through a package manager if you don't already have it installed.
- **Maven 3.8 or higher**: This is required to handle project dependencies and to build the project. Maven can be downloaded and installed from [Apache Maven Project](https://maven.apache.org/download.cgi).
- **MySQL Server 8.0 or higher**: The project is configured to use MySQL for its database. You will need MySQL installed and running on your system. Installation guides are available on the [MySQL official website](https://dev.mysql.com/doc/mysql-installation-excerpt/8.0/en/).

### 1. Clone the repository
   ```bash
   git clone https://github.com/grand1nqu1s1tor/echolodean.git
   cd echolodean

### 2. Configure the Application
To set up the `application.properties` file for the Echolodean project, navigate to `src/main/resources/` and open the `application.properties` file. Replace all placeholder values (`INSERT_..._HERE`) with your actual settings. Here’s a breakdown of the configurations you'll need to adjust:

- **Server Configuration**:
  - `server.port`: Defines the port on which the application listens for incoming requests (default is 8080).
  - `custom.server.ip`: Sets the base URL for the application (default is http://localhost:8080).

- **Spotify API Configuration**:
  - `spotify.client-id`: Replace with your Spotify Client ID, which is obtainable from the Spotify Developer Dashboard.
  - `spotify.client-secret`: Replace with your Spotify Client Secret, also obtainable from the Spotify Developer Dashboard.

- **Database Configuration**:
  - `spring.datasource.driver-class-name`: Set to `com.mysql.cj.jdbc.Driver` for MySQL database connection.
  - `spring.datasource.url`: Update with the connection URL for your MySQL database.
  - `spring.datasource.username`: Replace with your database username.
  - `spring.datasource.password`: Replace with your database password.

### 3. Running the Application

Once the configuration is complete, you can run the application. Open a terminal window, navigate to the project directory, and execute the following command:

```bash
mvn spring-boot:run

### 4. Video Tutorial/Demo

For a detailed walkthrough or demonstration of Echolodean, check out our video on YouTube:
 [Watch the Echolodean Demo]([https://www.youtube.com/watch?v=your_video_id](https://youtu.be/TpCiyLQC9L8?si=4w-E2PrHyFUGBz6m))
