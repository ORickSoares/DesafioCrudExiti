// script.js - toggle + confirmar exclusão + atualização do label do botão
(function() {
  const THEME_KEY = 'theme';

  function applyThemeSync() {
    const theme = localStorage.getItem(THEME_KEY);
    if (theme === 'dark') {
      document.documentElement.setAttribute('data-theme', 'dark');
    } else {
      document.documentElement.removeAttribute('data-theme');
    }
    updateToggleLabel();
  }

  function updateToggleLabel() {
    const btn = document.getElementById('theme-toggle');
    if (!btn) return;
    const current = document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
    btn.innerText = current === 'dark' ? '🌙 Dark' : '☀️ Light';
  }

  function toggleTheme() {
    const current = document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
    const next = current === 'dark' ? 'light' : 'dark';
    localStorage.setItem(THEME_KEY, next);
    applyThemeSync();
  }

  // apply immediately (safety in case inline snippet not present)
  try { applyThemeSync(); } catch(e) {}

  document.addEventListener('DOMContentLoaded', function(){
    const btn = document.getElementById('theme-toggle');
    if (btn) btn.addEventListener('click', function(e){ e.preventDefault(); toggleTheme(); });
    // ensure label is correct
    updateToggleLabel();
  });
})();

function confirmarExclusao() {
  return confirm('Deseja realmente excluir este usuário? Esta ação não pode ser desfeita.');
}
