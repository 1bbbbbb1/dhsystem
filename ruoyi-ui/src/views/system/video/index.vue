<template>
  <div class="app-container">
    <!-- 页面标题（增加数字人标识） -->
    <div class="page-header">
      <el-page-header content="数字人视频定制" />
    </div>

    <!-- 表单区域：强化数字人相关引导 -->
    <el-card shadow="hover" class="form-card">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <!-- 视频描述（必填，增加数字人提示） -->
        <el-form-item label="数字人描述" prop="prompt">
          <el-input
            v-model="form.prompt"
            type="textarea"
            :rows="4"
            placeholder="请输入数字人视频的详细描述（例如：生成一段穿着商务西装的年轻男性数字人在办公室演讲的视频，背景为会议场景，光线明亮）"
            resize="none"
          />
          <div class="desc-tip">
            提示：可描述数字人的性别、年龄、服饰、动作、背景环境等特征
          </div>
        </el-form-item>

        <!-- 视频参数配置 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="视频分辨率" prop="resolution">
              <el-select v-model="form.resolution" placeholder="请选择视频分辨率">
                <el-option label="480p" value="480p" />
                <el-option label="720p" value="720p" />
                <el-option label="1080p" value="1080p" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="视频比例" prop="ratio">
              <el-select v-model="form.ratio" placeholder="请选择视频比例">
                <el-option label="16:9 (横版)" value="16:9" />
                <el-option label="9:16 (竖版)" value="9:16" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="视频时长" prop="duration">
              <el-select v-model="form.duration" placeholder="请选择时长">
                <el-option label="2秒" value="2" />
                <el-option label="5秒" value="5" />
                <el-option label="10秒" value="10" />
                <el-option label="15秒" value="15" />
                <el-option label="20秒" value="20" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="generateVideo" :loading="isGenerating">
            <i class="el-icon-refresh" v-if="isGenerating" />
            <span v-if="isGenerating">生成中...</span>
            <span v-else>生成数字人视频</span>
          </el-button>
          <el-button type="text" @click="resetForm" style="margin-left: 10px">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 任务状态与结果展示区域 -->
    <el-card shadow="hover" class="result-card" v-if="taskId || errorMsg">
      <div class="result-header">
        <el-tag :type="videoUrl ? 'success' : (errorMsg ? 'danger' : 'info')">
          {{ videoUrl ? '数字人视频生成成功' : (errorMsg ? '错误信息' : '任务处理中') }}
        </el-tag>
        <el-tag v-if="taskId" type="info" style="margin-left: 10px">
          任务ID: {{ taskId }}
        </el-tag>
      </div>

      <!-- 任务处理中 -->
      <div class="processing-content" v-if="taskId && !videoUrl && !errorMsg">
        <el-progress :percentage="progress" stroke-width="6" style="max-width: 500px;" />
        <div class="processing-status">
          <i class="el-icon-loading" />
          {{ statusText || '数字人视频生成中，请稍候...' }}
        </div>
        <el-button
          type="text"
          @click="cancelTask"
          :disabled="!taskId || isCanceling"
          style="margin-top: 10px"
        >
          <i class="el-icon-circle-close" v-if="isCanceling" />
          <span v-if="isCanceling">取消中...</span>
          <span v-else>取消任务</span>
        </el-button>
      </div>

      <!-- 生成成功 -->
      <div class="success-content" v-if="videoUrl">
<!--        <el-video-->
<!--          :src="videoUrl"-->
<!--          controls-->
<!--          :poster="videoCover"-->
<!--          class="generated-video"-->
<!--        >-->
<!--          您的浏览器不支持视频播放-->
<!--        </el-video>-->
        <div class="video-actions">
          <el-button type="text" @click="downloadVideo">
            <i class="el-icon-download" /> 下载数字人视频
          </el-button>
          <el-button type="text" @click="copyVideoUrl">
            <i class="el-icon-copy-document" /> 复制视频链接
          </el-button>
        </div>
      </div>

      <!-- 生成失败 -->
      <div class="error-content" v-if="errorMsg">
        <el-alert
          :message="errorMsg"
          type="error"
          show-icon
          description="请检查数字人描述是否清晰，或稍后重试"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
