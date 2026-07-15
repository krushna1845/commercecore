import React, { useMemo, useState } from 'react';
import { useAssistant } from '../hooks/useAssistant';
import { MarkdownRenderer } from './MarkdownRenderer';
import { Send, Sparkles, Moon, Sun } from 'lucide-react';

const samplePrompts = [
  'I need shoes under ₹3000.',
  'Suggest a birthday gift.',
  'I want gaming laptops.',
  'Compare iPhone and Samsung.',
  'Help me checkout with available offers.',
];

export function AIShoppingAssistant() {
  const { messages, draft, setDraft, isTyping, sendMessage, streamMessage, error, resetConversation, canSend } = useAssistant();
  const [darkMode, setDarkMode] = useState(false);

  const themeClass = darkMode ? 'dark bg-slate-950 text-slate-100' : 'bg-slate-50 text-slate-900';

  const handlePromptClick = (prompt: string) => {
    sendMessage(prompt).catch(() => {});
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!canSend) return;
    await sendMessage(draft);
  };

  const statusText = useMemo(() => {
    if (isTyping) return 'Assistant is typing...';
    return 'AI Shopping Assistant';
  }, [isTyping]);

  return (
    <section className={`rounded-3xl border ${darkMode ? 'border-slate-800' : 'border-slate-200'} p-4 shadow-lg ${themeClass}`}>
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h2 className="text-2xl font-semibold">AI Shopping Assistant</h2>
          <p className="text-sm text-slate-500 dark:text-slate-400">Chat naturally and get product recommendations, comparisons, order help, and checkout advice.</p>
        </div>
        <button type="button" onClick={() => setDarkMode(!darkMode)} className="inline-flex items-center gap-2 rounded-full border px-3 py-2 text-sm transition hover:bg-slate-100 dark:hover:bg-slate-800">
          {darkMode ? <Sun size={16} /> : <Moon size={16} />} {darkMode ? 'Light' : 'Dark'} mode
        </button>
      </div>

      <div className="mt-4 grid gap-4 lg:grid-cols-[1.3fr_0.7fr]">
        <div className="space-y-4">
          <div className={`rounded-3xl border p-4 shadow-sm ${darkMode ? 'border-slate-800 bg-slate-900' : 'border-slate-200 bg-white'} dark:bg-slate-900`}>
            <div className="flex items-center justify-between">
              <span className="text-sm font-medium text-slate-600 dark:text-slate-300">{statusText}</span>
              <Sparkles className="text-primary" size={18} />
            </div>
            <div className="mt-4 space-y-4 max-h-[480px] overflow-y-auto pr-2">
              {messages.map((message) => (
                <div key={message.id} className={`rounded-3xl p-4 ${message.role === 'user' ? 'bg-slate-100 text-slate-900 self-end dark:bg-slate-800 dark:text-slate-100' : 'bg-slate-900 text-slate-100 dark:bg-slate-700'}`}>
                  <div className="mb-3 text-xs uppercase tracking-[0.2em] text-slate-500 dark:text-slate-400">{message.role === 'user' ? 'You' : 'Assistant'}</div>
                  <MarkdownRenderer input={message.text} />
                  {message.cards?.length ? (
                    <div className="mt-4 grid gap-3 sm:grid-cols-2">
                      {message.cards.map(card => (
                        <div key={card.id} className="overflow-hidden rounded-3xl border border-slate-200 bg-white text-slate-900 dark:border-slate-800 dark:bg-slate-950 dark:text-slate-100">
                          <img src={card.imageUrl} alt={card.name} className="h-32 w-full object-cover" />
                          <div className="p-3">
                            <div className="font-semibold">{card.name}</div>
                            <div className="mt-1 text-sm text-slate-500 dark:text-slate-400">{card.description}</div>
                            <div className="mt-3 flex items-center justify-between gap-2 text-sm">
                              <span className="font-semibold">₹{card.price}</span>
                              <span className={`rounded-full px-2 py-1 text-[11px] ${card.inStock ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-500/20 dark:text-emerald-200' : 'bg-rose-100 text-rose-800 dark:bg-rose-500/20 dark:text-rose-200'}`}>{card.inStock ? 'In stock' : 'Out of stock'}</span>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : null}
                </div>
              ))}
            </div>
          </div>

          <form onSubmit={handleSubmit} className="flex flex-col gap-3">
            <label className="sr-only" htmlFor="assistant-input">Ask the assistant</label>
            <textarea
              id="assistant-input"
              rows={3}
              value={draft}
              onChange={(event) => setDraft(event.target.value)}
              placeholder="Ask for product recommendations, comparisons, order help, or offers..."
              className="min-h-[100px] w-full rounded-3xl border px-4 py-3 text-sm outline-none focus:border-slate-400 focus:ring-2 focus:ring-slate-200 dark:border-slate-800 dark:bg-slate-950 dark:text-slate-100 dark:focus:ring-slate-700"
            />
            <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
              <button type="submit" disabled={!canSend} className="inline-flex items-center justify-center gap-2 rounded-3xl bg-slate-900 px-5 py-3 text-sm font-semibold text-white transition hover:bg-slate-700 disabled:cursor-not-allowed disabled:opacity-50 dark:bg-slate-100 dark:text-slate-950 dark:hover:bg-slate-200">
                <Send size={16} /> {isTyping ? 'Typing...' : 'Send'}
              </button>
              <button type="button" onClick={resetConversation} className="rounded-3xl border px-4 py-3 text-sm text-slate-600 transition hover:border-slate-400 hover:text-slate-900 dark:border-slate-700 dark:text-slate-300 dark:hover:border-slate-500 dark:hover:text-slate-100">
                Clear chat
              </button>
            </div>
            {error && <p className="text-sm text-rose-500">{error}</p>}
          </form>
        </div>

        <aside className={`rounded-3xl border p-4 shadow-sm ${darkMode ? 'border-slate-800 bg-slate-900' : 'border-slate-200 bg-white'}`}>
          <div className="mb-4 flex items-center justify-between">
            <h3 className="text-lg font-semibold">Quick prompts</h3>
            <span className="text-xs uppercase tracking-[0.25em] text-slate-500 dark:text-slate-400">Try</span>
          </div>
          <div className="grid gap-3 text-sm">
            {samplePrompts.map(prompt => (
              <button key={prompt} type="button" onClick={() => handlePromptClick(prompt)} className="rounded-3xl border px-4 py-3 text-left transition hover:border-slate-300 dark:border-slate-700 dark:hover:border-slate-500">
                {prompt}
              </button>
            ))}
          </div>
        </aside>
      </div>
    </section>
  );
}
