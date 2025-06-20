import {axios} from '../../utils/request'
import {PRODUCTS_MODULE, TYPE_MODULE, SEARCH_MODULE} from '../_prefix'

export interface Specification {
    id: string;
    item: string;
    value: string;
    productId: string;
}

export interface ProductType {
    typeId: number;
    typeName?: string;
}

export type UpdateInfo = {
    id?: string,
    title: string,
    price?: number,
    rate?: number,
    description?: string,
    cover?: string,
    detail?: string,
    specifications?: Set<Specification>;
    types?: Set<ProductType>;
}

export type AddInfo = {
    title: string,
    price: number,
    rate: number,
    description?: string,
    cover?: string,
    detail?: string,
    specifications?: Set<Specification>;
    types?: Set<ProductType>;
}

export type SearchInfo = {
    searchString: string,
    type: number,
}

//搜索指定商品
export const searchList = (searchInfo: SearchInfo) => {
    return axios.post(`${SEARCH_MODULE}`, searchInfo, {
        headers: { 'Content-Type': 'application/json' }
    }).then(res => {
        return res
    })
}

//获取热门商品
export const getTopList = () => {
    return axios.get(`${SEARCH_MODULE}/top`)
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

function setToArray<T>(set?: Set<T>): T[] | undefined {
    return set ? Array.from(set) : undefined;
}

// 增加商品
export const addInfo = (addInfo: AddInfo) => {
    const payload = {
        ...addInfo,
        specifications: setToArray(addInfo.specifications),
        types: setToArray(addInfo.types),
    };

    return axios.post(`${PRODUCTS_MODULE}`, payload, {
        headers: { 'Content-Type': 'application/json' }
    });
}

// 更新商品信息
export const updateInfo = (updateInfo: UpdateInfo) => {
    const payload = {
        ...updateInfo,
        specifications: setToArray(updateInfo.specifications),
        types: setToArray(updateInfo.types),
    };

    return axios.put(`${PRODUCTS_MODULE}`, payload, {
        headers: { 'Content-Type': 'application/json' }
    });
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

