<script setup lang="ts">
import { ref } from "vue";
import { library } from "@fortawesome/fontawesome-svg-core";
import { faEye, faEyeSlash } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { useField } from "vee-validate";
import _ from "lodash";
import type { InputTypeHTMLAttribute, Ref } from "vue";
library.add(faEye, faEyeSlash);

interface Props {
  name: string;
  type?: InputTypeHTMLAttribute | undefined;
  initialValue?: string;
  label?: string;
}

const props = withDefaults(defineProps<Props>(), {
  type: "text",
  initialValue: "",
  label: "",
});

const inputId = _.uniqueId(props.name);

const passwordShown: Ref<boolean> = ref(false);
const isPasswordInput: boolean = props.type === "password";
const concreteType: Ref<string> = ref(props.type);

function togglePasswordShown(): void {
  concreteType.value = passwordShown.value ? "password" : "text";
  passwordShown.value = !passwordShown.value;
}

//#region Form Context
const { value, errorMessage } = useField(props.name, undefined, {
  initialValue: props.initialValue,
});
//#endregion
</script>

<template>
  <div :class="['rounded-input', errorMessage ? 'rounded-input--error' : '']">
    <div class="rounded-input__input-container">
      <input
        :id="inputId"
        :name="name"
        :type="concreteType"
        :placeholder="label"
        :aria-label="name"
        v-model="value"
        v-bind="$attrs"
        class="rounded-input__input"
      />
      <div
        v-if="isPasswordInput"
        @click="togglePasswordShown"
        class="rounded-input__end-icon"
      >
        <FontAwesomeIcon
          :icon="passwordShown ? faEyeSlash : faEye"
          aria-label="show password"
        />
      </div>
    </div>
    <div
      v-if="errorMessage"
      :aria-label="`${name}-error`"
      class="rounded-input__invalid-message"
      role="alert"
    >
      {{ errorMessage }}
    </div>
  </div>
</template>

<style scoped></style>
