const fs = require('fs');
const path = require('path');

const filePath = path.join(__dirname, 'DashboardView.vue');
let content = fs.readFileSync(filePath, 'utf8');

const cssStartMarker = '/* --- Hero Section & Welcome Banner --- */';
const cssEndMarker = '/* --- Main Chart Card & Bottom Cards --- */';

const startIndex = content.indexOf(cssStartMarker);
const endIndex = content.indexOf(cssEndMarker);

if (startIndex !== -1 && endIndex !== -1) {
  const newCss = `/* --- V2 Command Center Layout CSS --- */
.mb-24 { margin-bottom: 24px; }

.vertical-stats-panel {
  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: baseline;
    
    .title-text {
      font-size: 16px;
      font-weight: 800;
      color: #0f172a;
    }
    
    .subtitle-text {
      font-size: 13px;
      font-weight: 600;
      color: #94a3b8;
    }
  }

  .vertical-stats-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .v-stat-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    border-radius: 12px;
    background: #f8fafc;
    border: 1px solid transparent;
    transition: all 0.3s ease;

    &:hover {
      background: #ffffff;
      border-color: #e2e8f0;
      box-shadow: 0 10px 15px -3px rgba(0,0,0,0.05);
      transform: translateY(-2px);
    }

    .v-stat-icon {
      width: 44px;
      height: 44px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 20px;
      background: #ffffff;
      border: 1px solid #e2e8f0;
      box-shadow: 0 2px 4px rgba(0,0,0,0.02);
      flex-shrink: 0;

      &.cyan { color: #06b6d4; box-shadow: inset 0 0 0 1px rgba(6, 182, 212, 0.1); }
      &.blue { color: #3b82f6; box-shadow: inset 0 0 0 1px rgba(59, 130, 246, 0.1); }
      &.purple { color: #8b5cf6; box-shadow: inset 0 0 0 1px rgba(139, 92, 246, 0.1); }
      &.pink { color: #ec4899; box-shadow: inset 0 0 0 1px rgba(236, 72, 153, 0.1); }
    }

    .v-stat-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 4px;

      .v-stat-label {
        font-size: 13px;
        font-weight: 600;
        color: #64748b;
      }

      .v-stat-value {
        font-size: 20px;
        font-weight: 800;
        color: #0f172a;
        line-height: 1.2;
        letter-spacing: -0.5px;
      }
    }

    .v-stat-trend {
      .trend-badge {
        display: inline-flex;
        align-items: center;
        gap: 4px;
        padding: 4px 8px;
        border-radius: 6px;
        font-size: 12px;
        font-weight: 700;

        &.up { background: #ecfdf5; color: #059669; border: 1px solid #d1fae5; }
        &.down { background: #fff1f2; color: #e11d48; border: 1px solid #ffe4e6; }
        &.neutral { background: #f1f5f9; color: #64748b; border: 1px solid #e2e8f0; }
      }
    }
  }
}

`;

  const before = content.substring(0, startIndex);
  const after = content.substring(endIndex);

  fs.writeFileSync(filePath, before + newCss + after, 'utf8');
  console.log('CSS replaced successfully');
} else {
  console.log('Could not find markers', startIndex, endIndex);
}
