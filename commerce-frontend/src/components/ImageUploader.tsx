import { useCallback, useState } from 'react';
import { Upload, X, Loader2 } from 'lucide-react';
import { cn } from '@/lib/utils';

interface ImageUploaderProps {
  value?: string;
  onChange: (url: string) => void;
  onUpload: (file: File) => Promise<string>;
}

export function ImageUploader({ value, onChange, onUpload }: ImageUploaderProps) {
  const [dragging, setDragging] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');

  const handleFile = useCallback(async (file: File) => {
    if (!file.type.startsWith('image/')) {
      setError('Please upload an image file');
      return;
    }
    setError('');
    setUploading(true);
    try {
      const url = await onUpload(file);
      onChange(url);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Upload failed');
    } finally {
      setUploading(false);
    }
  }, [onChange, onUpload]);

  const onDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setDragging(false);
    const file = e.dataTransfer.files[0];
    if (file) handleFile(file);
  };

  return (
    <div className="space-y-2">
      <div
        onDragOver={(e) => { e.preventDefault(); setDragging(true); }}
        onDragLeave={() => setDragging(false)}
        onDrop={onDrop}
        className={cn(
          'relative border-2 border-dashed rounded-xl p-6 text-center transition-colors',
          dragging ? 'border-primary bg-primary/5' : 'border-muted-foreground/25',
          uploading && 'opacity-60 pointer-events-none'
        )}
      >
        {uploading ? (
          <div className="flex flex-col items-center gap-2 py-4">
            <Loader2 className="w-8 h-8 animate-spin text-primary" />
            <p className="text-sm text-muted-foreground">Uploading to Cloudinary...</p>
          </div>
        ) : value ? (
          <div className="relative inline-block">
            <img src={value} alt="Preview" className="h-32 w-32 rounded-lg object-cover mx-auto" />
            <button
              type="button"
              onClick={() => onChange('')}
              className="absolute -top-2 -right-2 p-1 rounded-full bg-destructive text-destructive-foreground"
            >
              <X size={14} />
            </button>
          </div>
        ) : (
          <label className="cursor-pointer flex flex-col items-center gap-2">
            <Upload className="w-8 h-8 text-muted-foreground" />
            <span className="text-sm font-medium">Drag & drop product image</span>
            <span className="text-xs text-muted-foreground">or click to browse</span>
            <input
              type="file"
              accept="image/*"
              className="hidden"
              onChange={(e) => e.target.files?.[0] && handleFile(e.target.files[0])}
            />
          </label>
        )}
      </div>
      {error && <p className="text-xs text-destructive">{error}</p>}
      {!value && (
        <input
          type="url"
          placeholder="Or paste image URL"
          className="w-full text-sm px-3 py-2 rounded-md border bg-background"
          onBlur={(e) => e.target.value && onChange(e.target.value)}
        />
      )}
    </div>
  );
}
