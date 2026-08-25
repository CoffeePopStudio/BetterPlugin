import { readFileSync } from 'node:fs'
import { defineConfig } from 'vitepress'

const root = new URL('../..', import.meta.url)
const base = process.env.DOCS_BASE ?? '/'

function readPluginVersion(): string {
  const properties = readFileSync(new URL('gradle.properties', root), 'utf8')
  const match = properties.match(/^version=(.+)$/m)
  if (!match) {
    throw new Error('version not found in gradle.properties')
  }
  return match[1].trim()
}

const pluginVersion = readPluginVersion()

export default defineConfig({
  title: 'BetterPlugin',
  description: 'BetterPlugin - 面向 Paper 服务器的插件开发框架',
  lang: 'zh-CN',
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
  themeConfig: {
    logo: `${base}assets/logo.svg`,
    search: {
      provider: 'local',
    },
    nav: [
      { text: '首页', link: '/' },
      { text: '快速开始', link: '/guide' },
      { text: '命令 API', link: '/command/' },
      { text: '插件基础', link: '/plugin' },
      { text: '异常', link: '/exception' },
      { text: '集成接入', link: '/guide/third-party' },
      { text: '配置说明', link: '/guide/configuration' },
    ],
    sidebar: [
      {
        text: '开始',
        items: [
          { text: '快速开始', link: '/guide' },
          { text: '集成接入', link: '/guide/third-party' },
          { text: '配置说明', link: '/guide/configuration' },
          { text: '构建与贡献', link: '/guide/building' },
        ],
      },
      {
        text: '模块',
        items: [
          { text: '插件基础', link: '/plugin' },
          {
            text: '命令 API',
            collapsed: false,
            items: [
              { text: '概览', link: '/command/' },
              { text: '快速上手', link: '/command/quick-start' },
              { text: 'API 参考', link: '/command/api' },
              { text: '示例', link: '/command/examples' },
            ],
          },
          { text: '异常', link: '/exception' },
        ],
      },
    ],
    socialLinks: [
      { icon: 'github', link: 'https://github.com/CoffeePopStudio/BetterPlugin' },
    ],
    footer: {
      message: 'Powered by VitePress',
      copyright: 'Copyright © 2026 CoffeePopStudio',
    },
  },
})
