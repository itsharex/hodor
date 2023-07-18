// 执行器接口函数
import httpInstance from "@/utils/http"

export const getActuatorInfoAPI = (name) => {
    return httpInstance({
        url: `/hodor/app/actuator/info?name=${name}`,
        method: 'GET',
    })
}

export const getActuatorListAPI = () => {
    return httpInstance({
        url: `/hodor/app/actuator/list`,
        method: 'GET',
    })
}