import {createRouter, createWebHashHistory} from "vue-router"

const router = createRouter({
    history: createWebHashHistory(),
    routes: [{
        path: '/',
        redirect: '/login',
    }, {
        path: '/comment/:productId',
        component: () => import('../views/comment/comment.vue'),
        meta: {title: '评论区'}
    }, {
        path: '/cart',
        component: () => import('../views/cart/Cart.vue'),
        meta: {title: '购物车'}
    }, {
        path: '/login',
        component: () => import('../views/user/Login.vue'),
        meta: {title: '用户登录'}
    }, {
        path: '/register',
        component: () => import('../views/user/Register.vue'),
        meta: {title: '用户注册'}
    }, {
        path: '/main',
        component: () => import('../views/Main.vue'),
        meta: {title: '主页面'}
    }, {
        path: '/dashboard',
        component: () => import('../views/user/Dashboard.vue'),
        meta: {title: '个人信息'}
    }, {
        path: '/productDetail/:id',
        component: () => import('../views/product/ProductDetail.vue'),
        meta: {title: '商品详情'}
    }, {
        path:'/changeInfo/:id',
        component: () => import('../views/product/ChangeInfo.vue'),
        meta:{title: '修改商品'}
    }, {
        path: '/createProduct',
        component: () => import('../views/product/CreateProduct.vue'),
        meta: {title: '创建商品'}
    }, {
        path:'/editAdvertisements',
        component: () => import('../views/advertisements/EditAdvertisements.vue'),
        meta: {title: '编辑广告'}
    }, {
        path:'/advertisementDetail/:id',
        component: () => import('../views/advertisements/AdvertisementsDetail.vue'),
        meta: {title: '广告详情'}
    }, {
        path:'/orderList',
        component: () => import('../views/order/OrderList.vue'),
        meta: {title: '订单列表'}
    }, {
        path: '/orderDetail/:id',
        component: () => import('../views/order/OrderDetail.vue'),
        meta: {title: '订单详情'}
    }, {
        path: '/test',
        component: () => import('../views/test.vue'),
        meta: {title: "测试页面"}
    }, {
        path: '/searchMain/:keyword',
        component: () => import('../views/searchMain.vue'),
        meta: {title: "搜索后页面"}
    }, {
        path: '/404',
        name: '404',
        component: () => import('../views/NotFound.vue'),
        meta: {title: '404'}
    }, {
        path: '/:catchAll(.*)',
        redirect: '/404'
    }]
})

// router.beforeEach((to, _, next) => {
//     const token: string | null = sessionStorage.getItem('token');
//     const role: string | null = sessionStorage.getItem('role')
//
//     if (to.meta.title) {
//         document.title = to.meta.title
//     }
//
//     if (token) {
//         if (to.meta.permission) {
//             if (to.meta.permission.includes(role!)) {
//                 next()
//             } else {
//                 next('/404')
//             }
//         } else {
//             next()
//         }
//     } else {
//         if (to.path === '/login') {
//             next();
//         } else if (to.path === '/register') {
//             next()
//         } else {
//             next('/login')
//         }
//     }
// })

export {router}
