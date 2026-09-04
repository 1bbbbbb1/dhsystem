import request from '@/utils/request'

/**
 * 生成图片
 * @param prompt 图片描述
 * @param size 图片尺寸
 * @returns {Promise<any>}
 */
export function generateImage(prompt, size) {
  return request({
    url: '/system/image/generate',
    method: 'post',
    params: { prompt, size } // 与后端接口参数名一致
  })
}
