<script setup lang="ts">
import { toRef } from "vue";
import { useLoadingDelay } from "@/composables/useLoadingDelay";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { library } from "@fortawesome/fontawesome-svg-core";
import { faCircleCheck } from "@fortawesome/free-solid-svg-icons";
library.add(faCircleCheck);

interface Props {
  loading?: boolean;
  label?: string;
  error?: boolean;
  loadingSuccessText?: string;
  loadingSuccessIcon?: string;
  persistentLabel?: string;
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  label: "button",
  error: false,
  loadingSuccessText: "Success",
  loadingSuccessIcon: "circle-check",
});

const loading = toRef(props, "loading");

const { loadingFinished } = useLoadingDelay(loading);
</script>

<template>
  <button id="button" :aria-label="label">
    <slot v-if="loadingFinished && !error" name="loadSuccess">
      <span>
        {{ loadingSuccessText }}
        <FontAwesomeIcon :icon="loadingSuccessIcon" data-testid="success" />
      </span>
    </slot>
    <slot v-else-if="!loading"></slot>
    <slot v-else name="loading">
      <span class="loader loader--small" data-testid="spinner"></span>
    </slot>
  </button>
</template>

<style scoped>
#button {
  font-weight: bold;
}
</style>
