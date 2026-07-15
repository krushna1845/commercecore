import React, { useEffect, useRef, useState } from 'react';
import { useSearch } from '../hooks/use-search';
import { Highlight } from '../utils/highlight';
import { startVoiceRecognition, stopVoiceRecognition } from '../utils/voice';
import { recordSearchLocal, getRecentLocal } from '../utils/searchHistory';

type Props = {
  placeholder?: string;
  onSelect?: (product: any) => void;
  initialQuery?: string;
}

export function Search({ placeholder = 'Search products, brands, categories...', onSelect, initialQuery }: Props) {
  const { query, setQuery, results, loading, suggestions, loadMore, total, page } = useSearch({ q: initialQuery });
  const [showDropdown, setShowDropdown] = useState(false);
  const [listening, setListening] = useState(false);
  const recogRef = useRef<any>(null);
  const containerRef = useRef<HTMLDivElement | null>(null);
  const [recent] = useState(() => getRecentLocal(10));

  useEffect(() => {
    const onClick = (e: MouseEvent) => {
      if (!containerRef.current) return;
      if (!containerRef.current.contains(e.target as Node)) setShowDropdown(false);
    };
    window.addEventListener('click', onClick);
    return () => window.removeEventListener('click', onClick);
  }, []);

  useEffect(() => {
    if (results.length > 0) {
      setShowDropdown(true);
    }
  }, [results]);

  const handleVoice = () => {
    if (listening) { stopVoiceRecognition(recogRef.current); setListening(false); return; }
    recogRef.current = startVoiceRecognition((text) => {
      setQuery(text);
      recordSearchLocal(text);
      setListening(false);
    }, () => setListening(false));
    if (recogRef.current) setListening(true);
  };

  const handleSelect = (p: any) => {
    recordSearchLocal(query);
    // optionally call backend record (fire and forget)
    fetch('/api/search/record', { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({ q: query }) });
    setShowDropdown(false);
    onSelect && onSelect(p);
  };

  return (
    <div ref={containerRef} className="relative w-full max-w-xl">
      <div className="flex items-center gap-2">
        <input
          aria-label="search"
          placeholder={placeholder}
          value={query}
          onChange={e => setQuery(e.target.value)}
          onFocus={() => setShowDropdown(true)}
          className="w-full rounded-md border px-3 py-2"
        />
        <button onClick={handleVoice} title="Voice search" className={`p-2 rounded ${listening ? 'bg-red-200' : 'bg-muted/20'}`}>
          {listening ? 'Listening...' : '🎤'}
        </button>
      </div>

      {showDropdown && (
        <div className="absolute z-40 mt-2 w-full bg-white border rounded shadow-lg max-h-96 overflow-auto">
          <div className="p-2">
            <div className="mb-2 text-sm text-muted">Suggestions</div>
            <div className="flex flex-col gap-1">
              {(suggestions?.terms || []).map((t: string) => (
                <button key={t} className="text-left p-2 hover:bg-muted/50 rounded" onClick={() => setQuery(t)}>
                  <Highlight text={t} query={query} />
                </button>
              ))}
            </div>
          </div>

          <div className="border-t p-2">
            <div className="mb-2 text-sm text-muted">Results</div>
            {loading && <div className="p-2">Loading…</div>}
            {results.map((r:any) => (
              <div key={r.id} className="p-2 flex items-center gap-3 hover:bg-muted/50 cursor-pointer" onClick={() => handleSelect(r)}>
                <img src={r.imageUrl} alt="" className="w-12 h-12 object-cover rounded" />
                <div>
                  <div className="font-medium"><Highlight text={r.name} query={query} /></div>
                  <div className="text-sm text-muted">${r.price.toFixed(2)}</div>
                </div>
              </div>
            ))}
            {results.length > 0 && results.length < total && (
              <div className="p-2 text-center">
                <button onClick={() => loadMore()} className="text-sm text-primary">Load more</button>
              </div>
            )}
            {results.length === 0 && !loading && (
              <div className="p-4 text-sm text-muted">No results</div>
            )}
          </div>

          <div className="border-t p-2 text-sm text-muted">
            Recent
            <div className="flex gap-2 mt-2 flex-wrap">
              {recent.map(r => <button key={r} className="p-1 px-2 bg-muted/20 rounded text-xs" onClick={() => setQuery(r)}>{r}</button>)}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Search;
