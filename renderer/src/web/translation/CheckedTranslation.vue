<template>
  <div v-if="show" class="p-4 layout-column min-h-0" id="checked-translation">
    <DivinationCard v-if="item.category === ItemCategory.DivinationCard" :item="item" />
    <StackableCurrency v-if="item.category === ItemCategory.StackableCurrency" :item="item" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
// import { useI18n } from 'vue-i18n'
import { ItemCategory, ItemRarity, ParsedItem } from '@/parser'
import DivinationCard from '@/web/translation/items/DivinationCard.vue'
import StackableCurrency from '@/web/translation/items/StackableCurrency.vue'

const props = defineProps<{
  item: ParsedItem
}>()

const show = computed(() => {
  return !(props.item.rarity === ItemRarity.Unique &&
    props.item.isUnidentified &&
    props.item.info?.unique == null)
})

// const { t } = useI18n()

// const translations = computed(() => {
//   return props.item.rawText.split('\r\n')
// })

const item = computed(() => {
  return props.item
})

// const isDivCard = computed(() => {
//   return props.item.category === ItemCategory.DivinationCard
// })
</script>
