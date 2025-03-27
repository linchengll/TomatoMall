import {axios} from '../utils/request'
//上传图片
export const imageInfoUpdate = async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);

    return axios.post(`/api/images`, formData, {

    }).then(res => {
        return res
    })
};
