const http = require('http');

const options = {
  hostname: '127.0.0.1',
  port: 8082,
  path: '/influencer-api/v1/dashboard/stats',
  method: 'GET',
  family: 4  // Force IPv4
};

const req = http.request(options, (res) => {
  console.log(`STATUS: ${res.statusCode}`);
  res.on('data', (chunk) => {
    console.log(`BODY: ${chunk}`);
  });
});

req.on('error', (e) => {
  console.error(`ERROR: ${e.message}`);
});

req.end();
