import { login, logout, getInfo, refreshToken } from '@/api/login'
import { getToken, setToken, setExpiresIn, removeToken } from '@/utils/auth'

const user = {
  state: {
    token: getToken(),
    name: '',
    avatar: '',
    roles: [],
    permissions: []
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_EXPIRES_IN: (state, time) => {
      state.expires_in = time
    },
    SET_NAME: (state, name) => {
      state.name = name
    },
    SET_AVATAR: (state, avatar) => {
      state.avatar = avatar
    },
    SET_ROLES: (state, roles) => {
      state.roles = roles
    },
    SET_PERMISSIONS: (state, permissions) => {
      state.permissions = permissions
    }
  },

  actions: {
    // 登录
    Login({ commit }, userInfo) {
      /**参考  https://vuex.vuejs.org/zh/guide/actions.html#%E5%88%86%E5%8F%91-action
       * Action 类似于 mutation，不同在于：
         Action 提交的是 mutation，而不是直接变更状态。
         Action 可以包含任意异步操作。
        Action 通过 store.dispatch 方法触发：
      store.dispatch('increment')
      乍一眼看上去感觉多此一举，我们直接分发 mutation 岂不更方便？实际上并非如此，
      还记得 mutation 必须同步执行这个限制么？Action 就不受约束！我们可以在 action 内部执行异步操作
       */
      // 获取表单里的数据，涛涛表示有手就行
      const username = userInfo.username.trim()  //两边去空格
      const password = userInfo.password
      const code = userInfo.code
      const uuid = userInfo.uuid
      //  ES6---new Promise()使用方法  参考 https://blog.csdn.net/qq_29483485/article/details/86605396
      /**
       *Promise 是一个对象，也是一个构造函数。是js对异步操作的一种解决方案，为异步操作提供了统一的接口
       Promise的构造函数接收一个参数，是函数
       并且传入两个参数：resolve，reject，
       分别表示 异步操作执行成功后的回调函数
       和      异步操作执行失败后的回调函数。
       其实这里用“成功”和“失败”来描述并不准确，按照标准来讲，
       resolve是将Promise的状态置为fullfiled，
       reject是将Promise的状态置为rejected。
参考  https://www.cnblogs.com/mg6666/p/14508322.html

       .then() 和 .catch()：
　                  　Promise构造器接受一个函数作为参数，这个函数有两个参数：resolve，reject，分别代表这个Promise实例成功之后的回调函数和失败之后的回调函数。
                      Promise.all() 和 Promise.race()：
　　                all方法的效果实际就是 谁跑的慢，那么就以谁为准来执行回调，而race则相反，谁跑的快，就以谁为准来执行回调。
       */
      return new Promise((resolve, reject) => {
        login(username, password, code, uuid).then(res => {
          //这些就是后端返回来后的要看的了，暂且不提，先看这个code经历了什么
          //此处调用了login方法，传来四个参数  username, password, code, uuid
          // import { login, logout, getInfo, refreshToken } from '@/api/login'
          // 这个方法是从/api/login导入的，进去看看
          let data = res.data
          setToken(data.access_token)
          commit('SET_TOKEN', data.access_token)
          setExpiresIn(data.expires_in)
          commit('SET_EXPIRES_IN', data.expires_in)
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 获取用户信息
    GetInfo({ commit, state }) {
      return new Promise((resolve, reject) => {
        getInfo().then(res => {
          const user = res.user
          const avatar = user.avatar == "" ? require("@/assets/images/profile.jpg") : user.avatar;
          if (res.roles && res.roles.length > 0) { // 验证返回的roles是否是一个非空数组
            commit('SET_ROLES', res.roles)
            commit('SET_PERMISSIONS', res.permissions)
          } else {
            commit('SET_ROLES', ['ROLE_DEFAULT'])
          }
          commit('SET_NAME', user.userName)
          commit('SET_AVATAR', avatar)
          resolve(res)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 刷新token
    RefreshToken({commit, state}) {
      return new Promise((resolve, reject) => {
        refreshToken(state.token).then(res => {
          setExpiresIn(res.data)
          commit('SET_EXPIRES_IN', res.data)
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 退出系统
    LogOut({ commit, state }) {
      return new Promise((resolve, reject) => {
        logout(state.token).then(() => {
          commit('SET_TOKEN', '')
          commit('SET_ROLES', [])
          commit('SET_PERMISSIONS', [])
          removeToken()
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 前端 登出
    FedLogOut({ commit }) {
      return new Promise(resolve => {
        commit('SET_TOKEN', '')
        removeToken()
        resolve()
      })
    }
  }
}

export default user
