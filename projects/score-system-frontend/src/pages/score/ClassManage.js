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
    url: '/class',
    page: 1,
    size: 10,
    field: 'gmtCreate',
    order: 'asc'
  };

  state = {
    isVisible: false
  };

  formList = [
    {
      type: 'INPUT',
      label: '班级名称',
      field: 'name',
      placeholder: '请输入班级名称',
      width: 140
    },
    {
      type: 'INPUT',
      label: '班级描述',
      field: 'description',
      placeholder: '请输入班级描述',
      width: 140
    }
  ];

  componentDidMount() {
    this.requestList();
  }

  handleFilter = (params) => {
    console.debug(`params=${JSON.stringify(params)}`);
    if (params.name && params.name != '' ||
      params.description && params.description != '') {
      this.params.url = '/class/search/findByNameLikeAndDescriptionLike';
    } else {
      this.params.url = '/class';
    }
    this.params.name = '%' + params.name + '%';
    this.params.description = '%' + params.description + '%';
    this.requestList();
  };

  requestList = () => {
    axios.requestEntityList(this, this.params);
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
  };

  // 创建班级提交
  handleSubmit = () => {

    let _this = this;

    // 表单校验
    const form = this.form.props.form;
    form.validateFields((err, values) => {
      if (err) {
        return;
      }

      // 提交表单
      let type = _this.state.type;
      let data = _this.form.props.form.getFieldsValue();
      // 描述字段如果是空的，则设置空字符串
      if (!data.description) {
        data.description = '';
      }
      axios.ajaxEntity({
        url: type == 'create' ? '/class' : '/class/' + data.id,
        method: type == 'create' ? 'post' : 'put',
        data: {
          params: data
        }
      }).then((res) => {
        _this.form.props.form.resetFields();
        _this.setState({
          isVisible: false
        });
        _this.requestList();
      }).catch((error) => {
        console.debug(error);
      })
    });
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
              initialValue: info.name,
              rules: [
                {
                  required: true,
                  message: '请输入班级名称',
                },
              ],
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