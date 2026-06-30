export function LoadingState({ message = "Loading..." }: { message?: string }) {
  return (
    <div className="flex min-h-48 items-center justify-center rounded-xl border border-dashed">
      <p className="text-sm text-muted-foreground">{message}</p>
    </div>
  );
}
