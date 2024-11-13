import { AxiosError, AxiosResponse } from "axios";

new AxiosError();

export const axiosResponseFactory = (
  data: Record<string, string | number>,
  status = 500,
  statusText = "Internal Server Error",
  headers = {},
  config?: object | undefined
): AxiosResponse => ({
  data,
  status,
  statusText,
  headers,
  config: { ...config },
});

export const errorResponseFactory = (
  data: Record<string, string | number>,
  endpoint: string,
  status = 500
): AxiosError => ({
  message: "error message",
  config: {},
  request: undefined,
  status: status.toString(),
  response: axiosResponseFactory(data, status, "Internal Server Error", {
    url: `https://localhost${endpoint}`,
  }),
  isAxiosError: true,
  toJSON: function (): object {
    throw new Error("Function not implemented.");
  },
  name: "AxiosError",
});
