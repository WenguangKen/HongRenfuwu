import re

with open('C:\\Users\\Administrator\\Desktop\\红人服务系统源码\\frontend\\src\\views\\dashboard\\DashboardView.vue', 'r', encoding='utf-8') as f:
    content = f.read()

script_match = re.search(r'<script setup lang="ts">.*?</script>', content, re.DOTALL)
if script_match:
    print(script_match.group(0))
else:
    print("Script not found")
