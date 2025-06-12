<template>
  <div class="max-w-md p-2">
    <div class="mb-2" v-if="!leagues.error.value">
      <div class="flex-1 mb-1">{{ t('league') }}
        <button class="btn" @click="leagues.load" :disabled="leagues.isLoading.value">{{ t('Refresh') }}</button>
      </div>
      <div v-if="leagues.isLoading.value" class="mb-4">
        <i class="fas fa-info-circle text-gray-600"></i> {{ t('app.leagues_loading') }}</div>
      <template v-else-if="leagues.list.value.length">
        <div class="mb-2 flex flex-col gap-1 items-start whitespace-nowrap">
          <ui-radio v-for="league of leagues.list.value" :key="league.id"
            v-model="leagueId" :value="league.id">{{ league.id }}</ui-radio>
        </div>
        <div class="flex gap-x-2 mb-4">
          <div class="text-gray-500">{{ t('settings.private_league') }}</div>
          <input v-model="customLeagueId" placeholder="My League (PL12345)" class="rounded bg-gray-900 px-1 mb-1 flex-1" />
        </div>
      </template>
    </div>
    <ui-error-box v-else class="mb-4">
      <template #name>{{ t('app.leagues_failed') }}</template>
      <p>{{ t('app.leagues_failed_help_alt') }}</p>
      <template #actions>
        <button class="btn" @click="leagues.load">{{ t('Retry') }}</button>
      </template>
    </ui-error-box>
    <div class="mb-2 mx-2">
      <div class="mb-4 flex">
        <span class="bg-gray-900 text-gray-500 rounded px-2">{{ hotkeyLockedLang }}</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue'
import { useI18nNs } from '@/web/i18n'
import UiRadio from '@/web/ui/UiRadio.vue'
import UiCheckbox from '@/web/ui/UiCheckbox.vue'
import UiToggle from '@/web/ui/UiToggle.vue'
import UiErrorBox from '@/web/ui/UiErrorBox.vue'
import { configModelValue, configProp, findWidget } from '../settings/utils.js'
import type { TranslationWidget } from '@/web/overlay/interfaces'
import { useLeagues } from '../background/Leagues.js'

export default defineComponent({
  name: 'translation.name',
  components: { UiRadio, UiCheckbox, UiToggle, UiErrorBox },
  props: configProp(),
  setup (props) {
    const configWidget = computed(() => findWidget<TranslationWidget>('translation', props.config)!)

    const leagues = useLeagues()
    const { t } = useI18nNs('translation')

    return {
      t,
      leagueId: configModelValue(() => props.config, 'leagueId'),
      customLeagueId: computed<string>({
        get: () => !leagues.list.value.some(league => league.id === props.config.leagueId)
          ? props.config.leagueId ?? ''
          : '',
        set: (value) => { props.config.leagueId = value }
      }),
      accountName: configModelValue(() => props.config, 'accountName'),
      showCursor: configModelValue(() => configWidget.value, 'showCursor'),
      builtinBrowser: configModelValue(() => configWidget.value, 'builtinBrowser'),
      hotkeyLockedLang: computed(() => configWidget.value.hotkeyLockedLang),
      leagues
    }
  }
})
</script>
