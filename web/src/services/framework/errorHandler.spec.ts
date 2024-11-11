import { AxiosError } from "axios";
import { handleError } from "./errorHandler";
import { useToast } from "@/stores/toastStore";
import { ProblemError } from "@/types/problem";

vi.mock("@/stores/toastStore");

describe("Error handler tests", () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("Should return problem message", () => {
    const expectedErrorMessage = "test";

    expect(() =>
      handleError(new ProblemError(expectedErrorMessage, expectedErrorMessage))
    ).toThrowError(expectedErrorMessage);

    expect(useToast().error).toHaveBeenCalledWith({
      text: expectedErrorMessage,
    });
  });

  it("Should return axios message", () => {
    const expectedErrorMessage = "test";

    expect(() =>
      handleError(new AxiosError(expectedErrorMessage))
    ).toThrowError(expectedErrorMessage);

    expect(useToast().error).toHaveBeenCalledWith({
      text: expectedErrorMessage,
    });
  });

  it("Should work with default error type", () => {
    const expectedErrorMessage = "An unexpected error occurred";
    const expectedError = new Error(expectedErrorMessage);

    expect(() => handleError(expectedError)).toThrow(expectedErrorMessage);

    expect(useToast().error).toHaveBeenCalledWith({
      text: expectedErrorMessage,
    });
  });
});
