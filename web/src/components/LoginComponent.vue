<script lang="ts" setup>
import { reactive } from "vue";
import { useForm } from "vee-validate";
import { toTypedSchema } from "@vee-validate/zod";
import { useLogin } from "@/composables/authentication/useAuthentication";
import { RouterLink, useRouter } from "vue-router";
import InputComponent from "@/components/UI/InputComponent.vue";
import ModalComponent from "@/components/UI/ModalComponent.vue";
import ButtonComponent from "@/components/UI/ButtonComponent.vue";
import { userLoginSchema } from "@/types/user";
import type { UserLoginRequest } from "@/types/user";

//#region Routing
const { push } = useRouter();
//#endregion

//#region Form Context
const { handleSubmit, values } = useForm({
  validationSchema: toTypedSchema(userLoginSchema),
});

let loginRequest: UserLoginRequest = reactive({
  username: values.username ?? "",
  password: values.password ?? "",
});

const { doLogin, error, loading } = useLogin(loginRequest);

const onSubmit = handleSubmit(async (values) => {
  loginRequest.username = values.username;
  loginRequest.password = values.password;
  await doLogin();
  push({ name: "home" });
});
//#endregion
</script>

<template>
  <ModalComponent>
    <template #header>
      <label><strong>Login</strong></label>
    </template>
    <template #default>
      <div class="form form--centered">
        <div class="input-group">
          <InputComponent
            name="username"
            type="text"
            label="Username"
          ></InputComponent>
          <div>
            <InputComponent
              name="password"
              type="password"
              label="Password"
            ></InputComponent>
            <RouterLink to="/register" class="form__text float-right">
              Forgot password?
            </RouterLink>
          </div>
        </div>
        <ButtonComponent
          :loading="loading"
          :error="!!error"
          label="Log in"
          type="submit"
          class="gradient-button gradient-button--raised gradient-button--animated"
          @click="onSubmit"
        >
          Log in
        </ButtonComponent>
        <div class="d-flex form__text">
          Dont have an account? <RouterLink to="/register">Create</RouterLink>
        </div>
      </div>
    </template>
  </ModalComponent>
</template>

<style lang="scss" scoped></style>
