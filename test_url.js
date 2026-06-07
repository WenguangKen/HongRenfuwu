const https = require('https');

const options = {
  hostname: 'ws-na.amazon-adsystem.com',
  port: 443,
  path: '/widgets/q?ASIN=B0B5RDY256&Format=_SL150_&ID=AsinImage&MarketPlace=US',
  method: 'GET',
  headers: {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
    'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
    'Accept-Language': 'en-US,en;q=0.5',
    'Connection': 'keep-alive'
  }
};

https.get(options, (res) => {
  console.log('StatusCode:', res.statusCode);
  console.log('Headers:', res.headers);
  if (res.headers.location) {
    console.log('Redirect location:', res.headers.location);
  }
}).on('error', (e) => {
  console.error('Error:', e);
});
