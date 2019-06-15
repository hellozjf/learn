import React from 'react'
import {Table, Button, Modal, Form, Input, Icon, Checkbox} from 'antd'
import reqwest from 'reqwest';

const columns = [
  {
    title: '班级名称',
    dataIndex: 'name',
  },
  {
    title: '班级描述',
    dataIndex: 'description',
  },
];

export default class ClassManage extends React.Component {

  state = {
    data: [],
    pagination: {},
    loading: false,
    visible: false,
  };

  componentDidMount() {
    this.fetch();
  }

  handleTableChange = (pagination, filters, sorter) => {
    const pager = {...this.state.pagination};
    pager.current = pagination.current;
    this.setState({
      pagination: pager,
    });
    this.fetch({
      page: pagination.current,
      size: pagination.pageSize,
      sort: `${sorter.field},${sorter.order}`,
      ...filters,
    });
  };

  showModal = () => {
    this.setState({
      visible: true,
    });
  };

  handleOk = e => {
    console.log(e);
    this.setState({
      visible: false,
    });
  };

  handleCancel = e => {
    console.log(e);
    this.setState({
      visible: false,
    });
  };

  handleSubmit = e => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (!err) {
        console.log('Received values of form: ', values);
      }
    });
  };

  fetch = (params = {}) => {
    console.log('params:', params);
    this.setState({loading: true});
    reqwest({
      url: 'http://localhost:8080/class',
      method: 'get',
      data: {
        ...params,
      },
      type: 'json',
    }).then(data => {
      const pagination = {...this.state.pagination};
      pagination.current = data.page.number;
      pagination.pageSize = data.page.size;
      pagination.total = data.page.totalElements;
      this.setState({
        loading: false,
        data: data._embedded.student,
        pagination,
      });
    });
  };

  render() {
    const {getFieldDecorator} = this.props.form;
    return (
      <div>
        <Button onClick={this.showModal}>添加</Button>
        <Table
          columns={columns}
          rowKey={record => record.id}
          dataSource={this.state.data}
          pagination={this.state.pagination}
          loading={this.state.loading}
          onChange={this.handleTableChange}
        />
        <Modal
          title="添加班级"
          visible={this.state.visible}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
        >
          <Form onSubmit={this.handleSubmit} className="login-form">
            <Form.Item>
              {getFieldDecorator('username', {
                rules: [{ required: true, message: 'Please input your username!' }],
              })(
                <Input
                  prefix={<Icon type="user" style={{ color: 'rgba(0,0,0,.25)' }} />}
                  placeholder="Username"
                />,
              )}
            </Form.Item>
            <Form.Item>
              {getFieldDecorator('password', {
                rules: [{ required: true, message: 'Please input your Password!' }],
              })(
                <Input
                  prefix={<Icon type="lock" style={{ color: 'rgba(0,0,0,.25)' }} />}
                  type="password"
                  placeholder="Password"
                />,
              )}
            </Form.Item>
            <Form.Item>
              {getFieldDecorator('remember', {
                valuePropName: 'checked',
                initialValue: true,
              })(<Checkbox>Remember me</Checkbox>)}
              <a className="login-form-forgot" href="">
                Forgot password
              </a>
              <Button type="primary" htmlType="submit" className="login-form-button">
                Log in
              </Button>
              Or <a href="">register now!</a>
            </Form.Item>
          </Form>
        </Modal>
      </div>
    );
  }
}