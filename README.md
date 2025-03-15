# Video Stream

A full-stack video streaming application built with **React (Flowbite UI)** for the frontend and **Spring Boot 3.4.3** for the backend. This project allows users to upload, store, and stream videos seamlessly, using **file storage** for video files and **MySQL** for metadata management. The video content is segmented to enhance security and prevent unauthorized downloads.

## Table of Contents
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [API Endpoints](#api-endpoints)
- [Frontend Setup](#frontend-setup)
- [Backend Setup](#backend-setup)
- [Security Implementation](#security-implementation)
- [Configuration](#configuration)
- [Contributing](#contributing)

## Features
- Video file upload and storage
- Video streaming via HTTP
- HLS (HTTP Live Streaming) support with segmentation
- REST API for video management
- User-friendly frontend using Flowbite UI
- MySQL database for efficient video metadata storage
- Secure segmented streaming to restrict downloads

## Technologies Used
### Backend
- Java 21
- Spring Boot 3.4.3
- Spring Data JPA
- MySQL
- FFmpeg (for HLS conversion)

### Frontend
- React (Vite)
- Flowbite UI (Tailwind CSS-based UI library)
- Axios for API requests

## Installation
### Prerequisites
- Java 21
- Node.js 18+
- MySQL (configured in `application.properties`)
- FFmpeg installed for HLS streaming

### Backend Setup
1. Clone the repository:
   ```sh
   git clone https://github.com/robin-rodrigues/video-stream.git
   cd video-stream/backend
   ```
2. Update database credentials in `src/main/resources/application.properties`.
3. Build and run the application:
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

### Frontend Setup
1. Navigate to the frontend directory:
   ```sh
   cd ../frontend
   ```
2. Install dependencies:
   ```sh
   npm install
   ```
3. Start the development server:
   ```sh
   npm run dev
   ```

## API Endpoints
### Video Management
| Method | Endpoint                | Description          |
|--------|-------------------------|----------------------|
| `POST` | `/api/v1/videos`        | Upload a video file |
| `GET`  | `/api/v1/videos/{id}`   | Get video metadata  |
| `GET`  | `/api/v1/videos/stream/{id}` | Stream video content |

## Security Implementation
To prevent unauthorized downloads and enhance security, the following measures have been implemented:

1. **Video Segmentation with HLS:**
   - Videos are converted into **HLS format (.m3u8 and .ts segments)** using FFmpeg.
   - The video is served as a playlist (`master.m3u8`), referencing segmented `.ts` files.
   - This makes it difficult to download the full video directly.

2. **Byte-Range Streaming:**
   - The `/api/v1/videos/stream/range/{videoId}` endpoint allows streaming in chunks.
   - This prevents easy direct file downloads and ensures controlled access to video content.

3. **Cache Control & Security Headers:**
   - Security headers such as `Cache-Control`, `Pragma`, `Expires`, and `X-Content-Type-Options` are set to prevent caching and sniffing.
   - Example response headers:
     ```http
     Content-Range: bytes 0-102399/5000000
     Cache-Control: no-cache, no-store, must-revalidate
     Pragma: no-cache
     Expires: 0
     X-Content-Type-Options: nosniff
     ```

4. **Restricted Direct Access to Segments:**
   - `.m3u8` and `.ts` files are served dynamically through the backend.
   - Unauthorized users cannot directly access them without proper API calls.

## Configuration
Set the following properties in `application.properties`:
```properties
spring.application.name=video-stream-backend

# Database connection
spring.datasource.url=jdbc:mysql://localhost:3306/video_stream_server
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

# JPA Properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Enable Logging
logging.level.com.stream.app=debug

# Maximum file size allowed for a single file upload
spring.servlet.multipart.max-file-size=1000MB

# Maximum request size allowed for multipart/form-data requests
spring.servlet.multipart.max-request-size=1000MB

files.video=videos/
files.video.hls=videos_hls/
```

## Contributing
Contributions are welcome! Follow these steps:
1. Fork the repository.
2. Create a new branch: `git checkout -b feature-name`
3. Commit your changes: `git commit -m 'Add new feature'`
4. Push to the branch: `git push origin feature-name`
5. Open a Pull Request.
