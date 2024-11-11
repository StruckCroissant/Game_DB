import { ref, unref } from "vue";
import type { Ref } from "vue";
import { AxiosError } from "axios";
import { axiosInstance as axios } from "@/config/axiosConfig";
import type { AxiosResponse } from "axios";
import { MaybeRefOrGetter } from "vue";
import { toValue } from "vue";
import {
  isAxiosError,
  createProblemErrorFromAxiosError,
  ProblemError,
} from "@/types/problem";
import { objectToParams } from "@/services/network/http";

export interface NetworkComposable {
  error: Ref<AxiosError | null>;
  loading: Ref<boolean>;
}

export interface AxiosComposable<T> extends NetworkComposable {
  response: Ref<AxiosResponse | null>;
  data: Ref<T | null>;
}

interface AxiosBaseComposable<T> extends AxiosComposable<T> {
  doAction: <T>(
    request: GetRequest | PostRequest,
    data?: Record<string, number | string | symbol> | number | string | null
  ) => Promise<T | null>;
}

export interface UseFetch<T> extends AxiosComposable<T> {
  getData: (
    manualEndpoint?: MaybeRefOrGetter<string> | null,
    params?: MaybeRefOrGetter<Record<string, string>> | null
  ) => Promise<T | null>;
}

export interface UsePost<T> extends AxiosComposable<T> {
  postData: (
    manualEndopint?: MaybeRefOrGetter<string> | null,
    manualData?: MaybeRefOrGetter<object> | null
  ) => Promise<T | null>;
}

interface Request {
  endpoint: string;
}

interface PostRequest extends Request {
  type: "post";
  data: object;
}

interface GetRequest extends Request {
  type: "get";
}

const noop = (...args: any[]) => null;

export function useRequest<T = unknown, R = AxiosResponse<T>>(
  options: {
    onError: (error: AxiosError) => void;
    onSuccess: (response: AxiosResponse) => void;
  } = { onError: noop, onSuccess: noop }
): AxiosBaseComposable<T> {
  const { onError, onSuccess } = options;

  const data: Ref<T | null> = ref(null);
  const response: Ref<AxiosResponse<T> | null> = ref(null);
  const error: Ref<AxiosError | null> = ref(null);
  const loading: Ref<boolean> = ref(false);

  const doAction = async <T>(
    request: GetRequest | PostRequest
  ): Promise<T | null> => {
    try {
      loading.value = true;

      let serverResponse = null;
      switch (request.type) {
        case "get":
          serverResponse = await axios.get(request.endpoint);
          break;
        case "post":
          serverResponse = await axios.post(request.endpoint, request.data);
          break;
      }

      response.value = serverResponse;
      data.value = serverResponse.data;
      error.value = null;
      onSuccess(serverResponse);
    } catch (errorResponse) {
      if (!isAxiosError(errorResponse)) throw errorResponse;

      error.value = errorResponse;
      onError(errorResponse);
      throw unref(error.value);
    } finally {
      loading.value = false;
    }

    return (data.value as T) ?? error.value;
  };

  return {
    data,
    response,
    error,
    loading,
    doAction,
  };
}

export function useFetch<T>(
  endpoint: MaybeRefOrGetter<string>,
  params?: MaybeRefOrGetter<Record<string, string | string>> | null
): UseFetch<T> {
  const { doAction, ...rest } = useRequest<T>();

  const getData = async (
    manualEndpoint?: MaybeRefOrGetter<string> | null,
    manualParams?: MaybeRefOrGetter<Record<string, string | string>> | null
  ) => {
    let delegateEndpoint = toValue(manualEndpoint) ?? toValue(endpoint);
    const delegateParams = toValue(manualParams) ?? toValue(params);

    if (delegateParams) delegateEndpoint = "?" + objectToParams(delegateParams);

    return await doAction<T>({
      type: "get",
      endpoint: delegateEndpoint,
    });
  };

  return {
    getData,
    ...rest,
  };
}

export function usePost<T = unknown>(
  endpoint: MaybeRefOrGetter<string>,
  reactiveInputData?: MaybeRefOrGetter<object>
): UsePost<T> {
  const { doAction, ...rest } = useRequest<T | null>();

  const postData = async (
    manualEndpoint?: MaybeRefOrGetter<string> | null,
    manualData?: MaybeRefOrGetter<object> | null
  ) => {
    const data = manualData ?? toValue(reactiveInputData);
    if (!data) throw new Error("You must provide data for a post action");

    return await doAction<T>({
      type: "post",
      endpoint: toValue(manualEndpoint) ?? toValue(endpoint),
      data,
    });
  };

  return {
    postData,
    ...rest,
  };
}
