export function startVoiceRecognition(onResult: (text: string) => void, onEnd?: () => void) {
  const SpeechRecognition = (window as any).webkitSpeechRecognition || (window as any).SpeechRecognition;
  if (!SpeechRecognition) return null;
  const r = new SpeechRecognition();
  r.lang = 'en-US';
  r.interimResults = false;
  r.maxAlternatives = 1;
  r.onresult = (e: any) => {
    const t = e.results[0][0].transcript;
    onResult(t);
  };
  r.onerror = () => onEnd && onEnd();
  r.onend = () => onEnd && onEnd();
  r.start();
  return r;
}

export function stopVoiceRecognition(r: any) {
  try { r && r.stop(); } catch (e) {}
}
