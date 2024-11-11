import { useToast } from "@/stores/toastStore";

export function handleError(error: Error): void {
  const toastStore = useToast();

  toastStore.error({ text: error.message });
  throw error;
}
