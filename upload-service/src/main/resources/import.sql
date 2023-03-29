

-- This won't work automatically, we need to copy the images into the container running the database
-- INSERT INTO picture(title, created, image) VALUES ('Amsterdam',to_timestamp('2022-10-08 10:30:00','YYYY-MM-DD HH24:MI:SS'), pg_read_binary_file('/var/lib/postgresql/data/2023-Devoxxfr/Amsterdam.jpg'));
-- INSERT INTO picture(title, created, image) VALUES ('Barcelona',to_timestamp('2022-10-08 10:30:00','YYYY-MM-DD HH24:MI:SS'), pg_read_binary_file('/var/lib/postgresql/data/2023-Devoxxfr/Barcelona.JPG'));
-- INSERT INTO picture(title, created, image) VALUES ('London',to_timestamp('2022-10-08 10:30:00','YYYY-MM-DD HH24:MI:SS'), pg_read_binary_file('/var/lib/postgresql/data/2023-Devoxxfr/London.jpg'));
-- INSERT INTO picture(title, created, image) VALUES ('Madrid',to_timestamp('2022-10-08 10:30:00','YYYY-MM-DD HH24:MI:SS'), pg_read_binary_file('/var/lib/postgresql/data/2023-Devoxxfr/Madrid.jpg'));
-- INSERT INTO picture(title, created, image) VALUES ('NewYork',to_timestamp('2022-10-08 10:30:00','YYYY-MM-DD HH24:MI:SS'), pg_read_binary_file('/var/lib/postgresql/data/2023-Devoxxfr/NewYork.jpg'));
-- INSERT INTO picture(title, created, image) VALUES ('Paris',to_timestamp('2022-10-08 10:30:00','YYYY-MM-DD HH24:MI:SS'), pg_read_binary_file('/var/lib/postgresql/data/2023-Devoxxfr/Paris.jpg'));


