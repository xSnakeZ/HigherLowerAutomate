README generated with GitHub Copilot 

# HigherLowerAutomate

## Overview
The HigherLowerAutomate project is a Java-based application designed to automate and enhance the "Higher or Lower" game experience. The project leverages modular design principles to ensure scalability, maintainability, and ease of integration with various components.

## Main Goal
The primary goal of this project is to provide an automated solution for playing and managing the "Higher or Lower" game. It integrates decision-making models, database management, and user interface automation to deliver a seamless and efficient gaming experience.

## Key Components
- **Game Logic**: Implements the core logic for the "Higher or Lower" game.
- **Database Integration**: Stores previous decisions in a PostgreSQL Database (localhost:5432) to speed up gameplay.
- **Decision Models**: Utilizes large language models hosted on localhost:11434 to make decisions during gameplay.
- **UI Automation**: Automates interactions with the game's user interface using Selenium.

## Getting Started
### Prerequisites
- Java 11 or higher
- Maven 3.x
- PostgreSQL Database running on localhost:5432
- Ollama model running on localhost:11434

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
4. Copy Dependencies to the target folder:
   ```cmd
   mvn dependency:copy-dependencies
   ```

### Running the Application
To run the application, execute the following command:
```cmd
java -cp "target\HigherLowerAutomate-1.0-SNAPSHOT.jar;target\dependency\*" Main <model_name> <model_name> <useDatabase>
```
- `<model_name>`: The name of the decision model to use (e.g., "gpt-4").
- `<useDatabase>`: A boolean value (`true` or `false`) indicating whether to use the database for storing and retrieving previous decisions.
