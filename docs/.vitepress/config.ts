import { readFileSync } from 'node:fs'
import { defineConfig } from 'vitepress'

const root = new URL('../..', import.meta.url)
const base = process.env.DOCS_BASE ?? '/'
const logo = `${base}assets/logo.svg`

function readPluginVersion(): string {
  const properties = readFileSync(new URL('gradle.properties', root), 'utf8')
  const match = properties.match(/^version=(.+)$/m)
  if (!match) {
    throw new Error('version not found in gradle.properties')
  }
  return match[1].trim()
}

const pluginVersion = readPluginVersion()

const enNav = [
  { text: 'Quick start', link: '/guide' },
  { text: 'Commands', link: '/command/' },
  { text: 'Integration', link: '/guide/third-party' },
]

const enSidebar = [
  {
    text: 'Getting started',
    items: [
      { text: 'Quick start', link: '/guide' },
      { text: 'Integration', link: '/guide/third-party' },
      { text: 'plugin.yml', link: '/guide/configuration' },
      { text: 'Building & contributing', link: '/guide/building' },
    ],
  },
  {
    text: 'Modules',
    items: [
      { text: 'Overview', link: '/modules' },
      { text: 'Plugin entry', link: '/plugin' },
      {
        text: 'Commands',
        collapsed: false,
        items: [
          { text: 'Overview', link: '/command/' },
          { text: 'Quick start', link: '/command/quick-start' },
          { text: 'Reference', link: '/command/api' },
          { text: 'Examples', link: '/command/examples' },
        ],
      },
      { text: 'Errors', link: '/exception' },
    ],
  },
]

const zhNav = [
  { text: '快速开始', link: '/zh/guide' },
  { text: '命令', link: '/zh/command/' },
  { text: '集成接入', link: '/zh/guide/third-party' },
]

const zhSidebar = [
  {
    text: '开始',
    items: [
      { text: '快速开始', link: '/zh/guide' },
      { text: '集成接入', link: '/zh/guide/third-party' },
      { text: 'plugin.yml', link: '/zh/guide/configuration' },
      { text: '构建与贡献', link: '/zh/guide/building' },
    ],
  },
  {
    text: '模块',
    items: [
      { text: '概览', link: '/zh/modules' },
      { text: '插件入口', link: '/zh/plugin' },
      {
        text: '命令',
        collapsed: false,
        items: [
          { text: '概览', link: '/zh/command/' },
          { text: '快速上手', link: '/zh/command/quick-start' },
          { text: '参考', link: '/zh/command/api' },
          { text: '示例', link: '/zh/command/examples' },
        ],
      },
      { text: '错误', link: '/zh/exception' },
    ],
  },
]

export default defineConfig({
  title: 'BetterPlugin',
  description: 'A plugin development framework for Paper servers',
  lang: 'en-US',
  cleanUrls: true,
  lastUpdated: true,
  base,
  head: [
    ['link', { rel: 'icon', type: 'image/svg+xml', href: `${base}assets/logo.svg` }],
  ],
  markdown: {
    config(md) {
      md.core.ruler.push('betterplugin-version', (state) => {
        const replace = (tokens: any[]) => {
          for (const token of tokens) {
            if (token.content) {
              token.content = token.content.replaceAll('{{plugin_version}}', pluginVersion)
            }
            for (const attr of ['href', 'src']) {
              const value = token.attrGet(attr)
              if (value) {
                token.attrSet(attr, value.replaceAll('{{plugin_version}}', pluginVersion))
              }
            }
            if (token.children) {
              replace(token.children)
            }
          }
        }
        replace(state.tokens)
        return true
      })
    },
  },
  locales: {
    root: {
      label: 'English',
      lang: 'en-US',
      title: 'BetterPlugin',
      description: 'A plugin development framework for Paper servers',
      themeConfig: {
        nav: enNav,
        sidebar: enSidebar,
        footer: {
          message: 'Powered by VitePress',
          copyright: 'Copyright © 2026 CoffeePopStudio',
        },
      },
    },
    zh: {
      label: '简体中文',
      lang: 'zh-CN',
      title: 'BetterPlugin',
      description: '面向 Paper 服务器的插件开发框架',
      themeConfig: {
        nav: zhNav,
        sidebar: zhSidebar,
        footer: {
          message: 'Powered by VitePress',
          copyright: 'Copyright © 2026 CoffeePopStudio',
        },
      },
    },
  },
  themeConfig: {
    logo,
    search: {
      provider: 'local',
    },
    socialLinks: [
      { icon: 'github', link: 'https://github.com/CoffeePopStudio/BetterPlugin' },
    ],
  },
})
