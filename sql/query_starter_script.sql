-- Drop user first if they exist
DROP USER if exists 'video'@'%' ;

-- Now create user with prop privileges
CREATE USER 'video'@'%' IDENTIFIED BY 'admin';

GRANT ALL PRIVILEGES ON * . * TO 'video'@'%';

-- Create schema
CREATE SCHEMA `video_stream_server` ;