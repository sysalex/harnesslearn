<script setup lang="ts">
import { useAppStore } from '../stores/app'

const appStore = useAppStore()
</script>

<template>
  <section class="login-shell">
    <div class="login-panel">
      <p class="login-panel__eyebrow">Harness Engineering Demo</p>
      <h1 class="login-panel__title">欢迎登录考勤系统</h1>
      <p class="login-panel__subtitle">
        当前是前端占位页面，后续任务会接入真实认证流程。
      </p>

      <el-form label-position="top" class="login-panel__form">
        <el-form-item label="账号">
          <el-input placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" class="login-panel__button" disabled>
          {{ appStore.appName }} 即将开放登录
        </el-button>
      </el-form>
    </div>
  </section>
</template>

<style scoped>
.login-shell {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 24px;
  background:
    radial-gradient(circle at top, rgba(96, 165, 250, 0.35), transparent 36%),
    linear-gradient(145deg, #f8fbff 0%, #dbeafe 45%, #eff6ff 100%);
}

.login-panel {
  width: min(100%, 420px);
  padding: 32px;
  border: 1px solid rgba(148, 163, 184, 0.24);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 28px 60px rgba(15, 23, 42, 0.12);
}

.login-panel__eyebrow {
  margin: 0 0 8px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  color: #2563eb;
  text-transform: uppercase;
}

.login-panel__title {
  margin: 0;
  font-size: 32px;
  line-height: 1.15;
  color: #0f172a;
}

.login-panel__subtitle {
  margin: 12px 0 24px;
  color: #475569;
  line-height: 1.6;
}

.login-panel__form {
  display: grid;
  gap: 4px;
}

.login-panel__button {
  width: 100%;
  margin-top: 8px;
}
</style>
