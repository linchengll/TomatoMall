import {axios} from '../../utils/request'
import {ADVERTISEMENTS_MODULE} from '../_prefix'

type UpdateInfo = {
    id: string,
    title?: string,
    content?: string,
    imgUrl?: string,
    productId: string,
}

type AddInfo = {
    title: string,
    content: string,
    imgUrl: string,
    productId: string,
}

//获取广告列表
export const getADVListInfo = () => {
    return axios.get(`${ADVERTISEMENTS_MODULE}`)
        .then(res => {
            return res
        })
}

// 更新广告信息
export const updateADVInfo = (updateInfo: UpdateInfo) => {
    return axios.put(`${ADVERTISEMENTS_MODULE}`, updateInfo, {
        headers: { 'Content-Type': 'application/json' }
    }).then(res => {
        return res
    })
}

// 增加广告
export const addADVInfo = (addInfo: AddInfo) => {
    return axios.post(`${ADVERTISEMENTS_MODULE}`, addInfo, {
        headers: { 'Content-Type': 'application/json' }
    }).then(res => {
        return res
    })
}

// 删除广告
export const deleteADVInfo = (id: string) => {
    return axios.delete(`${ADVERTISEMENTS_MODULE}/${id}`)
        .then(res => {
            return res
        })
}
