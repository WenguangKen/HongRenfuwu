const https = require('https');

const asin = 'B0B5RDY256';
const url = `https://www.amazon.com/dp/${asin}`;

const options = {
  headers: {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36',
    'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
    'Accept-Language': 'en-US,en;q=0.5'
  }
};

https.get(url, options, (res) => {
  console.log('Status Code:', res.statusCode);
  let data = '';
  res.on('data', (chunk) => { data += chunk; });
  res.on('end', () => {
    console.log('Data length:', data.length);
    // Extract image URLs
    const matches = data.match(/https:\/\/m\.media-amazon\.com\/images\/I\/[a-zA-Z0-9\-_%]+(\.[a-zA-Z0-9\-_%]+)?\.jpg/g);
    console.log('Matches:', matches ? [...new Set(matches)].slice(0, 10) : 'None');
  });
}).on('error', (e) => {
  console.error('Error:', e);
});
