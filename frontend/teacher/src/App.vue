<template>
  <div class="app-shell">
    <header class="app-header">
      <strong>移动互动课堂 · 教师端</strong>
      <span class="app-tip">教师端初始化成功</span>
    </header>
    <main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped>
.app-shell {
  display: flex;
  flex-direction: column;
  min-height: 100%;
}

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 20px;
  background: #ffffff;
  border-bottom: 1px solid #e5e8ef;
}

.app-tip {
  color: var(--color-primary, #2f6fed);
  font-size: 14px;
}

.app-main {
  flex: 1;
  padding: 20px;
}
</style>
