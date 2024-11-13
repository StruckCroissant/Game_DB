<script setup lang="ts">
import { RouterView } from "vue-router";
import SidebarComponent from "@/components/UI/SidebarComponent.vue";
</script>

<template>
  <section class="container">
    <SidebarComponent class="container__sidebar" />
    <RouterView class="container__content" />
  </section>
</template>

<style lang="scss" scoped>
.container {
  display: flex;

  &__content {
    padding: 3rem 5rem;
    width: 100vw;
    height: 100vh;
  }
}
</style>
