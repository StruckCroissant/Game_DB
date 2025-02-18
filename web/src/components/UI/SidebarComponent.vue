<script lang="ts" setup>
import {
  faAnglesRight,
  faAnglesLeft,
  faGear,
  faRightFromBracket,
  faHouseChimney,
} from "@fortawesome/free-solid-svg-icons";
import type { IconDefinition } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { computed, onBeforeMount, onBeforeUnmount, reactive, ref } from "vue";
import * as _ from "lodash";
import { usePersistentState } from "@/composables/usePersistentState";
import { RouteNames } from "@/router/routes";
import { firstLetterUppercase } from "@/utilities/common";

const sidebar = ref<HTMLElement | null>(null);

onBeforeMount(() => {
  window.addEventListener("resize", _.debounce(reactToCoverage, 200));
});
onBeforeUnmount(() => {
  window.removeEventListener("resize", _.debounce(reactToCoverage, 200));
});

function reactToCoverage() {
  if (sidebar.value === null) return;

  const sidebarCoverage = (sidebar.value.clientWidth / window.innerWidth) * 100;
  if (sidebarCoverage > 25) {
    close();
  }
}

const { persistentValue: sidebarOpen } = usePersistentState<boolean>(
  "sidebar/open",
  true
);

const documentTitle = computed(() => document.title);
const buttonIcon = computed(() =>
  sidebarOpen.value ? faAnglesLeft : faAnglesRight
);

function open() {
  sidebarOpen.value = true;
}

function close() {
  sidebarOpen.value = false;
}

type NavOption = {
  name: string;
  selected: boolean;
  icon: IconDefinition;
};

const options = reactive<NavOption[]>([
  {
    name: RouteNames.HOME,
    selected: true,
    icon: faHouseChimney,
  },
]);
</script>

<template>
  <TransitionGroup name="slide">
    <section
      class="sidebar"
      ref="sidebar"
      key="sidebar-content"
      :class="($attrs.class, !sidebarOpen ? 'sidebar--closed' : '')"
    >
      <header class="sidebar__header">
        <span>{{ documentTitle }}</span>
        <button class="button" @click="close()">
          <FontAwesomeIcon :icon="buttonIcon" />
        </button>
      </header>
      <nav class="sidebar__item-container">
        <RouterLink
          v-for="option in options"
          class="sidebar__item"
          :to="option.name"
          :key="option.name"
          :class="[option.selected ? 'sidebar__item--selected' : '']"
        >
          {{ firstLetterUppercase(option.name) }}
          <FontAwesomeIcon :icon="option.icon" />
        </RouterLink>
      </nav>
      <footer class="sidebar__footer">
        <RouterLink to="logout">
          <FontAwesomeIcon :icon="faRightFromBracket" />
        </RouterLink>
        <FontAwesomeIcon :icon="faGear" />
      </footer>
    </section>
    <div class="sidebar-tab" v-if="!sidebarOpen" key="sidebar-tab">
      <button
        class="button button--left-attach sidebar__collapse-button"
        @click="open()"
      >
        <FontAwesomeIcon :icon="buttonIcon" />
      </button>
    </div>
  </TransitionGroup>
</template>

<style lang="scss" scoped>
@import "@/styles/abstracts/variables";
@import "@/styles/abstracts/mixins";
@import "@/styles/abstracts/placeholders";

%transition-speed {
  transition: all 0.3s ease;
}

.slide-enter-active,
.slide-leave-active {
  transform: translateX(-100%);
  position: absolute;
}

.sidebar {
  @extend %transition-speed;
  @include box-shadow($grey--light);
  $header-color-base: #e091ffe8;
  transition: all 0.3s ease;

  position: sticky;
  left: 0;
  top: 0;
  height: 100vh;
  border-radius: 0 10px 10px 0;
  background-color: white;
  overflow: hidden;
  flex-direction: column;
  border-right: 1px solid $grey--light;
  display: flex;
  min-width: 220px;
  width: 220px;
  visibility: visible;

  &--closed {
    width: 0;
    min-width: 0px;
    visibility: hidden;
  }

  &__header {
    height: 60px;
    width: 100%;
    align-items: center;
    justify-content: space-between;
    display: flex;
    padding: 0.5rem;
    font-size: large;
    font-weight: bold;
    border-bottom: 1px solid $grey--light;
  }

  &__footer {
    @extend %float-down;
    display: flex;
    align-items: center;
    border-top: 1px solid $grey--light;
    padding: 0.5rem 0.75rem;
    justify-content: space-between;

    > svg {
      height: 20px;
    }
  }

  &__header svg,
  &__footer svg {
    color: $grey;
  }

  &__item-container {
    padding: 0.75rem;
  }

  &__item {
    font-weight: bold;
    font-size: large;
    display: flex;
    text-align: center;
    border-radius: 5px;
    padding: 0.35rem 0.75rem;
    margin-bottom: 0.5rem;
    background-color: rgb(240 240 240);
    border: 2px solid rgb(240 240 240);

    &:hover {
      filter: brightness(115%);
    }

    > i,
    > svg {
      @extend %float-right;
    }

    &--selected {
      border: 2px solid #afafaf;
    }
  }
}

.sidebar-tab {
  @extend %transition-speed;
  position: absolute;
  margin-top: 10px;
}
</style>
