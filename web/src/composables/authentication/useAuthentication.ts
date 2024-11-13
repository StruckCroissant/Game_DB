import { useAuthenticationStore } from "@/stores/authentication";
import { usePost } from "@/composables/network/useDataFetch";
import { toValue } from "vue";
import type { User, UserLoginRequest } from "@/types/user";
import type { MaybeRefOrGetter } from "vue";

export function useLogin(loginRequest: MaybeRefOrGetter<UserLoginRequest>) {
  const authStore = useAuthenticationStore();
  const login = usePost<User>("/login", loginRequest);

  const doLogin = async () => {
    const loginRequestData = toValue(loginRequest);
    const loginData = await login.postData();
    authStore.addBasicAuth(
      loginRequestData.username,
      loginRequestData.password
    );
    return loginData;
  };

  return {
    ...login,
    doLogin,
  };
}

export function useRegister(
  registerRequest: MaybeRefOrGetter<UserLoginRequest>
) {
  const register = usePost<boolean>("/register", registerRequest);

  return {
    ...register,
    doRegister: register.postData,
  };
}

export function logout() {
  const authStore = useAuthenticationStore();
  authStore.removeAuthToken();
}
