import { axios } from '../utils/request';

type commentData = {
    content: string,
    productId: string,
    imageUrls: string[]
}

//获取该商品所有评论
export const getComments = (productId: string) => {
    return axios.get(`/api/comment/${productId}`)
        .then(res => {
            return res;
        });
};

//创建评论
export const createComment = (commentdata: commentData) => {
    return axios.post('/api/comment', commentdata, {
        headers: { 'Content-Type': 'application/json' }
    }).then(res => {
        return res;
    });
};

//删除评论
export const deleteComment = (commentId: string) => {
    return axios.delete(`/api/comment/${commentId}`)
        .then(res => {
            return res;
        });
};

//点赞评论
export const updateLikeCount = (commentId: string) => {
    return axios.put(`/api/comment/${commentId}`)
        .then(res => {
            return res;
        });
};