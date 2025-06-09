<template>
  <div class="p-2 flex flex-col h-full items-center">
    <div class="flex flex-col items-center p-2 mb-4">
      <img class="w-12 h-12" src="/images/TransferOrb.png">
      <p class="text-base">Awakened PoE Trade Frank</p>
      <p class="">{{ t('app.version', [version]) }}</p>
      <div class="flex gap-2"></div>
    </div>
    <div class="text-center mt-auto py-8">
      <p>{{ t('app.contact_me') }} <br><span class="font-sans text-gray-500 select-all">&lt;@295216259795124225&gt;</span></p>
      <ul class="flex gap-4">
        <li><img class="rounded inline" src="/images/dc_tft.gif"> <a class="border-b" href="https://discord.gg/tftrove" target="_blank">The Forbidden Trove</a></li>
        <li><img class="rounded inline" src="/images/dc_reddit.png"> <a class="border-b" href="https://discord.gg/pathofexile" target="_blank">r/pathofexile</a></li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { Host } from '@/web/background/IPC'
import { DateTime } from 'luxon'

function fmtTime (millis: number) {
  return DateTime.fromMillis(millis).toRelative({ style: 'long' }) ?? 'n/a'
}

export default defineComponent({
  name: 'settings.about',
  inheritAttrs: false,
  setup () {
    const { t } = useI18n()

    return {
      t,
      version: Host.version
    }
  }
})
</script>
