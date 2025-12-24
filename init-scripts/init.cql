CREATE KEYSPACE IF NOT EXISTS workshop_keyspace
WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1};
