import React from 'react';

export function MarkdownRenderer({ input }: { input: string }) {
  const lines = input.split(/\r?\n/);
  return (
    <div className="markdown prose prose-slate dark:prose-invert">
      {lines.map((line, index) => {
        if (line.startsWith('### ')) return <h3 key={index}>{line.substring(4)}</h3>;
        if (line.startsWith('## ')) return <h2 key={index}>{line.substring(3)}</h2>;
        if (line.startsWith('# ')) return <h1 key={index}>{line.substring(2)}</h1>;
        if (line.startsWith('- ')) return <li key={index}>{line.substring(2)}</li>;
        const boldLine = line.replace(/\*\*(.*?)\*\*/g, (_, text) => `<strong>${text}</strong>`);
        const linked = boldLine.replace(/\[(.*?)\]\((.*?)\)/g, (_, text, href) => `<a href=\"${href}\" target=\"_blank\" rel=\"noreferrer\">${text}</a>`);
        return <p key={index} dangerouslySetInnerHTML={{ __html: linked }} />;
      })}
    </div>
  );
}
