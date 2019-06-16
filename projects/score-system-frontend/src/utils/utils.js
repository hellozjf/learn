import React from 'react';
import {Select} from 'antd'

const Option = Select.Option;
export default {
  formateDate(time) {
    if (!time) return '';
    let date = new Date(time);
    return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate() + ' ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();
  },
  pagination(data, callback) {
    return {
      onChange: (current) => {
        callback(current)
      },
      current: data.result.page,
      pageSize: data.result.page_size,
      total: data.result.total_count,
      showTotal: () => {
        return `共${data.result.total_count}条`
      },
      showQuickJumper: true
    }
  },
  /**
   * 专门为spring-data-rest使用的pagination
   * @param response
   * @param callback
   * @returns {{onChange: onChange, current: *, pageSize: *, total: *, showTotal: (function(): string), showQuickJumper: boolean}}
   */
  paginationEntity(data, callback) {
    return {
      onChange: (current) => {
        callback(current)
      },
      // ant-design的分页从1开始，jpa-rest的分页从0开始，所以传入的时候要-1，接收的时候要+1
      current: data.page.number + 1,
      pageSize: data.page.size,
      total: data.page.totalElements,
      showTotal: () => {
        return `共${data.page.totalElements}条`
      },
      showQuickJumper: true
    }
  },
  getOptionList(data) {
    if (!data) {
      return [];
    }
    let options = [] //[<Option value="0" key="all_key">全部</Option>];
    data.map((item) => {
      options.push(<Option value={item.id} key={item.id}>{item.name}</Option>)
    })
    return options;
  },
  updateSelectedItem(selectedRowKeys, selectedItem, selectedIds) {
    if (selectedIds) {
      this.setState({
        selectedRowKeys,
        selectedItem,
        selectedIds
      })
    } else {
      this.setState({
        selectedRowKeys,
        selectedItem
      })
    }
  }
}