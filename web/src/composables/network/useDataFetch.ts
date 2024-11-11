import { isDataResponse } from "@/types/request";
import {
  useRequest as wrappedUseRequest,
  useFetch as wrappedUseFetch,
  usePost as wrappedUsePost,
} from "./useFetch";
import type { AxiosError, AxiosResponse } from "axios";
import {
  createProblemErrorFromAxiosError,
  ProblemError,
} from "@/types/problem";
import { computed, ComputedRef, MaybeRefOrGetter, toValue } from "vue";
import { objectToParams } from "@/services/network/http";

const errorTransformer = (error: AxiosError | null) => {
  const problemError = createProblemErrorFromAxiosError(error);
  if (!problemError) {
    return error;
  }

  return problemError;
};

const successTransformer = (response: AxiosResponse) => {
  if (!isDataResponse(response)) throw response;
};

function useRequest<T = unknown, R = AxiosResponse<T>>(...args: unknown[]) {
  const { error: wrappedError, ...rest } = wrappedUseRequest<T, R>({
    onError: errorTransformer,
    onSuccess: successTransformer,
    ...args,
  });

  const error: ComputedRef<AxiosError | ProblemError | null> = computed(() =>
    errorTransformer(wrappedError.value)
  );

  return {
    error,
    ...rest,
  };
}

export function useFetch<T = unknown>(
  endpoint: MaybeRefOrGetter<string>,
  params?: MaybeRefOrGetter<Record<string, string | string>> | null
) {
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
) {
  const { doAction, ...rest } = useRequest<T>();

  const postData = async (
    manualEndpoint?: MaybeRefOrGetter<string> | null,
    manualData?: MaybeRefOrGetter<object> | null
  ) => {
    const data = manualData ?? toValue(reactiveInputData);
    if (!data) throw new Error("You must provide data for a post action");

    return await doAction({
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
