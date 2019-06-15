export default {
  formateDate(time) {
    if (!time) {
      return '';
    }
    let date = new Date(time);
    return date.getFullYear() + '-'
      + (date.getMonth() + 1) + '-'
      + date.getDate() + ' '
      + date.getHours() + ':'
      + date.getMinutes() + ':'
      + date.getSeconds();
  },
  pagination(data, callback) {
    let page = {
      onChange: (current) => {
        callback(current)
      },
      current:data.page.number + 1,
      pageSize: data.page.size,
      total:data.page.totalElements,
      showTotal:()=>{
        return `共${data.page.totalElements}条`
      },
      showQuickJumper:true
    }
    return page;
  }
}