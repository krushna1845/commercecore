const KEY = 'search_history_v1';

export function recordSearchLocal(q: string) {
  if (!q) return;
  try {
    const raw = localStorage.getItem(KEY);
    const arr = raw ? JSON.parse(raw) as string[] : [];
    const filtered = arr.filter(x => x !== q);
    filtered.unshift(q);
    const sliced = filtered.slice(0, 50);
    localStorage.setItem(KEY, JSON.stringify(sliced));
  } catch (e) {}
}

export function getRecentLocal(limit = 10) {
  try {
    const raw = localStorage.getItem(KEY);
    const arr = raw ? JSON.parse(raw) as string[] : [];
    return arr.slice(0, limit);
  } catch (e) { return []; }
}

export function clearLocalHistory() { localStorage.removeItem(KEY); }
