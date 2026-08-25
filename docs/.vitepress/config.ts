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
  { text: 'Home', link: '/' },
  { text: 'Quick start', link: '/guide' },
  { text: 'Command API', link: '/command/' },
  { text: 'Plugin base', link: '/plugin' },
  { text: 'Exceptions', link: '/exception' },
  { text: 'Integration', link: '/guide/third-party' },
  { text: 'Configuration', link: '/guide/configuration' },
]

const enSidebar = [
  {
    text: 'Getting started',
    items: [
      { text: 'Quick start', link: '/guide' },
      { text: 'Integration', link: '/guide/third-party' },
      { text: 'Configuration', link: '/guide/configuration' },
      { text: 'Building & contributing', link: '/guide/building' },
    ],
  },
  {
    text: 'Modules',
    items: [
      { text: 'Plugin base', link: '/plugin' },
      {
        text: 'Command API',
        collapsed: false,
        items: [
          { text: 'Overview', link: '/command/' },
          { text: 'Quick start', link: '/command/quick-start' },
          { text: 'API reference', link: '/command/api' },
          { text: 'Examples', link: '/command/examples' },
        ],
      },
      { text: 'Exceptions', link: '/exception' },
    ],
  },
]

const zhNav = [
  { text: '首页', link: '/zh/' },
  { text: '快速开始', link: '/zh/guide' },
  { text: '命令 API', link: '/zh/command/' },
  { text: '插件基础', link: '/zh/plugin' },
  { text: '异常', link: '/zh/exception' },
  { text: '集成接入', link: '/zh/guide/third-party' },
  { text: '配置说明', link: '/zh/guide/configuration' },
]

const zhSidebar = [
  {
    text: '开始',
    items: [
      { text: '快速开始', link: '/zh/guide' },
      { text: '集成接入', link: '/zh/guide/third-party' },
      { text: '配置说明', link: '/zh/guide/configuration' },
      { text: '构建与贡献', link: '/zh/guide/building' },
    ],
  },
  {
    text: '模块',
    items: [
      { text: '插件基础', link: '/zh/plugin' },
      {
        text: '命令 API',
        collapsed: false,
        items: [
          { text: '概览', link: '/zh/command/' },
          { text: '快速上手', link: '/zh/command/quick-start' },
          { text: 'API 参考', link: '/zh/command/api' },
          { text: '示例', link: '/zh/command/examples' },
        ],
      },
      { text: '异常', link: '/zh/exception' },
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
