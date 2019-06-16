import JsonP from 'jsonp'
import axios from 'axios'
import {Modal} from 'antd'
import Utils from './../utils/utils'

export default class Axios {

  static requestList(_this, url, params, isMock) {
    var data = {
      params: params,
      isMock
    }
    this.ajax({
      url,
      data
    }).then((data) => {
      if (data && data.result) {
        let list = data.result.item_list.map((item, index) => {
          item.key = index;
          return item;
        });
        _this.setState({
          list,
          pagination: Utils.pagination(data, (current) => {
            _this.params.page = current;
            _this.requestList();
          })
        })
      }
    });
  }

  static requestEntityList(_this, params) {
    // ant-design的分页从1开始，jpa-rest的分页从0开始，所以传入的时候要-1，接收的时候要+1
    if (params.page) {
      params.page = params.page - 1;
    }
    // jpa-rest的排序字段需要手动拼接
    if (params.field && params.order) {
      params.sort = params.field + ',' + params.order
    }
    let url = params.url;
    let data = {
      params
    };
    this.ajaxEntity({
      url,
      data,
      method: 'get'
    }).then((data) => {
      let list = data._embedded.class.map((item, index) => {
        item.key = index;
        return item;
      });
      _this.setState({
        list,
        pagination: Utils.paginationEntity(data, (current) => {
          _this.params.page = current;
          _this.requestList();
        })
      })
    });
  }

  static jsonp(options) {
    return new Promise((resolve, reject) => {
      JsonP(options.url, {
        param: 'callback'
      }, function (err, response) {
        if (response.status == 'success') {
          resolve(response);
        } else {
          reject(response.messsage);
        }
      })
    })
  }

  static ajax(options) {
    let loading;
    if (options.data && options.data.isShowLoading !== false) {
      loading = document.getElementById('ajaxLoading');
      loading.style.display = 'block';
    }
    let baseApi = '';
    if (options.isMock) {
      baseApi = 'https://www.easy-mock.com/mock/5a7278e28d0c633b9c4adbd7/api';
    } else {
      baseApi = 'https://www.easy-mock.com/mock/5a7278e28d0c633b9c4adbd7/api';
    }
    return new Promise((resolve, reject) => {
      axios({
        url: options.url,
        method: 'get',
        baseURL: baseApi,
        timeout: 5000,
        params: (options.data && options.data.params) || ''
      }).then((response) => {
        if (options.data && options.data.isShowLoading !== false) {
          loading = document.getElementById('ajaxLoading');
          loading.style.display = 'none';
        }
        if (response.status == '200') {
          let res = response.data;
          if (res.code == '0') {
            resolve(res);
          } else {
            Modal.info({
              title: "提示",
              content: res.msg
            })
          }
        } else {
          reject(response.data);
        }
      })
    });
  }

  static ajaxEntity(options) {
    let loading;
    if (options.data && options.data.isShowLoading !== false) {
      loading = document.getElementById('ajaxLoading');
      loading.style.display = 'block';
    }
    let baseApi = 'http://localhost:8080/entity';
    return new Promise((resolve, reject) => {
      axios({
        url: options.url,
        method: options.method,
        baseURL: baseApi,
        timeout: 5000,
        params: options.method == 'get' ? (options.data && options.data.params) || '' : '',
        data: options.method != 'get' ? (options.data && options.data.params) || '' : ''
      }).then((response) => {
        if (options.data && options.data.isShowLoading !== false) {
          loading = document.getElementById('ajaxLoading');
          loading.style.display = 'none';
        }
        if (response.status == '200' || response.status == '201' || response.status == '204') {
          let res = response.data;
          resolve(res);
        } else {
          reject(response.data);
        }
      }).catch((error)=>{
        console.debug(error);
      })
    });
  }
}