export function LoadingSkeleton() {
  return (
    <div className="space-y-6">
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {Array.from({ length: 4 }).map((_, index) => (
          <div key={index} className="animate-pulse rounded-[2rem] bg-surface p-6">
            <div className="h-48 rounded-3xl bg-muted" />
            <div className="mt-4 space-y-3">
              <div className="h-4 w-3/4 rounded-full bg-muted" />
              <div className="h-4 w-1/2 rounded-full bg-muted" />
              <div className="h-10 rounded-2xl bg-muted" />
            </div>
          </div>
        ))}
      </div>
      <div className="rounded-[2rem] border border-white/10 bg-white/10 p-8 shadow-[0_24px_60px_-40px_rgba(15,23,42,0.35)] backdrop-blur-xl">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div className="space-y-3">
            <div className="h-6 w-52 rounded-full bg-muted" />
            <div className="h-4 w-72 rounded-full bg-muted" />
          </div>
          <div className="h-12 w-40 rounded-full bg-muted" />
        </div>
      </div>
    </div>
  );
}
