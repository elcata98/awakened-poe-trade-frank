import { Result, ok, err } from 'neverthrow'
import {
  CLIENT_STRINGS as _$,
  CLIENT_STRINGS_REF as _$REF,
  ITEM_BY_REF,
  BaseType
} from '@/assets/data'
import { ItemCategory } from './meta'
import { ParsedItem, ItemRarity, DivinationCard, StackableCurrency } from './ParsedItem'
import { useI18n } from 'vue-i18n'

interface ParserState extends ParsedItem {
  name: string
  baseType: string | undefined
  infoVariants: BaseType[]
}

type ParserFn = (lines: string[][]) => ParsedItem

// const { t } = useI18n()

const parsers = new Map<string, ParserFn>();
parsers.set("Divination Cards", parseDivinationCards);
parsers.set("Stackable Currency", parseStackableCurrency);

export function parseV2(text: string): Result<ParsedItem, string> {
  try {
    // const lines = text.split(/\r?\n/)
    // const itemClass = lines.find(l => l.includes(_$.ITEM_CLASS))?.replace(_$.ITEM_CLASS, "").trim() || ""

    let sections = itemTextToSections(text)
    const itemClass = sections[0][0].replace("アイテムクラス: ", "").trim()
    let parser = parsers.get(itemClass)!

    let parsedItem = parser.call(parser, sections)
    findInDatabase(parsedItem)

    return Object.freeze(ok(parsedItem))
  } catch (e) {
    console.log(e)
    return err('item.parse_error')
  }
}

function itemTextToSections(text: string) {
  const lines = text.split(/\r?\n/)
  if (lines[lines.length - 1] === '') {
    lines.pop()
  }

  const sections: string[][] = [[]]
  lines.reduce((section, line) => {
    if (line !== '--------') {
      section.push(line)
      return section
    } else {
      const section: string[] = []
      sections.push(section)
      return section
    }
  }, sections[0])
  return sections.filter(section => section.length)
}

function parseNamePlate(section: string[]) {
  let line = section.shift()
  if (!line?.startsWith(_$.ITEM_CLASS)) {
    return err('item.parse_error')
  }

  line = section.shift()
  let rarityText: string | undefined
  if (line?.startsWith(_$.RARITY)) {
    rarityText = line.slice(_$.RARITY.length)
    line = section.shift()
  }

  let name: string
  if (line != null) {
    name = markupConditionParser(line)
  } else {
    return err('item.parse_error')
  }

  line = section.shift()
  const baseType = line && markupConditionParser(line)

  const item: ParserState = {
    rarity: undefined,
    category: undefined,
    name: name,
    baseType: baseType,
    isUnidentified: false,
    isCorrupted: false,
    newMods: [],
    statsByType: [],
    unknownModifiers: [],
    influences: [],
    info: undefined!,
    infoVariants: undefined!,
    rawText: undefined!,
    description: []
  }

  switch (rarityText) {
    case _$.RARITY_CURRENCY:
      item.category = ItemCategory.StackableCurrency; break
    case _$.RARITY_DIVCARD:
      item.category = ItemCategory.DivinationCard; break
    case _$.RARITY_GEM:
      item.category = ItemCategory.Gem; break
    case _$.RARITY_NORMAL:
    case _$.RARITY_QUEST:
      item.rarity = ItemRarity.Normal; break
    case _$.RARITY_MAGIC:
      item.rarity = ItemRarity.Magic; break
    case _$.RARITY_RARE:
      item.rarity = ItemRarity.Rare; break
    case _$.RARITY_UNIQUE:
      item.rarity = ItemRarity.Unique; break
  }

  return ok(item)
}

function markupConditionParser(text: string) {
  // ignores state set by <<set:__>>
  // always evaluates first condition to true <if:__>{...}
  // full markup: https://gist.github.com/SnosMe/151549b532df8ea08025a76ae2920ca4

  text = text.replace(/<<set:.+?>>/g, '')
  text = text.replace(/<(if:.+?|elif:.+?|else)>{(.+?)}/g, (_, type: string, body: string) => {
    return type.startsWith('if:')
      ? body
      : ''
  })

  return text
}

export function removeLinesEnding(
  lines: readonly string[], ending: string
): string[] {
  return lines.map(line =>
    line.endsWith(ending)
      ? line.slice(0, -ending.length)
      : line
  )
}

// NEW
function findInDatabase(item: ParsedItem) {
  let info: BaseType[] | undefined
  if (item.category === ItemCategory.DivinationCard) {
    info = ITEM_BY_REF('DIVINATION_CARD', item.name)
  } else if (item.category === ItemCategory.CapturedBeast) {
    info = ITEM_BY_REF('CAPTURED_BEAST', item.name)
  } else if (item.category === ItemCategory.Gem) {
    info = ITEM_BY_REF('GEM', item.name)
  } else if (item.category === ItemCategory.MetamorphSample) {
    info = ITEM_BY_REF('ITEM', item.name)
  } else if (item.category === ItemCategory.Voidstone) {
    info = ITEM_BY_REF('ITEM', 'Charged Compass')
  } else if (item.rarity === ItemRarity.Unique && !item.isUnidentified) {
    info = ITEM_BY_REF('UNIQUE', item.name)
  } else {
    info = ITEM_BY_REF('ITEM', item.name)
  }
  if (!info?.length) {
    return err('item.unknown')
  }
  if (info[0].unique) {
    // info = info.filter(info => info.unique!.base === item.baseType)
  }
  // item.infoVariants = info
  // choose 1st variant, correct one will be picked at the end of parsing
  item.info = info[0]
  // same for every variant
  if (!item.category) {
    if (item.info.craftable) {
      item.category = item.info.craftable.category
    } else if (item.info.unique) {
      item.category = ITEM_BY_REF('ITEM',
        item.info.unique.base)![0].craftable!.category
    }
  }
}

function parseDivinationCards(sections: string[][]): ParsedItem {
  const name = sections[0][2]

  const divinationCard: DivinationCard = {
    isUnidentified: false,
    isCorrupted: false,
    influences: [],
    statsByType: [],
    newMods: [],
    unknownModifiers: [],
    rawText: name,
    category: ItemCategory.DivinationCard,
    name: name,
    stackSize: sections[1][0],
    reward: sections[2][0],
    description: sections[3]
  }
  return divinationCard
}

function parseStackableCurrency(sections: string[][]): ParsedItem {

  var description = sections[2]
  var instructions: string[] = []
  if (sections.length > 3) {
    instructions = sections[3]
  }
  const name = sections[0][2]

  const stackableCurrency: StackableCurrency = {
    isUnidentified: false,
    isCorrupted: false,
    influences: [],
    statsByType: [],
    newMods: [],
    unknownModifiers: [],
    rawText: name,
    category: ItemCategory.StackableCurrency,
    name: name,
    stackSize: sections[1][0],
    description: description,
    instructions: instructions
  }
  return stackableCurrency
}