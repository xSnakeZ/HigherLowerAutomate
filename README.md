README generated with GitHub Copilot 

# HigherLowerAutomate

## Overview
The HigherLowerAutomate project is a Java-based application designed to automate and enhance the "Higher or Lower" game experience. The project leverages modular design principles to ensure scalability, maintainability, and ease of integration with various components.

## Main Goal
The primary goal of this project is to provide an automated solution for playing and managing the "Higher or Lower" game. It integrates decision-making models, database management, and user interface automation to deliver a seamless and efficient gaming experience.

## Key Components
- **Game Logic**: Implements the core logic for the "Higher or Lower" game.
- **Database Integration**: Stores previous decisions to speed up gameplay.
- **Decision Models**: Utilizes large language models to make decisions during gameplay.
- **UI Automation**: Automates interactions with the game's user interface using Selenium.

## Getting Started
### Prerequisites
- Java 11 or higher
- Maven 3.x

### Installation
1. Clone the repository:
   ```cmd
   git clone https://github.com/your-repo-url.git
   ```
2. Navigate to the project directory:
   ```cmd
   cd HigherLowerAutomate
   ```
3. Build the project:
   ```cmd
   mvn clean install
   ```

### Running the Application
To run the application, execute the following command:
```cmd
java -cp target/HigherLowerAutomate-1.0-SNAPSHOT.jar main.java.Main
```
While running, the application also needs access to the specified Ollama model e.g.
```cmd
ollama run gpt-oss:120b-cloud
```

## Screenshots
<img width="1601" height="1232" alt="image" src="https://github.com/user-attachments/assets/3587ffa8-14a1-4cba-a22a-21d7cfb6ec2d" />
<img width="1597" height="1242" alt="image" src="https://github.com/user-attachments/assets/d3ef30b7-cc27-48a4-b029-900052a84c68" />




