<template>
  <router-view></router-view>
</template>

<script setup>
</script>

<style>
/* 1. 全局白/浅蓝渐变背景 (干净、通透) */
body {
  margin: 0;
  background: linear-gradient(135deg, #fdfbfb 0%, #e2ebf0 100%) !important;
  color: #303133 !important;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  min-height: 100vh;
}

/* 2. 核心毛玻璃卡片质感 (亚克力效果) */
.el-card {
  background: rgba(255, 255, 255, 0.5) !important; /* 半透明纯白 */
  backdrop-filter: blur(16px) !important;         /* 强力的背景模糊 */
  -webkit-backdrop-filter: blur(16px) !important;
  border: 1px solid rgba(255, 255, 255, 0.8) !important; /* 边缘高光 */
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.04) !important; /* 极柔和的弥散阴影 */
  color: #303133 !important;
}

.el-card h2, .el-card h3 { color: #303133 !important; }

/* 3. 按钮：清爽的渐变浅蓝 */
.el-button--primary {
  background: linear-gradient(135deg, #8bc6ec 0%, #9599e2 100%) !important;
  border: none !important;
  box-shadow: 0 4px 12px rgba(139, 198, 236, 0.4) !important;
  transition: all 0.3s ease !important;
}
.el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(139, 198, 236, 0.6) !important;
}

/* 4. 表格透明化处理 */
.el-table {
  background-color: transparent !important;
  --el-table-border-color: rgba(0, 0, 0, 0.05) !important;
  --el-table-header-bg-color: rgba(255, 255, 255, 0.4) !important;
  --el-table-tr-bg-color: transparent !important;
  --el-table-text-color: #606266 !important;
  --el-table-header-text-color: #303133 !important;
}
.el-table th.el-table__cell, .el-table td.el-table__cell {
  background-color: transparent !important;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05) !important;
}
.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell {
  background: rgba(255, 255, 255, 0.3) !important;
}
.el-table tr:hover > td {
  background-color: rgba(255, 255, 255, 0.6) !important;
}

/* 5. 弹窗和下拉框的毛玻璃 */
.el-dialog, .el-select-dropdown, .el-picker-panel {
  background: rgba(255, 255, 255, 0.75) !important;
  border: 1px solid rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(20px) !important;
  box-shadow: 0 12px 32px rgba(0,0,0,0.08) !important;
}
.el-dialog__title, .el-form-item__label { color: #303133 !important; }
.el-input__wrapper, .el-textarea__inner {
  background-color: rgba(255, 255, 255, 0.6) !important;
  box-shadow: 0 0 0 1px rgba(0,0,0,0.05) inset !important;
}
</style>