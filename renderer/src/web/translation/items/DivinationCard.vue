<template>
  <div id="div-card">
    <br />
    <table>
      <tr><th><img :src="item.info.icon" style="object-fit: none;" /></th></tr>
      <tr><td><br /></td></tr>
      <tr><th>{{ item.name }}</th></tr>
      <tr><td><br /></td></tr>
      <tr><td>{{ item.stackSize }}</td></tr>
      <tr><td><br /></td></tr>
      <tr><th>{{ item.reward }}</th></tr>
      <tr><br /></tr>
      <tr v-for="line in item.description"><td>{{ line }}</td></tr>
    </table>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType, computed } from 'vue'
import { DivinationCard, ParsedItem } from '@/parser/ParsedItem';

export default defineComponent({
  name: 'DivinationCard',
  props: {
    item: {
      type: Object as PropType<ParsedItem>,
      required: true
    }
  },
  setup(props) {

    const item = computed(() => {
      return props.item as DivinationCard
    })

    return {
      item
    }
  }
})
</script>
