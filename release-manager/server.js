const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');
const { exec, spawn } = require('child_process');
const net = require('net');

const app = express();
const port = 3005;

app.use(cors());
app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));

app.get('/favicon.ico', (req, res) => res.status(204).end());

const configPath = path.join(__dirname, 'config.json');
const ROOT_DIR = path.join(__dirname, '..');

const getConfig = () => JSON.parse(fs.readFileSync(configPath, 'utf8'));
const saveConfig = (cfg) => fs.writeFileSync(configPath, JSON.stringify(cfg, null, 2));

const getToolEnv = () => {
  const extraPaths = [
    path.join(process.env.LOCALAPPDATA || '', 'Programs', 'apache-maven', 'bin'),
    path.join(process.env.ProgramFiles || 'C:\\Program Files', 'nodejs'),
    path.join(process.env.ProgramFiles || 'C:\\Program Files', 'Java', 'jdk-17', 'bin'),
  ].filter((p) => fs.existsSync(p));

  const javaHome = path.join(process.env.ProgramFiles || 'C:\\Program Files', 'Java', 'jdk-17');
  const env = { ...process.env };
  if (fs.existsSync(javaHome)) env.JAVA_HOME = javaHome;

  const pathKey = Object.keys(env).find((k) => k.toUpperCase() === 'PATH') || 'Path';
  const existingPath = env[pathKey] || '';
  const systemRoots = [];
  const winRoot = process.env.SystemRoot || process.env.WINDIR;
  if (winRoot) {
    const sys32 = path.join(winRoot, 'System32');
    const ps = path.join(sys32, 'WindowsPowerShell', 'v1.0');
    if (fs.existsSync(sys32)) systemRoots.push(sys32);
    if (fs.existsSync(ps)) systemRoots.push(ps);
  }
  for (const k of Object.keys(env)) {
    if (k.toUpperCase() === 'PATH') delete env[k];
  }
  env[pathKey] = [...extraPaths, ...systemRoots, existingPath].filter(Boolean).join(path.delimiter);
  return env;
};

const sseClients = new Set();
const latencyHistory = {};
const globalErrorLogs = [];

function checkPortLatency(svcPort) {
  return new Promise((resolve) => {
    const start = Date.now();
    const socket = new net.Socket();
    socket.setTimeout(800);
    socket.on('connect', () => { resolve(Date.now() - start); socket.destroy(); });
    socket.on('error', () => resolve(-1));
    socket.on('timeout', () => { socket.destroy(); resolve(-1); });
    socket.connect(svcPort, '127.0.0.1');
  });
}

setInterval(async () => {
  try {
    const config = getConfig();
    for (const svc of Object.values(config.services)) {
      const ms = await checkPortLatency(svc.port);
      if (!latencyHistory[svc.name]) latencyHistory[svc.name] = [];
      latencyHistory[svc.name].push(ms);
      if (latencyHistory[svc.name].length > 20) latencyHistory[svc.name].shift();
    }
  } catch (e) {}
}, 2000);

app.get('/api/stream', (req, res) => {
  res.writeHead(200, { 'Content-Type': 'text/event-stream', 'Cache-Control': 'no-cache', 'Connection': 'keep-alive' });
  sseClients.add(res);
  req.on('close', () => sseClients.delete(res));
});

const NOISE_PATTERNS = [
  /^Progress \(\d+\):/,
  /^Downloading from/,
  /^Downloaded from/,
  /^Uploading to/,
  /^Uploaded to/,
];

function isNoise(line) {
  const trimmed = line.trim();
  if (!trimmed) return true;
  return NOISE_PATTERNS.some((re) => re.test(trimmed));
}

function broadcastLog(serviceName, type, message) {
  const rawLines = message.split(/\r?\n/);
  const lines = [];
  for (const raw of rawLines) {
    const parts = raw.split('\r').filter((s) => s.length > 0);
    const lastPart = parts.length > 0 ? parts[parts.length - 1] : raw;
    if (isNoise(lastPart)) continue;
    if (lastPart.trim().length === 0) continue;
    lines.push(lastPart);
  }
  lines.forEach(line => {
    const logObj = { service: serviceName, type, message: line.trim(), time: new Date().toLocaleTimeString() };
    if (type === 'error' || logObj.message.toLowerCase().includes('exception') || logObj.message.toLowerCase().includes('error :')) {
      globalErrorLogs.unshift(logObj);
      if (globalErrorLogs.length > 200) globalErrorLogs.pop();
    }
    if (sseClients.size > 0) {
      const payload = JSON.stringify(logObj);
      sseClients.forEach(client => client.write(`data: ${payload}\n\n`));
    }
  });
}

const stopServiceProcess = (svcPort) => {
  return new Promise((resolve) => {
    exec(`netstat -ano | findstr :${svcPort}`, (error, stdout) => {
      if (error || !stdout) return resolve(true);
      const lines = stdout.trim().split('\n');
      let pidToKill = null;
      for (let line of lines) {
        if (line.includes('LISTENING')) {
          const parts = line.trim().split(/\s+/);
          pidToKill = parts[parts.length - 1];
          break;
        }
      }
      if (pidToKill) {
        exec(`taskkill /PID ${pidToKill} /F`, () => resolve(true));
      } else {
        resolve(true);
      }
    });
  });
};

