import { mount } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'
import ElementPlus from 'element-plus'
import App from './App.vue'
import { router } from './router'
import { pinia } from './stores'

describe('App', () => {
  it('shows the login placeholder as the default screen', async () => {
    router.push('/')
    await router.isReady()

    const wrapper = mount(App, {
      global: {
        plugins: [pinia, router, ElementPlus],
      },
    })

    expect(wrapper.text()).toContain('欢迎登录考勤系统')
  })
})
