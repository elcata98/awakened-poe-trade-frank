<template>
  <div v-if="show" class="p-4 layout-column min-h-0">
    <!-- <label>{{ item }}</label>
    <br/> -->
    <label>{{ item.info.name }}</label>
    <br/>
    <label>{{ item.info.unique?.base }}</label>
    <img :src="item.info.icon" style="object-fit: none;"/>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType, computed} from 'vue'
import { useI18n } from 'vue-i18n'
import { ItemRarity, ParsedItem } from '@/parser'

export default defineComponent({
  name: 'CheckedTranslation',
  props: {
    item: {
      type: Object as PropType<ParsedItem>,
      required: true
    }
  },
  setup (props) {

    const show = computed(() => {
      return !(props.item.rarity === ItemRarity.Unique &&
        props.item.isUnidentified &&
        props.item.info.unique == null)
    })

    const { t } = useI18n()

    const translations = computed(() => {
      return props.item.rawText.split('\r\n')
    })

    const item = computed(() => {
      return props.item
    })

    return {
      t,
      translations,
      show,
      item
    }
  }
})
</script>
