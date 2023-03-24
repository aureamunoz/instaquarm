-- SELECT pg_ls_dir('META-INF.resources');
-- SHOW  data_directory;

INSERT INTO picture(id, title, created) VALUES (1, 'screenshot-1',to_timestamp('2022-10-08 22:35:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO picture(id, title, created) VALUES (2, 'screenshot-2',to_timestamp('2022-07-13 23:10:00','YYYY-MM-DD HH24:MI:SS'));
INSERT INTO picture(id, title, created) VALUES (3, 'screenshot-3',to_timestamp('2022-05-20 21:30:00','YYYY-MM-DD HH24:MI:SS'));
-- This doesn't work automatically because the image needs to be copied to the specific container.
-- INSERT INTO picture(id, title, created, image) VALUES (4, 'screenshot-4',to_timestamp('2022-10-08 10:30:00','YYYY-MM-DD HH24:MI:SS'), pg_read_binary_file('/var/lib/postgresql/data/ejemplo.png'));

