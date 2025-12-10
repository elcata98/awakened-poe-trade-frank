<template>
  <div id="div-card">
    <br />
    <table>
      <th><img :src="item.info.icon" style="object-fit: none;" /></th>
      <tr><br /></tr>
      <th>{{ item.name }}</th>
      <tr><br /></tr>
      <tr>{{ item.stackSize }}</tr>
      <tr><br /></tr>
      <th>{{ item.reward }}</th>
      <tr><br /></tr>
      <tr>{{ item.description }}</tr>
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
