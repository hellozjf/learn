import React from 'react';
import {Button, Card, Form, Input, Modal, Radio, Select} from 'antd'
import axios from './../../axios'
import Utils from './../../utils/utils'
import BaseForm from './../../components/BaseForm'
import ETable from './../../components/ETable'

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const TextArea = Input.TextArea;
const Option = Select.Option;
export default class User extends React.Component {

  params = {
    page: 1,
    size: 10,
    field: 'gmtCreate',
    order: 'asc',
  }

  state = {
    isVisible: false
  }

  formList = [
    {
      type: 'INPUT',
      label: '班级名称',
      field: 'name',
      placeholder: '请输入班级名称',
      width: 140
    }
    // {
    //   type: 'INPUT',
    //   label: '用户名',
    //   field: 'user_name',
    //   placeholder: '请输入用户名称',
    //   width: 130,
    // }, {
    //   type: 'INPUT',
    //   label: '用户手机号',
    //   field: 'user_mobile',
    //   placeholder: '请输入用户手机号',
    //   width: 140,
    // }, {
    //   type: 'DATE',
    //   label: '请选择入职日期',
    //   field: 'user_date',
    //   placeholder: '请输入日期',
    // }
  ]

  componentDidMount() {
    this.requestList();
  }

  handleFilter = (params) => {
    console.debug(`params=${JSON.stringify(params)}`);
    params.name = '%' + params.name + '%';
    params.page = this.params.page;
    params.size = this.params.size;
    params.field = this.params.field;
    params.order = this.params.order;
    axios.requestEntityList(this, '/class/search/findByNameLike', params);
  };

  requestList = () => {
    axios.requestEntityList(this, '/class', this.params);
  };

  // 功能区操作
  hanleOperate = (type) => {
    let item = this.state.selectedItem;
    if (type == 'create') {
      this.setState({
        type,
        isVisible: true,
        title: '创建班级',
        info: null
      })
    } else if (type == 'edit') {
      if (!item) {
        Modal.info({
          title: "提示",
          content: '请选择一个班级'
        });
        return;
      }
      this.setState({
        type,
        isVisible: true,
        title: '编辑班级',
        info: item
      })
    } else {
      if (!item) {
        Modal.info({
          title: "提示",
          content: '请选择一个班级'
        });
        return;
      }
      let _this = this;
      Modal.confirm({
        title: '确认删除',
        content: '是否要删除当前选中的班级',
        onOk() {
          axios.ajaxEntity({
            url: '/class/' + item.id,
            method: 'delete',
            data: {
              params: {}
            }
          }).then((res) => {
            _this.setState({
              isVisible: false
            });
            _this.requestList();
            _this.setState({
              selectedRowKeys: null,
              selectedItem: null,
              selectedIds: null
            });
          })
        }
      })
    }
  }

  // 创建班级提交
  handleSubmit = () => {
    let type = this.state.type;
    let data = this.form.props.form.getFieldsValue();
    axios.ajaxEntity({
      url: type == 'create' ? '/class' : '/class/' + data.id,
      method: type == 'create' ? 'post' : 'put',
      data: {
        params: data
      }
    }).then((res) => {
      this.form.props.form.resetFields();
      this.setState({
        isVisible: false
      })
      this.requestList();
    }).catch((error) => {
      console.debug(error);
    })
  };

  /**
   * 分页、过滤、排序
   */
  handleChange = (pagination, filters, sorter) => {
    console.debug(`pagination=${JSON.stringify(pagination)}`);
    console.debug(`filters=${filters}`);
    console.debug(`sorter=${JSON.stringify(sorter)}`);
    this.params.page = pagination.current;
    this.params.size = pagination.pageSize;
    this.params.field = sorter.field;
    this.params.order = sorter.order == 'ascend' ? 'asc' : 'desc';
    this.requestList();
  };

  render() {
    const columns = [{
      title: '班级名称',
      dataIndex: 'name',
      sorter: true,
    }, {
      title: '班级描述',
      dataIndex: 'description',
      sorter: true,
    },
    ];
    return (
      <div>
        <Card>
          <BaseForm formList={this.formList} filterSubmit={this.handleFilter}/>
        </Card>
        <Card style={{marginTop: 16}} className="operate-wrap">
          <Button type="primary" icon="plus" onClick={() => this.hanleOperate('create')}>创建班级</Button>
          <Button type="primary" icon="edit" onClick={() => this.hanleOperate('edit')}>编辑班级</Button>
          <Button type="primary" icon="delete" onClick={() => this.hanleOperate('delete')}>删除班级</Button>
          <ETable
            style={{marginTop: 16}}
            size="middle"
            updateSelectedItem={Utils.updateSelectedItem.bind(this)}
            columns={columns}
            dataSource={this.state.list}
            selectedRowKeys={this.state.selectedRowKeys}
            selectedItem={this.state.selectedItem}
            pagination={this.state.pagination}
            onChange={this.handleChange}
          />
        </Card>
        {/*<div className="content-wrap">*/}
        {/*</div>*/}
        <Modal
          title={this.state.title}
          visible={this.state.isVisible}
          onOk={this.handleSubmit}
          onCancel={() => {
            this.form.props.form.resetFields();
            this.setState({
              isVisible: false
            })
          }}
          width={600}
        >
          <EditForm type={this.state.type} info={this.state.info} wrappedComponentRef={(inst) => {
            this.form = inst;
          }}/>
        </Modal>
      </div>
    );
  }
}

class EditForm extends React.Component {

  render() {
    let type = this.props.type;
    let info = this.props.info || {};
    const {getFieldDecorator} = this.props.form;
    const formItemLayout = {
      labelCol: {span: 5},
      wrapperCol: {span: 19}
    };
    const showId = type == 'create' ? {
      style: {display: 'none'}
    } : {};
    return (
      <Form layout="horizontal">
        <FormItem label="ID" {...formItemLayout} {...showId}>
          {
            getFieldDecorator('id', {
              initialValue: info.id
            })(
              <Input type="text" readOnly/>
            )
          }
        </FormItem>
        <FormItem label="班级名称" {...formItemLayout}>
          {
            getFieldDecorator('name', {
              initialValue: info.name
            })(
              <Input type="text" placeholder="请输入班级名称"/>
            )
          }
        </FormItem>
        <FormItem label="班级描述" {...formItemLayout}>
          {
            getFieldDecorator('description', {
              initialValue: info.description
            })(
              <Input type="text" placeholder="请输入班级描述"/>
            )
          }
        </FormItem>
        <FormItem label="创建时间" {...formItemLayout} style={{display: 'none'}}>
          {
            getFieldDecorator('gmtCreate', {
              initialValue: info.gmtCreate
            })(
              <Input type="text" readOnly/>
            )
          }
        </FormItem>
        <FormItem label="修改时间" {...formItemLayout} style={{display: 'none'}}>
          {
            getFieldDecorator('gmtModified', {
              initialValue: info.gmtModified
            })(
              <Input type="text" readOnly/>
            )
          }
        </FormItem>
      </Form>
    );
  }
}

EditForm = Form.create({})(EditForm);