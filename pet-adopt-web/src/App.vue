<template>
  <transition name="route-fade" mode="out-in">
    <router-view :key="$route.path" />
  </transition>
</template>
<script>export default { name: 'App' }</script>
