<script lang="ts" setup>
import { reactive } from "vue";
import { useForm } from "vee-validate";
import { useRouter } from "vue-router";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { faArrowLeftLong } from "@fortawesome/free-solid-svg-icons";
import { useRegister } from "@/composables/authentication/useAuthentication";
import { userLoginSchema } from "@/types/user";
import { toTypedSchema } from "@vee-validate/zod";
import InputComponent from "@/components/UI/InputComponent.vue";
import ButtonComponent from "@/components/UI/ButtonComponent.vue";
import ModalComponent from "./UI/ModalComponent.vue";
import type { UserLoginRequest } from "@/types/user";

//#region Form validation
const { values, handleSubmit } = useForm({
  validationSchema: toTypedSchema(userLoginSchema),
});
//#endregion

//#region Registration
const registerRequest: UserLoginRequest = reactive({
  username: values.username ?? "",
  password: values.password ?? "",
});

const { push } = useRouter();
const { loading, error, doRegister } = useRegister(registerRequest);

const onSubmit = handleSubmit(async (values) => {
  [registerRequest.username, registerRequest.password] = [
    values.username,
    values.password,
  ];

  await doRegister();
  push({ name: "login" });
});
//#endregion
</script>

<template>
  <ModalComponent>
    <template #header>
      <a
        id="back-button"
        data-testid="back-link"
        alt="Go back"
        aria-role="link"
        @click="push({ name: 'login' })"
      >
        <FontAwesomeIcon :icon="faArrowLeftLong" />
      </a>
      <label><strong>Register</strong></label>
    </template>
    <template #default>
      <form class="form form--centered" @submit="onSubmit">
        <div class="input-group">
          <InputComponent label="Username" name="username"></InputComponent>
          <InputComponent
            label="Password"
            name="password"
            type="password"
          ></InputComponent>
          <ButtonComponent
            :loading="loading"
            :error="!!error"
            label="Register"
            type="submit"
            class="gradient-button gradient-button--raised gradient-button--animated"
          >
            Register
          </ButtonComponent>
        </div>
      </form>
    </template>
    <template #footer> </template>
  </ModalComponent>
</template>

<style lang="scss" scoped>
@import "@/styles/abstracts/variables";

#back-button {
  color: $grey;
  position: fixed;
  top: 5px;
  left: 5px;
  cursor: pointer;
  font-size: x-large;
}
</style>
