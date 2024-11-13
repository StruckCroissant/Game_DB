import { axiosInstance } from "@/config/axiosConfig";
import { useFetch, usePost } from "./useDataFetch";
import { AxiosError, AxiosResponse } from "axios";
import { vi } from "vitest";
import { ref } from "vue";
import * as defaultAxiosProblemData from "~/mocks/fixtures/problem.json";
import {
  errorResponseFactory,
  axiosResponseFactory,
} from "~/mocks/factories/axios";
import { createProblemErrorFromProblem } from "@/types/problem";
import { waitFor } from "@testing-library/vue";

vi.mock("@/config/axiosConfig");

const problemData = {
  type: "about:blank",
  title: "error",
  detail: "error",
  status: 500,
  instance: "waow",
  timestamp: "now",
};

const errorResponse = errorResponseFactory(problemData);

describe("UseDataFetch error tests", () => {
  it("useFetch should set state on problem format errors", async () => {
    vi.mocked(axiosInstance.get).mockRejectedValue(
      new AxiosError(
        errorResponse.message,
        errorResponse.status,
        errorResponse.config,
        errorResponse.request,
        axiosResponseFactory(defaultAxiosProblemData, 500, "error", {
          "content-type": "application/problem+json",
        })
      )
    );

    const fetchResult = useFetch(ref("test"));
    expect(fetchResult.getData()).rejects.toStrictEqual(
      createProblemErrorFromProblem(defaultAxiosProblemData)
    );
    await waitFor(() => {
      expect(fetchResult.error.value).toStrictEqual(
        createProblemErrorFromProblem(defaultAxiosProblemData)
      );
    });
  });

  it("usePost should set state on problem format errors", async () => {
    vi.mocked(axiosInstance.get).mockRejectedValue(
      new AxiosError(
        errorResponse.message,
        errorResponse.status,
        errorResponse.config,
        errorResponse.request,
        axiosResponseFactory(problemData, undefined, undefined, {
          "content-type": "application/problem+json",
        })
      )
    );
    const fetchResult = useFetch(ref("test"));
    expect(fetchResult.getData()).rejects.toStrictEqual(
      createProblemErrorFromProblem(problemData)
    );

    await waitFor(() => {
      expect(fetchResult.error.value).toStrictEqual(
        createProblemErrorFromProblem(problemData)
      );
    });
  });

  it("useFetch should rethrow unexpected error", async () => {
    const mockError = { test: "test" };
    vi.mocked(axiosInstance.get).mockResolvedValue(mockError);

    const fetchResult = useFetch(ref("test"));
    expect(fetchResult.getData()).rejects.toStrictEqual(mockError);
  });

  it("usePost should rethrow unexpected error", () => {
    const mockError = { test: "test" };
    vi.mocked(axiosInstance.post).mockResolvedValue(mockError);

    const fetchResult = usePost(ref("test"));
    expect(
      fetchResult.postData(null, ref({ test: "test" }))
    ).rejects.toStrictEqual(mockError);
  });
});
