import Vue from 'vue'
import Router from 'vue-router'
import Layout from '../views/Layout.vue'

Vue.use(Router)

// Vue Router 3 会把守卫重定向、重复跳转等正常导航结果作为 Promise rejection。
// 统一吞掉带 _isRouter 标记的预期导航结果，避免开发环境错误遮罩误报；真实异常仍继续抛出。
function wrapNavigationMethod (method) {
  const original = Router.prototype[method]
  Router.prototype[method] = function (location, onComplete, onAbort) {
    if (onComplete || onAbort) return original.call(this, location, onComplete, onAbort)
    return original.call(this, location).catch(error => {
      if (error && error._isRouter) return error
      return Promise.reject(error)
    })
  }
}

wrapNavigationMethod('push')
wrapNavigationMethod('replace')

const router = new Router({
  mode: 'hash',
  scrollBehavior: () => ({ x: 0, y: 0 }),
  routes: [
    { path: '/login', component: () => import('../views/Login.vue') },
    { path: '/welcome', name: 'welcome', component: () => import('../views/Welcome.vue'), meta: { auth: true } },
    { path: '/', component: Layout, children: [
      { path: '', name: 'home', component: () => import('../views/Home.vue') },
      { path: 'pets', name: 'pets', component: () => import('../views/PetList.vue'), meta: { auth: true } },
      { path: 'pets/:id', name: 'pet-detail', component: () => import('../views/PetDetail.vue'), meta: { auth: true } },
      { path: 'publish', name: 'publish', component: () => import('../views/Publish.vue'), meta: { auth: true } },
      { path: 'publish/:id', name: 'pet-edit', component: () => import('../views/Publish.vue'), meta: { auth: true } },
      { path: 'profile', name: 'profile', component: () => import('../views/Profile.vue'), meta: { auth: true } }
    ] },
    { path: '*', redirect: '/' }
  ]
})

router.beforeEach((to, from, next) => {
  if (to.meta.auth && !localStorage.getItem('pet_token')) next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
  else next()
})

export default router
