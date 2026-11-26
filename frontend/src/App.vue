<script setup>
import { computed } from 'vue'
import { RouterLink, RouterView, useRoute } from 'vue-router'

const route = useRoute()
const title = computed(() => route.meta.title ?? '')
</script>

<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="brand">Admin</div>
      <nav>
        <RouterLink to="/" class="nav-item">대시보드</RouterLink>
        <RouterLink to="/members" class="nav-item">회원 관리</RouterLink>
      </nav>
    </aside>

    <div class="main">
      <header class="topbar">
        <h1>{{ title }}</h1>
        <span class="user">관리자님</span>
      </header>
      <main class="content">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<style scoped>
.layout { display: flex; min-height: 100vh; }

.sidebar {
  width: 220px;
  flex-shrink: 0;
  background: var(--sidebar-bg);
  color: #cbd5e1;
  padding: 20px 12px;
}
.brand { font-size: 20px; font-weight: 700; color: #fff; padding: 0 12px 20px; }
.nav-item {
  display: block;
  padding: 10px 12px;
  border-radius: 6px;
  color: inherit;
  text-decoration: none;
  margin-bottom: 4px;
}
.nav-item:hover { background: rgba(255, 255, 255, 0.06); }
.nav-item.router-link-exact-active { background: var(--primary); color: #fff; }

.main { flex: 1; min-width: 0; display: flex; flex-direction: column; }
.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 28px;
  background: var(--surface);
  border-bottom: 1px solid var(--border);
}
.topbar h1 { font-size: 18px; margin: 0; }
.user { color: var(--muted); font-size: 14px; }
.content { padding: 24px 28px; }

@media (max-width: 760px) {
  .layout { flex-direction: column; }
  .sidebar { width: auto; display: flex; align-items: center; gap: 8px; padding: 10px 16px; }
  .brand { padding: 0 12px 0 0; }
  .sidebar nav { display: flex; gap: 4px; }
  .nav-item { margin: 0; }
  .topbar, .content { padding-left: 16px; padding-right: 16px; }
}
</style>
