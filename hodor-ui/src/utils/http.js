import axios from 'axios'
import { message } from 'ant-design-vue';
import JSONBIG from 'json-bigint'

// 设置响应数据的转换函数,使用JSONBIG解析响应数据,解决精度丢失问题
axios.defaults.transformResponse = [
  function (data) {
    const json = JSONBIG({
      storeAsString: true
    })
    const res = json.parse(data)
    return res
  }
]

// 创建axios实例
const httpInstance = axios.create({
  baseURL: window.location.origin + '/hodor/admin',  // 根地址
  timeout: 5000  // 超时时间
})

// axios请求拦截器
httpInstance.interceptors.request.use(config => {
  // 请求头添加API-KEY
  const apiKey = 'b50fd4d4d71935b7c2a001b87f068c4f'
  config.headers['API-KEY'] = apiKey
  return config
}, e => Promise.reject(e))

// 处理错误回调函数
const catchError = function (error) {
  if (error.response) {
    switch (error.response.status) {
      case 400:
        message.error(error.response.data.message || '请求参数异常')
        break
      case 401:
        message.warning(error.response.data.message || '密码错误或账号不存在！')
        break
      case 403:
        message.warning(error.response.data.message || '无访问权限，请联系企业管理员')
        break
      default:
        message.error(error.response.data.message || '服务端异常，请联系技术支持')
    }
  }
  return Promise.reject(error)
}
// axios响应式拦截器
httpInstance.interceptors.response.use(res => res.data, catchError)



export default httpInstance