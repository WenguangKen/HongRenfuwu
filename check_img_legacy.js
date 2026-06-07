const https = require('https');

const urls = [
  'https://images-na.ssl-images-amazon.com/images/P/B0B5RDY256.01._SL150_.jpg',
  'https://images-na.ssl-images-amazon.com/images/P/B0B5RDY256.01.LZZZZZZZ.jpg',
  'https://images-na.ssl-images-amazon.com/images/P/B0B5RDY256.01.MZZZZZZZ.jpg',
  'https://images-na.ssl-images-amazon.com/images/P/B0B5RDY256.01.TZZZZZZZ.jpg',
  'https://images-na.ssl-images-amazon.com/images/P/B0B5RDY256.01.jpg',
  'https://images.amazon.com/images/P/B0B5RDY256.01.LZZZZZZZ.jpg',
  'https://m.media-amazon.com/images/I/412K1bhxZXL.jpg'
];

urls.forEach((url) => {
  https.get(url, (res) => {
    console.log(`URL: ${url}`);
    console.log(`  Status: ${res.statusCode}`);
    console.log(`  Content-Length: ${res.headers['content-length']}`);
    console.log(`  Content-Type: ${res.headers['content-type']}`);
  }).on('error', (e) => {
    console.log(`URL: ${url} | Error: ${e.message}`);
  });
});