const startLocalProcess = (svc, envStr) => {
    const processEnv = getToolEnv();
    
    let command = '';
    let cwd = ROOT_DIR;
    
    if (svc.type === 'backend') {
      cwd = path.join(ROOT_DIR, svc.path);
      const jarPath = path.join(cwd, svc.jarPath);
      if (!fs.existsSync(jarPath)) {
          broadcastLog(svc.name, 'error', `未找到JAR包位置: ${jarPath}。 请先使用 BUILD 按钮进行构建打包。`);
          return;
      }
      command = `java -jar "${jarPath}"`;
    } else {
      cwd = path.join(ROOT_DIR, svc.path);
      command = `npm run dev`;
    }

    broadcastLog(svc.name, 'system', `========== 正在本地启动: ${svc.name} ==========`);
    
    try {
        const proc = spawn(command, { cwd, shell: true, env: processEnv });
        proc.stdout.on('data', data => broadcastLog(svc.name, 'info', data.toString()));
        proc.stderr.on('data', data => broadcastLog(svc.name, 'error', data.toString()));
        proc.on('error', err => broadcastLog(svc.name, 'error', `启动失败: ${err.message}`));
        proc.on('close', code => broadcastLog(svc.name, 'system', `进程退出，错误码 ${code}`));
    } catch(e) {
        broadcastLog(svc.name, 'error', e.message);
    }
};

const PROD_HOST = '120.78.204.148';
const PROD_USER = 'root';
const PROD_PASS = 'HONGren@2025';
const PROD_BASE = '/data/influencer-marketing';

async function deployServiceToProd(svc) {
    return new Promise((resolve, reject) => {
        broadcastLog(svc.name, 'system', `🚀 开始向云端生产环境发行 [${svc.name}]...`);
        let command = '';
        
        if (svc.type === 'backend') {
            const jarName = path.basename(svc.jarPath);
            const localJar = path.join(ROOT_DIR, svc.path, svc.jarPath);
            
            if (!fs.existsSync(localJar)) {
                broadcastLog(svc.name, 'error', `未找到要发布的文件: ${localJar}`);
                return reject(new Error('Jar Not Found'));
            }
            
            command = `
                sshpass -p "${PROD_PASS}" scp -o StrictHostKeyChecking=no "${localJar}" ${PROD_USER}@${PROD_HOST}:${PROD_BASE}/ &&
                sshpass -p "${PROD_PASS}" ssh -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} "
                    cd ${PROD_BASE}
                    ps -ef | grep ${jarName} | grep -v grep | awk '{print \\$2}' | xargs -r kill -9
                    sleep 2
                    nohup java -jar ${jarName} --spring.profiles.active=prod > ${jarName}.log 2>&1 &
                    echo 'SUCCESS: ${svc.name} is started'
                "
            `.trim().replace(/\n/g, ' ');
        } else {
            command = `
                cd "${path.join(ROOT_DIR, svc.path)}" &&
                tar -czvf frontend-dist.tar.gz dist/ &&
                sshpass -p "${PROD_PASS}" scp -o StrictHostKeyChecking=no frontend-dist.tar.gz ${PROD_USER}@${PROD_HOST}:${PROD_BASE}/ &&
                sshpass -p "${PROD_PASS}" ssh -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} "
                    cd ${PROD_BASE}
                    tar -xzvf frontend-dist.tar.gz
                    rm -rf frontend-dist.tar.gz
                    echo 'SUCCESS: ${svc.name} is deployed'
                "
            `.trim().replace(/\n/g, ' ');
        }

        const proc = spawn('powershell.exe', ['-Command', command], { cwd: ROOT_DIR, env: getToolEnv() });
        
        proc.stdout.on('data', data => broadcastLog(svc.name, 'info', data.toString()));
        proc.stderr.on('data', data => broadcastLog(svc.name, 'info', data.toString())); 
        
        proc.on('close', code => {
            if (code === 0) {
                broadcastLog(svc.name, 'system', `✅ 云端部署成功！`);
                resolve(true);
            } else {
                broadcastLog(svc.name, 'error', `❌ 部署过程发生异常，退出码: ${code}`);
                resolve(false);
            }
        });
    });
}

app.get('/api/errors', (req, res) => res.json(globalErrorLogs));
app.delete('/api/errors', (req, res) => { globalErrorLogs.length = 0; res.json({ success: true }); });

