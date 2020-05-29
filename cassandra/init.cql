CREATE KEYSPACE fraud
WITH REPLICATION = {
  'class' : 'SimpleStrategy',
  'replication_factor' : 2
};

CREATE TABLE fraud.bots (
   ip text,
   marked_at timestamp,
   PRIMARY KEY (ip, marked_at)
)
WITH CLUSTERING ORDER BY (marked_at ASC);
