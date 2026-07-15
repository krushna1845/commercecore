import React from 'react';

export function Highlight({ text, query }: { text: string; query?: string }) {
  if (!query) return <>{text}</>;
  const q = query.replace(/[.*+?^${}()|[\\]\\\\]/g, '\\$&').trim();
  if (!q) return <>{text}</>;
  const re = new RegExp(`(${q.split(/\\s+/).join('|')})`, 'ig');
  const parts = text.split(re);
  return <>
    {parts.map((part, i) => re.test(part) ? <mark key={i} className="bg-yellow-200 text-yellow-900">{part}</mark> : <span key={i}>{part}</span>)}
  </>;
}

export default Highlight;
