import { Result, ok, err } from 'neverthrow'
import {
  CLIENT_STRINGS as _$,
  CLIENT_STRINGS_REF as _$REF,
  BaseType
} from '@/assets/data'
import { ItemCategory } from './meta'
import { ParsedItem, ItemRarity, DivinationCard } from './ParsedItem'

interface ParserState extends ParsedItem {
  name: string
  baseType: string | undefined
  infoVariants: BaseType[]
}

type ParserFn = (lines: string[]) => Result<ParsedItem, string>

const parsers = new Map<string, ParserFn>();
parsers.set("Divination Cards", parseDivinationCards);

export function parseV2(text: string): Result<ParsedItem, string> {
  try {
    const lines = text.split(/\r?\n/)
    const itemClass = lines.find(l => l.includes(_$.ITEM_CLASS))?.replace(_$.ITEM_CLASS,"").trim() || ""
    let parser = parsers.get(itemClass)!

    return parser.call(parser, lines)
  } catch (e) {
    console.log(e)
    return err('item.parse_error')
  }
}

function itemTextToSections (text: string) {
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

function parseNamePlate (section: string[]) {
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
    rawText: undefined!
  }

  switch (rarityText) {
    case _$.RARITY_CURRENCY:
      item.category = ItemCategory.Currency; break
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

function markupConditionParser (text: string) {
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

export function removeLinesEnding (
  lines: readonly string[], ending: string
): string[] {
  return lines.map(line =>
    line.endsWith(ending)
      ? line.slice(0, -ending.length)
      : line
  )
}

function parseDivinationCards(lines: string[]): Result<ParsedItem, string> {

  const baseType: BaseType = {
    name: lines[2],
    refName: '',
    namespace: 'DIVINATION_CARD',
    icon: 'https://webtw.poecdn.com/gen/image/WzI1LDE0LHsiZiI6IjJESXRlbXMvRGl2aW5hdGlvbi9JbnZlbnRvcnlJY29uIiwidyI6MSwiaCI6MSwic2NhbGUiOjF9XQ/f34bf8cbb5/InventoryIcon.png'
  }

  const divinationCard: DivinationCard = {
    isUnidentified: false,
    isCorrupted: false,
    influences: [],
    statsByType: [],
    newMods: [],
    unknownModifiers: [],
    info: baseType,
    rawText: baseType.name,
    category: ItemCategory.DivinationCard,
    cardName: lines[2],
    cardStackSize: lines[4],
    reward: lines[6],
    description: lines[8]
  }
  return Object.freeze(ok(divinationCard))
}