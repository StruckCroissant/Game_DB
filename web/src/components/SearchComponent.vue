<script lang="ts" setup>
import InputComponent from "@/components/UI/InputComponent.vue";
import Button from "@/components/UI/ButtonComponent.vue";
import { useFetch } from "@/composables/network/useFetch";
import { Game, PaginatedGameResponse } from "@/types/game";
import { reactive, toRaw, toRefs } from "vue";

const gameSearchParams = reactive({
  name: "",
});

const {} = useFetch<PaginatedGameResponse>("/game", () => ({
  query: toRaw(gameSearchParams),
}));
</script>

<template>
  <section>
    <div style="font-size: xx-large; font-weight: bold">Search</div>
    <div class="search-container">
      <InputComponent name="search" style="width: 100%"></InputComponent>
      <Button
        class="gradient-button gradient-button--raised gradient-button--sharper"
      >
        Search
      </Button>
    </div>
  </section>
</template>

<style lang="scss" scoped>
.search-container {
  display: flex;
  flex-direction: row;
  gap: 0.5rem;
  padding: 1rem 0;
  width: 100%;
}
</style>
