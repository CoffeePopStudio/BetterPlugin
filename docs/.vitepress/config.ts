import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'BetterPlugin',
  description: 'BetterPlugin - 一个基于 Paper Brigadier 的轻量命令框架',
  lang: 'zh-CN',
  cleanUrls: true,
  lastUpdated: true,
  base: process.env.DOCS_BASE ?? '/',
  themeConfig: {
    logo: '/assets/logo.svg',
    nav: [
      { text: '首页', link: '/' },
      { text: '指南', link: '/guide' },
      { text: 'API', link: '/api' },
      { text: '示例', link: '/examples' },
    ],
    sidebar: {
      '/guide': [
        {
          text: '指南',
          items: [
            { text: '快速开始', link: '/guide' },
            { text: '集成到第三方插件', link: '/guide/third-party' },
            { text: '配置说明', link: '/guide/configuration' },
          ],
        },
      ],
      '/api': [
        {
          text: 'API',
          items: [
            { text: 'CommandBuilder', link: '/api#commandbuilder' },
            { text: 'CommandBuilder.create', link: '/api#create' },
            { text: 'executes', link: '/api#executes' },
            { text: 'tabCompleter', link: '/api#tabcompleter' },
            { text: '命令限制', link: '/api#command-restrictions' },
            { text: '冷却', link: '/api#cooldown' },
            { text: '子命令', link: '/api#subcommands' },
          ],
        },
      ],
    },
    socialLinks: [
      { icon: 'github', link: 'https://github.com/oneachina/BetterPlugin' },
    ],
    footer: {
      message: 'Released under the MIT License.',
      copyright: 'Copyright © 2026 Neamyoo',
    },
  },
  head: [
    ['link', { rel: 'preconnect', href: 'https://fonts.googleapis.com' }],
    ['link', { rel: 'preconnect', href: 'https://fonts.gstatic.com', crossOrigin: '' }],
    ['link', { href: 'https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,400;0,500;0,700;1,400&family=Noto+Sans+SC:wght@400;500;700&display=swap', rel: 'stylesheet' }],
  ],
})