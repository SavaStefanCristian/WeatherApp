### 🌦️ Weather App (Java, Maven, PostgreSQL)

#### Overview  
This is a **client-server Weather App** built in **Java (Maven)** with a **PostgreSQL** database, integrated with **Eclipse Jakarta Persistence**. It allows users to create or log into an account, save their last chosen location, and retrieve weather data efficiently.

#### 🔹 Features  
✅ **User Authentication**: Create or log into an account.  
✅ **Location Selection**: Choose a location by name or coordinates.  
✅ **Efficient Lookup**: Finds the closest known location when selecting by coordinates.  
✅ **Weather Data**: View current weather and a multi-day forecast.  
✅ **Admin Functionality**: Load data into the database via a JSON file.  
✅ **Concurrent Server**: Handles multiple client connections simultaneously.  
✅ **Command Line Interface**: Intuitive menu for user interaction.  

#### 🏦 Architecture  
- **Client-Server Model**: Built using **TCP sockets** for communication.  
- **PostgreSQL Database**: Stores user data and weather information.  
- **Eclipse Jakarta Persistence**: For efficient ORM and database access.  

#### 🚀 Setup & Installation  
1. **Clone the Repository**  
2. **Import into IntelliJ IDEA** (or another Maven-supported IDE).  
3. **Restore the Database** using the provided `.sql` backup (schema only).  
4. **Manually Add Users**: Create at least one `USER` and one `ADMIN`.  
5. **Configure**  
   - Database authentication details.  
   - Addresses, ports, and paths in the server configuration.  
6. **Run the Project**  
   - Start the **server** first.  
   - Launch the **client** and connect.  

#### ⚠️ Important Notes  
- The repository **does not contain** sensitive details like **database credentials, addresses, or ports**—these must be configured manually.  
- The `.sql` backup only contains the **schema**, so you need to insert initial data.  


