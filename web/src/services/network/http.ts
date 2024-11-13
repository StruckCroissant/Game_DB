export function objectToParams(
  object: string[][] | Record<string, string> | string | URLSearchParams
): string {
  return new URLSearchParams(object).toString();
}
