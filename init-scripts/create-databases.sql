-- Create additional databases
CREATE DATABASE bookshopdb;
CREATE DATABASE pricingdb;
CREATE DATABASE userdb;
CREATE DATABASE paymentdb;
CREATE DATABASE notificationdb;

-- Optionally grant privileges to the existing user on these new databases
GRANT ALL PRIVILEGES ON DATABASE bookshopdb TO libraryuser;
GRANT ALL PRIVILEGES ON DATABASE pricingdb TO libraryuser;
GRANT ALL PRIVILEGES ON DATABASE userdb TO libraryuser;
GRANT ALL PRIVILEGES ON DATABASE paymentdb TO libraryuser;
GRANT ALL PRIVILEGES ON DATABASE notificationdb TO libraryuser;