// 引入API请求方法
import { createVideoTask, queryVideoTask, cancelVideoTask } from '@/api/system/video'
// 引入工具类
import { Message } from 'element-ui'

export default {
  name: 'DigitalHumanVideoGeneration',
  data() {
    return {
      // 表单数据
      form: {
        prompt: '', // 数字人视频描述
        resolution: '480p', // 视频分辨率
        ratio: '16:9', // 视频比例
        duration: '2' // 视频时长
      },
      // 表单校验规则（增加数字人关键词校验）
      formRules: {
        prompt: [
          { required: true, message: '请输入数字人视频的描述信息', trigger: 'blur' },
          { min: 5, max: 500, message: '描述长度需在5-500字之间', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              const keywords = ['数字人', '虚拟人', '数字角色', '虚拟角色'];
              const hasKeyword = keywords.some(keyword => value.includes(keyword));
              if (!hasKeyword) {
                callback(new Error('请描述与数字人相关的内容（如包含"数字人"、"虚拟人"等关键词）'));
              } else {
                callback();
              }
            },
            trigger: 'blur'
          }
        ],
        resolution: [
          { required: true, message: '请选择视频分辨率', trigger: 'change' }
        ],
        ratio: [
          { required: true, message: '请选择视频比例', trigger: 'change' }
        ],
        duration: [
          { required: true, message: '请选择视频时长', trigger: 'change' }
        ]
      },
      formRef: null,
      // 状态控制
      isGenerating: false, // 生成按钮加载状态
      isCanceling: false, // 取消按钮加载状态
      taskId: '', // 任务ID
      progress: 0, // 生成进度
      statusText: '', // 状态文本
      videoUrl: '', // 生成的视频URL
      videoCover: '', // 视频封面
      errorMsg: '', // 错误信息
      queryTimer: null // 轮询定时器
    }
  },
  beforeDestroy() {
    // 组件销毁时清除定时器
    if (this.queryTimer) {
      clearInterval(this.queryTimer)
    }
  },
  methods: {
    /**
     * 生成数字人视频（核心方法）
     */
    async generateVideo() {
      // 表单校验
      try {
        await this.$refs.formRef.validate()
      } catch (error) {
        return
      }

      // 重置状态
      this.isGenerating = true
      this.taskId = ''
      this.videoUrl = ''
      this.videoCover = ''
      this.errorMsg = ''
      this.progress = 0
      this.statusText = ''

      try {
        // 构建提示词（包含参数）
        const fullPrompt = `${this.form.prompt} --resolution ${this.form.resolution} --ratio ${this.form.ratio} --duration ${this.form.duration}`

        // 创建数字人视频生成任务
        const response = await createVideoTask(fullPrompt)
        if (response.code === 200) {
          this.taskId = response.data
          Message.success('数字人视频生成任务已创建，正在处理中...')

          // 启动轮询查询任务状态
          this.startQueryTask()
        } else {
          this.errorMsg = response.msg || '数字人视频生成任务创建失败'
          Message.error(this.errorMsg)
        }
      } catch (error) {
        this.errorMsg = error.message || '接口请求失败，请检查网络或联系管理员'
        Message.error(this.errorMsg)
      } finally {
        this.isGenerating = false
      }
    },

    /**
     * 轮询查询任务状态
     */
    startQueryTask() {
      // 立即查询一次
      this.queryTaskStatus()

      // 设置定时器，每3秒查询一次
      this.queryTimer = setInterval(() => {
        this.queryTaskStatus()
      }, 3000)
    },

    /**
     * 查询任务状态
     */
    async queryTaskStatus() {
      if (!this.taskId) return

      try {
        const response = await queryVideoTask(this.taskId)
        if (response.code === 200) {
          const taskInfo = response.data
          // 更新进度（根据实际返回字段调整）
          this.progress = taskInfo.progress || 0
          this.statusText = this.getStatusText(taskInfo.status)

          // 处理不同状态
          if (taskInfo.status === 'succeeded') {
            console.log('任务信息:', taskInfo) // 调试输出
            // 验证视频URL有效性
            if (taskInfo.content.video_url && taskInfo.content.video_url.trim() !== '') {
              this.videoUrl = taskInfo.content.video_url
              this.videoCover = taskInfo.content.last_frame_url || ''
              Message.success('数字人视频生成成功！')
              clearInterval(this.queryTimer)
            } else {
              this.errorMsg = '未获取到数字人视频地址'
              Message.error(this.errorMsg)
              clearInterval(this.queryTimer)
            }
          } else if (taskInfo.status === 'failed') {
            this.errorMsg = taskInfo.errorMsg || '数字人视频生成失败'
            Message.error(this.errorMsg)
            clearInterval(this.queryTimer)
          } else if (taskInfo.status === 'canceled') {
            this.errorMsg = '任务已取消'
            Message.warning(this.errorMsg)
            clearInterval(this.queryTimer)
          }
        } else {
          this.errorMsg = response.msg || '查询任务状态失败'
          clearInterval(this.queryTimer)
        }
      } catch (error) {
        this.errorMsg = '查询任务状态异常：' + error.message
        clearInterval(this.queryTimer)
      }
    },

    /**
     * 取消任务
     */
    async cancelTask() {
      if (!this.taskId) return

      this.isCanceling = true
      try {
        const response = await cancelVideoTask(this.taskId)
        if (response.code === 200) {
          Message.success('任务已取消')
          this.errorMsg = '任务已取消'
          clearInterval(this.queryTimer)
        } else {
          Message.error(response.msg || '取消任务失败')
        }
      } catch (error) {
        Message.error('取消任务异常：' + error.message)
      } finally {
        this.isCanceling = false
      }
    },

    /**
     * 重置表单
     */
    resetForm() {
      this.$refs.formRef.resetFields()
      this.taskId = ''
      this.videoUrl = ''
      this.videoCover = ''
      this.errorMsg = ''
      this.progress = 0
      this.statusText = ''
      if (this.queryTimer) {
        clearInterval(this.queryTimer)
        this.queryTimer = null
      }
    },

    /**
     * 下载视频
     */
    downloadVideo() {
      if (!this.videoUrl) return
      const link = document.createElement('a')
      link.href = this.videoUrl
      const fileName = `digital-human-video-${new Date().getTime()}.mp4`
      link.download = fileName
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      Message.success('数字人视频下载已启动！')
    },

    /**
     * 复制视频链接
     */
    copyVideoUrl() {
      if (!this.videoUrl) return
      navigator.clipboard.writeText(this.videoUrl)
        .then(() => {
          Message.success('视频链接已复制到剪贴板！')
        })
        .catch(() => {
          Message.error('复制失败，请手动复制')
        })
    },

    /**
     * 转换状态码为文本描述
     */
    getStatusText(status) {
      const statusMap = {
        'pending': '等待中...',
        'running': '生成中...',
        'succeeded': '生成成功',
        'failed': '生成失败',
        'canceled': '已取消'
      }
      return statusMap[status] || `处理中（状态：${status}）`
    }
  }
}
</script>

<style scoped>
.desc-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #606266;
  line-height: 1.5;
}

.app-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.form-card {
  margin-bottom: 20px;
}

.result-card {
  padding: 20px;
}

.result-header {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.processing-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 0;
}

.processing-status {
  margin-top: 15px;
  color: #606266;
  display: flex;
  align-items: center;
}

.processing-status .el-icon-loading {
  margin-right: 8px;
  animation: rotate 1.5s linear infinite;
}

.success-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.generated-video {
  width: 100%;
  max-width: 800px;
  border-radius: 4px;
  border: 1px solid #e6e6e6;
  padding: 10px;
}

.video-actions {
  margin-top: 15px;
}

.error-content {
  max-width: 800px;
}

/* 适配小屏幕 */
@media (max-width: 768px) {
  .generated-video {
    max-width: 100%;
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
