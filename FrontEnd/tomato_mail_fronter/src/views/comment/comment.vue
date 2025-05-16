<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getComments, createComment, deleteComment, updateLikeCount } from '../../api/comment.ts';
import { ElMessage } from 'element-plus';
import {imageInfoUpdate} from "../../api/tools.ts"

// const route = useRoute();
// const router = useRouter();
// const productId = ref(route.params.id);

const route = useRoute();
const router = useRouter();
const pId = route.params.productId as string;
const productId = ref(pId);


interface CommentItem {
  Id: string;
  Content: string;
  likeCount: number;
  productId: string;
  createTime: string;
  imageUrls: string[];
  userRate : number;
}

// const comments = ref<CommentItem[]>([
//   {
//     Id: '1',
//     Content: '太好看了！',
//     likeCount: 245,
//     productId: '10',
//     createTime: '2024-12-12',
//     imageUrls: [
//       'https://marnus.oss-cn-hangzhou.aliyuncs.com/095f6cab409560cd5aedf3b6bad13546.jpeg',
//       'https://marnus.oss-cn-hangzhou.aliyuncs.com/7db7bd39cc6da5c185e91e0dcfcd3c80.jpeg'
//     ]
//   },
//   {
//     Id: '2',
//     Content: '太好看了！',
//     likeCount: 2434,
//     productId: '10',
//     createTime: '2024-12-13',
//     imageUrls: []
//   }
// ]);
const comments = ref<CommentItem[]>([]);

const newUserRate = ref(0);
const newCommentText = ref('');
const uploadedImages = ref<string[]>([]);

//获取全部评论
const fetchComments = async () => {
  try {
    if(!productId.value) {
      console.error('No productId provided');
      return;
    }
    const response = await getComments(productId.value);
    if (response.data.code === '200') {
      comments.value = response.data.data.map((item: any) => ({
        Id: item.id,
        Content: item.content,
        likeCount: item.likeCount,
        productId: item.productId,
        createTime: item.createTime,
        imageUrls: item.imageUrls,
        userRate: item.userRate
      }));
    } else {
      ElMessage.error(response.data.msg || '获取评论失败');
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('获取评论失败');
  }
};

//删除评论
const handleDeleteComment = async (commentId: string) => {
  try {
    const response = await deleteComment(commentId);
    if (response.data.code === '200') {
      ElMessage.success('删除成功');
      fetchComments();
    } else {
      ElMessage.error(response.data.msg || '删除失败');
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('删除失败');
  }
};

//点赞评论
const handleLikeComment = async (commentId: string) => {
  try {
    const response = await updateLikeCount(commentId);
    if (response.data.code === '200') {
      ElMessage.success(response.data.data);
      fetchComments();
    } else {
      ElMessage.error(response.data.msg || '点赞失败了');
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('点赞失败');
  }
};

//上传图片
const handleUploadImage = async (file: File) => {
  try {
    const response = await imageInfoUpdate(file); // 调用上传图片接口
    if (response.data.code === '200') {
      uploadedImages.value.push(response.data.data);
      ElMessage.success('图片上传成功');
    } else {
      ElMessage.error(response.data.msg || '图片上传失败');
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('图片上传失败');
  }
};

//创建评论
const handleCreateComment = async () => {
  try {
    const response = await createComment({
      content: newCommentText.value,
      productId: productId.value,
      imageUrls: uploadedImages.value,
      userRate: newUserRate.value,
    });
    if (response.data.code === '200') {
      ElMessage.success('评论成功');
      newCommentText.value = '';
      newUserRate.value = 0;
      uploadedImages.value = [];
      fetchComments();
    } else {
      ElMessage.error(response.data.msg || '评论失败');
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('评论失败');
  }
};

onMounted(() => {
  fetchComments();
});


</script>

<template>
  <div style="min-height: 100vh; background-color: #fff7e6; display: flex; flex-direction: column;">
    <header style="background-color: #ff5100; padding: 10px; text-align: center; position: relative;">
      <span style="color: white; font-size: 24px;">TOMATO评论区——welcome!</span>
      <el-button
        type="primary"
        size="default"
        color="#ff5100"
        style="position: absolute; right: 45px; top: 12px;"
        @click="router.push(`/productDetail/${productId}`)"
      >
        返回
      </el-button>
    </header>

    <div style="flex: 1; padding: 20px; display: flex; flex-direction: column; align-items: center;">
      <div style="width: 80%; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); padding: 20px; background-color: white;">
        <div v-for="comment in comments" :key="comment.Id" style="margin-bottom: 20px; border: 1px solid #ccc; padding: 10px;">
          <div style="display: flex; align-items: center; margin-bottom: 10px;">
            <img src="./touxiang.png" alt="头像" style="width: 40px; height: 40px; border-radius: 50%; margin-right: 10px;" />
            <span>用户ID: {{ comment.Id }}</span>
          </div>
          <div style="display: flex; align-items: center; margin-bottom: 10px;">用户评分:{{comment.userRate}}</div>
          <div style="margin-bottom: 10px;">{{ comment.Content }}</div>
          <div v-if="comment.imageUrls && comment.imageUrls.length > 0" style="display: flex; gap: 10px; margin-bottom: 10px;">
            <img v-for="url in comment.imageUrls" :src="url" :key="url" style="width: 100px; height: 100px;" />
          </div>
          <div style="display: flex; gap: 10px;">
            <el-button size="small" type="danger" @click="handleDeleteComment(comment.Id)">删除</el-button>
            <el-button size="small" @click="handleLikeComment(comment.Id)">点赞 ({{ comment.likeCount }})</el-button>
          </div>
        </div>
      </div>

      <div style="width: 80%; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); padding: 20px; margin-top: 20px; background-color: white;">
        <el-input type="textarea" v-model="newCommentText" placeholder="请输入评论内容" style="margin-bottom: 10px;" />
        <el-form-item label="评分（0-10分）" style="margin-bottom: 10px;">
          <el-input-number
              v-model="newUserRate"
              :min="0"
              :max="10"
              :step="1"
              style="width: 100%;"
          />
        </el-form-item>
        <el-upload
          action=""
          :auto-upload="false"
          :on-change="file => handleUploadImage(file.raw)"
          multiple
          style="margin-bottom: 10px;"
        >
          <el-button type="primary">上传图片</el-button>
        </el-upload>
        <el-button type="primary" style="width: 100%;" @click="handleCreateComment">创建评论</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>