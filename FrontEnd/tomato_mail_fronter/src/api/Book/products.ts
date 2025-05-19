import {axios} from '../../utils/request'
import {PRODUCTS_MODULE, TYPE_MODULE} from '../_prefix'

export type UpdateInfo = {
    id?: string,
    title: string,
    price?: number,
    rate?: number,
    description?: string,
    cover?: string,
    detail?: string,
    specifications?: Set<Specification>;
}

export interface Specification {
    id: string;
    item: string;
    value: string;
    productId: string;
}

export type AddInfo = {
    title: string,
    price: number,
    rate: number,
    description?: string,
    cover?: string,
    detail?: string,
    specifications?: Set<Specification>;
}


type GetInfo = {

}

//获取商品列表
export const getListInfo = () => {
    return axios.get(`${PRODUCTS_MODULE}`)
        .then(res => {
            return res
        })
}

//获取指定商品
export const getInfo = (id: string) => {
    return axios.get(`${PRODUCTS_MODULE}/${id}`)
        .then(res => {
            return res
        })
}

// 更新商品信息
export const updateInfo = (updateInfo: UpdateInfo) => {
    return axios.put(`${PRODUCTS_MODULE}`, updateInfo, {
        headers: { 'Content-Type': 'application/json' }
    }).then(res => {
        return res
    })
}

// 增加商品
export const addInfo = (addInfo: AddInfo) => {
    return axios.post(`${PRODUCTS_MODULE}`, addInfo, {
        headers: { 'Content-Type': 'application/json' }
    }).then(res => {
        return res
    })
}

// 删除商品
export const deleteInfo = (id: string) => {
    return axios.delete(`${PRODUCTS_MODULE}/${id}`)
        .then(res => {
            return res
        })
}

// 添加类别
export const addTypeInfo = (typeName: string) => {
    return axios.post(`${TYPE_MODULE}`, typeName, {
        headers: { 'Content-Type': 'application/json' }
    }).then(res => {
        return res
    })
}

// 删除类别
export const deleteTypeInfo = (typeId: string) => {
    return axios.delete(`${TYPE_MODULE}/${typeId}`)
        .then(res => {
            return res
        })
}

// 获取全部类型
export const getTypeListInfo = () => {
    return axios.get(`${TYPE_MODULE}`)
        .then(res => {
            return res
        })
}