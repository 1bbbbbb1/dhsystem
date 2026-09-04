<template>
  <div class="app-container">
    <!-- 页面标题（增加数字人标识） -->
    <div class="page-header">
      <el-page-header content="数字人照片定制" />
    </div>

    <!-- 表单区域：输入参数（强化数字人相关引导） -->
    <el-card shadow="hover" class="form-card">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <!-- 照片描述（必填，增加数字人提示） -->
        <el-form-item label="数字人描述" prop="prompt">
          <el-input
            v-model="form.prompt"
            type="textarea"
            :rows="4"
            placeholder="请输入数字人照片的详细描述（例如：生成一位穿着商务西装的年轻男性数字人，背景为办公室，光线明亮）"
            resize="none"
          />
          <div class="desc-tip">
            提示：可描述数字人的性别、年龄、服饰、姿态、背景等特征
          </div>
        </el-form-item>

        <!-- 照片尺寸（保留原有选项，适配数字人常用尺寸） -->
        <el-form-item label="照片尺寸" prop="size">
          <el-select v-model="form.size" placeholder="请选择照片尺寸">
            <el-option label="2K (1080×1920) - 竖版人像" value="2K" />
            <el-option label="4K (2160×3840) - 高清竖版" value="4K" />
            <el-option label="1024×1024 - 正方形人像" value="1024x1024" />
            <el-option label="1024×1792 - 全身人像" value="1024x1792" />
          </el-select>
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="generateImage" :loading="isGenerating">
            <i class="el-icon-refresh" v-if="isGenerating" />
            <span v-if="isGenerating">生成中...</span>
            <span v-else>生成数字人照片</span>
          </el-button>
          <el-button type="text" @click="resetForm" style="margin-left: 10px">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 结果展示区域（调整提示文字） -->
    <el-card shadow="hover" class="result-card" v-if="imageUrl || errorMsg">
      <div class="result-header">
        <el-tag :type="imageUrl ? 'success' : 'danger'">
          {{ imageUrl ? '数字人照片生成成功' : '错误信息' }}
        </el-tag>
      </div>

      <!-- 成功：展示照片 + 下载按钮 -->
      <div class="success-content" v-if="imageUrl">
        <el-image
          :src="imageUrl"
          fit="contain"
          :preview-src-list="[imageUrl]"
          class="generated-image"
          placeholder="数字人照片加载中..."
          @error="handleImageError"
        />
        <div class="image-actions">
          <el-button type="text" @click="downloadImage">
            <i class="el-icon-download" /> 下载数字人照片
          </el-button>
          <el-button type="text" @click="copyImageUrl">
            <i class="el-icon-copy-document" /> 复制照片链接
          </el-button>
        </div>
      </div>

      <!-- 失败：展示错误信息 -->
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
// 引入API请求方法（需提前创建对应的api文件）
import { generateImage } from '@/api/system/image'
// 引入Ruoyi工具类（消息提示、复制功能等）
import { Message } from 'element-ui'

export default {
  name: 'DigitalHumanImageGeneration',
  data() {
    return {
      // 表单数据（保持原有结构）
      form: {
        prompt: '', // 数字人描述
        size: '2K'  // 默认尺寸
      },
      // 表单校验规则（强化数字人描述要求）
      formRules: {
        prompt: [
          { required: true, message: '请输入数字人的描述信息', trigger: 'blur' },
          { min: 5, max: 500, message: '描述长度需在5-500字之间', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              // 简单校验是否包含数字人相关关键词
              const keywords = ['数字人', '虚拟人', '虚拟形象', '数字形象'];
              const hasKeyword = keywords.some(keyword => value.includes(keyword));
              if (!hasKeyword) {
                callback(new Error('请描述与数字人相关的内容（如包含"数字人"、"虚拟人"等关键词）'));
              } else {
                callback();
              }
            },
            trigger: 'blur'
          }
        ]
      },
      formRef: null,
      isGenerating: false,
      imageUrl: '',
      errorMsg: ''
    }
  },
  methods: {
    /**
     * 生成数字人照片（核心方法，仅修改提示文字）
     */
    async generateImage() {
      try {
        await this.$refs.formRef.validate()
      } catch (error) {
        return
      }

      this.isGenerating = true
      this.imageUrl = ''
      this.errorMsg = ''

      try {
        const response = await generateImage(this.form.prompt, this.form.size)
        if (response.code === 200) {
          this.imageUrl = response.data
          Message.success('数字人照片生成成功！')
        } else {
          this.errorMsg = response.msg || '数字人照片生成失败'
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
     * 重置表单（保持原有逻辑）
     */
    resetForm() {
      this.$refs.formRef.resetFields()
      this.imageUrl = ''
      this.errorMsg = ''
    },

    /**
     * 下载照片（修改文件名标识）
     */
    downloadImage() {
      if (!this.imageUrl) return
      const link = document.createElement('a')
      link.href = this.imageUrl
      const fileName = `digital-human-image-${new Date().getTime()}.png`
      link.download = fileName
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      Message.success('数字人照片下载已启动！')
    },

    /**
     * 复制照片链接（保持原有逻辑）
     */
    copyImageUrl() {
      if (!this.imageUrl) return
      navigator.clipboard.writeText(this.imageUrl)
        .then(() => {
          Message.success('数字人照片链接已复制到剪贴板！')
        })
        .catch(() => {
          Message.error('复制失败，请手动复制')
        })
    },

    /**
     * 照片加载失败处理（修改提示文字）
     */
    handleImageError() {
      this.errorMsg = '数字人照片加载失败，请重试'
      this.imageUrl = ''
      Message.warning(this.errorMsg)
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
}

.success-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.generated-image {
  width: 100%;
  max-width: 800px;
  height: auto;
  border-radius: 4px;
  border: 1px solid #e6e6e6;
  padding: 10px;
}

.image-actions {
  margin-top: 15px;
}

.error-content {
  max-width: 800px;
}

/* 适配小屏幕 */
@media (max-width: 768px) {
  .generated-image {
    max-width: 100%;
  }
}
</style>
