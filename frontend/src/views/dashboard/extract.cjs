const fs = require('fs');
const content = fs.readFileSync('C:\\\\Users\\\\Administrator\\\\Desktop\\\\红人服务系统源码\\\\frontend\\\\src\\\\views\\\\dashboard\\\\DashboardView.vue', 'utf8');
const start = content.indexOf('<script setup lang="ts">');
const end = content.indexOf('</script>', start) + 9;
console.log(content.substring(start, end));