app.get('/api/services', async (req, res) => {
  try {
    const config = getConfig();
    const servicesArray = [];
    for (const [key, svc] of Object.entries(config.services)) {
      const latencies = latencyHistory[svc.name] || [];
      const isRunning = latencies.length > 0 && latencies[latencies.length - 1] > -1;
      servicesArray.push({ id: key, ...svc, isRunning, latencyTrend: latencies });
    }
    servicesArray.sort((a, b) => a.startOrder - b.startOrder);
    res.json({ project: config.project, version: config.currentVersion, environments: Object.keys(config.environments), services: servicesArray });
  } catch (error) { res.status(500).json({ error: error.message }); }
});

app.post('/api/build', async (req, res) => {
  const { id, env } = req.body;
  const config = getConfig();
  const svc = config.services[id];
  if (!svc) return res.status(404).json({ error: 'Service not found' });
  
  if (env === 'production' || env === 'staging') {
      broadcastLog(svc.name, 'system', `🚀 发起线上自动化流水线打包及部署...`);
  }
  
  let targetDir = path.join(ROOT_DIR, svc.path);
  if (svc.type === 'backend' && svc.jarPath) {
      const jarFullPath = path.join(ROOT_DIR, svc.path, svc.jarPath);
      if (fs.existsSync(jarFullPath)) {
        try {
          fs.unlinkSync(jarFullPath);
        } catch (e) {
          broadcastLog(svc.name, 'system', `旧JAR文件正在被占用，跳过删除（Maven构建会覆盖）`);
        }
      }
  }

  broadcastLog(svc.name, 'system', `========== 初始化构建: ${svc.name} ==========`);
  const proc = spawn('powershell.exe', ['-Command', svc.buildCmd], { cwd: targetDir, env: getToolEnv() });
  let hasSentResponse = false;

  proc.stdout.on('data', data => broadcastLog(svc.name, 'info', data.toString()));
  proc.stderr.on('data', data => broadcastLog(svc.name, 'error', data.toString()));
  proc.on('close', async (code) => {
    broadcastLog(svc.name, 'system', `构建作业完成，状态码 ${code}`);
    if (code === 0) {
      if (env === 'production' || env === 'staging') {
          const deployOk = await deployServiceToProd(svc);
          if (deployOk) {
              if(!hasSentResponse) { hasSentResponse = true; res.json({ success: true, message: `线上部署成功` }); }
          } else {
              if(!hasSentResponse) { hasSentResponse = true; res.status(500).json({ error: `云端传输或启动失败` }); }
          }
      } else {
          if(!hasSentResponse) { hasSentResponse = true; res.json({ success: true, message: `本地打包完成` }); }
      }
    } else {
      if(!hasSentResponse) { hasSentResponse = true; res.status(500).json({ error: `编译被阻断` }); }
    }
  });
});

app.post('/api/start', async (req, res) => {
  const { id, env } = req.body;
  const config = getConfig();
  const svc = config.services[id];
  if (!svc) return res.status(404).json({ error: 'Service not found' });
  
  if (env === 'production' || env === 'staging') {
      const ok = await deployServiceToProd(svc);
      return ok ? res.json({ success: true, message: '部署指令已送达云环境并执行' }) : res.status(500).json({ error: '云端投递失败' });
  }

  startLocalProcess(svc, env);
  res.json({ success: true, message: `正在本地后台启动 ${svc.name}` });
});

app.post('/api/stop', async (req, res) => {
  const { id, env } = req.body;
  const config = getConfig();
  const svc = config.services[id];
  if (!svc) return res.status(404).json({ error: 'Service not found' });
  
  const ok = await stopServiceProcess(svc.port);
  if (ok) {
     broadcastLog(svc.name, 'system', `========== 已停止服务 ${svc.name} ==========`);
     res.json({ success: true, message: `已成功终止服务进程` });
  } else {
     res.status(500).json({ error: '终止进程失败，可能权限不足或未运行' });
  }
});

app.post('/api/restart', async (req, res) => {
  const { id, env } = req.body;
  const config = getConfig();
  const svc = config.services[id];
  if (!svc) return res.status(404).json({ error: 'Service not found' });
  
  if (env === 'production' || env === 'staging') {
      const ok = await deployServiceToProd(svc);
      return ok ? res.json({ success: true, message: '远程重启指令已执行' }) : res.status(500).json({ error: '远端通信失败' });
  }

  broadcastLog(svc.name, 'system', `========== 初始化平滑重启: ${svc.name} ==========`);
  await stopServiceProcess(svc.port);
  setTimeout(() => {
    startLocalProcess(svc, env);
    res.json({ success: true, message: `已开始本地重启` });
  }, 1000);
});

app.get('/api/ngrok/status', async (req, res) => {
  try {
    const r = await fetch('http://127.0.0.1:4040/api/tunnels');
    const data = await r.json();
    const t = (data.tunnels || []).find((x) => x.public_url && x.public_url.startsWith('https://'));
    res.json({ running: !!t, url: t ? t.public_url : null });
  } catch (_) {
    res.json({ running: false, url: null, message: 'ngrok 未运行，请执行: docker start ngrok-tunnel' });
  }
});

app.listen(port, () => console.log(`🚀 FWGL Admin Panel running at http://localhost:${port}`));
