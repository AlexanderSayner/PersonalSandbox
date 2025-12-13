print('Creating reviewerdb database, reviews collection, and user...');
db = db.getSiblingDB('reviewerdb');
db.createCollection('reviews');
db.createUser({
  user: 'revieweruser',
  pwd: 'reviewerpwd',
  roles: [{ role: 'readWrite', db: 'reviewerdb' }]
});
print('Done.');
