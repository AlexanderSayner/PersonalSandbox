# PostgreSQL Initialization Scripts

This directory contains initialization scripts for the PostgreSQL container.

## Files

- `create-databases.sql` - Creates additional databases alongside the default `librarydb`

## Databases Created

During container initialization, the following databases are created:

- `librarydb` (default, defined by POSTGRES_DB environment variable)
- `bookshopdb`
- `pricingdb`
- `userdb`
- `paymentdb`
- `notificationdb`

All databases are accessible using the same credentials (libraryuser/librarypass).