import {axios} from '../utils/request'
import {USER_MODULE} from './_prefix'

type RegisterInfo = {
    username: string,
    password: string,
    name: string,
    avatar?: string,
    role: string,
    telephone?: string,
    email?: string,
    location?: string
}

type LoginInfo = {
    username: string,
    password: string
}

type UpdateInfo = {
    username: string,
    password?: string,
    name?: string,
    avatar?: string,
    role?: string,
    telephone?: string,
    email?: string,
    location?: string
}
// 用户登录
export const userLogin = async (loginInfo: LoginInfo) => {
    return axios.post(`${USER_MODULE}/login`, loginInfo)
        .then(res => {
            return res
        })
}

// 用户注册
export const userRegister = async (registerInfo: RegisterInfo) => {
    return axios.post(`${USER_MODULE}`, registerInfo, {
        headers: { 'Content-Type': 'application/json' }
    }).then(res => {
        return res
    })
}

// 获取用户信息
export const userInfo = (username: string) => {
    return axios.get(`${USER_MODULE}/${username}`)
        .then(res => {
            return res
        })
}


//获取用户信息
export const userInfo_commentGet = (userId: string) => {
    return axios.get(`${USER_MODULE}/comment_get/${userId}`)
        .then(res => {
            return res
        })
}

// 更新用户信息
export const userInfoUpdate = (updateInfo: UpdateInfo) => {
    return axios.put(`${USER_MODULE}`, updateInfo, {
        headers: { 'Content-Type': 'application/json' }
    }).then(res => {
        return res
    })
}

