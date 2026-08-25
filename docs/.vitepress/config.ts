import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'BetterPlugin',
  description: 'BetterPlugin - 面向 Paper 服务器的插件开发框架',
  lang: 'zh-CN',
  cleanUrls: true,
  lastUpdated: true,
  base: process.env.DOCS_BASE ?? '/',
  themeConfig: {
    logo: '/assets/logo.svg',
    nav: [
      { text: '首页', link: '/' },
      { text: '快速开始', link: '/guide' },
      { text: '命令 API', link: '/command/' },
      { text: '插件基础', link: '/plugin' },
      { text: '集成接入', link: '/guide/third-party' },
    ],
    sidebar: [
      {
        text: '开始',
        items: [
          { text: '快速开始', link: '/guide' },
          { text: '集成接入', link: '/guide/third-party' },
          { text: '配置说明', link: '/guide/configuration' },
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
  head: [
    ['link', { rel: 'preconnect', href: 'https://fonts.googleapis.com' }],
    ['link', { rel: 'preconnect', href: 'https://fonts.gstatic.com', crossOrigin: '' }],
    ['link', { href: 'https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,400;0,500;0,700;1,400&family=Noto+Sans+SC:wght@400;500;700&display=swap', rel: 'stylesheet' }],
  ],
})