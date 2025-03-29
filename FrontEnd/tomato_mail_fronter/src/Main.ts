import { createApp } from 'vue'
import {router} from './router'
import App from './App.vue'
import axios from 'axios'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css'

//设置后端地址（本地或服务器），会将请求转发到后端端口
axios.defaults.baseURL = ("http://localhost:8080")
axios.defaults.timeout = 30000;

//创建一个新的Vue应用实例，使用ElementPlus插件和路由，然后挂载到页面上id为'app'的元素上。
createApp(App).use(ElementPlus).use(router).mount('#app')
