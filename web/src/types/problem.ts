import * as z from "zod";
import { AxiosError } from "axios";

export const problemSchema = z.object({
  title: z.string(),
  detail: z.string(),
  status: z.number(),
  type: z.string().optional(),
  instance: z.string().optional(),
  timestamp: z.string().optional(),
});

export type Problem = z.infer<typeof problemSchema>;

export class ProblemError extends Error {
  #description: string;

  constructor(message: string, description: string) {
    super(message);
    this.#description = description;
  }

  get description() {
    return this.#description;
  }

  get isProblemError(): boolean {
    return true;
  }
}

export function createProblemErrorFromProblem(problem: Problem): ProblemError {
  return new ProblemError(problem.detail, problem.title);
}

export function createProblemErrorFromAxiosError(
  error: AxiosError | null
): ProblemError | null {
  const response = error?.response;

  const isProblemResponse = response?.headers?.["content-type"]?.includes(
    "application/problem+json"
  );
  if (isProblemResponse) {
    return createProblemErrorFromProblem(response?.data as Problem);
  }
  return null;
}

export const isAxiosError = (error: unknown): error is AxiosError =>
  (error as AxiosError)?.isAxiosError ?? false;

export const isProblemError = (error: unknown): error is ProblemError =>
  (error as ProblemError)?.isProblemError ?? false;
