import JsonP from 'jsonp'
import axios from "axios";
import {Modal} from 'antd'

export default class Axios {

  static jsonp(options) {
    return new Promise((resolve, reject) => {
      JsonP(options.url, {
        param: 'callback'
      }, function (err, response) {
        if (response.status === 'success') {
          resolve(response);
        } else {
          reject(response.message);
        }
      })
    });
  }

  static ajax(options) {
    let baseApi = 'http://localhost:8080/';
    return new Promise((resolve, reject) => {
      axios({
        url: options.url,
        method: options.method,
        baseURL: baseApi,
        timeout: 5000,
        params: (options.data && options.data.params) || ''
      }).then((response) => {
        if (response.status == '200') {
          let res = response.data;
          if (!res.code || res.code && res.code == '0') {
            // 如果res.code不存在，获取res.code存在且为0，则本次请求成功
            resolve(res);
          } else {
            Modal.info({
              title: '提示',
              content: res.msg
            })
          }
        } else {
          reject(response.data);
        }
      });
    });
  }
}