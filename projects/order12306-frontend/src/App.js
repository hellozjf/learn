import React, {Component} from 'react';
import {Button, DatePicker, Form, Input, Select,} from 'antd';
import axios from "axios";
import './App.css';

const {Option} = Select;

class GrabTicketForm extends React.Component {
  state = {
    ticketPeopleList: [],
  };

  handleSubmit = e => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        console.log('Received values of form: ', values);
      }
    });
  };

  getTicketPeopleList = () => {
    let formInfo = this.props.form.getFieldsValue();
    console.debug(formInfo);
    axios({
      url: '/order/queryTicketPeopleList',
      method: 'get',
      baseURL: 'http://127.0.0.1:12307',
      timeout: 5000,
      params: {
        'username': formInfo.username,
        'password': formInfo.password
      }
    }).then((response)=> {
      console.debug(response);
      if (response.status == 200 && response.data.code == 0) {
        this.setState({
          ticketPeopleList: response.data.data
        })
      } else {
        console.error(`failed status=${response.status} code=${response.data.code}`)
      }
    });
  };

  render() {
    const {getFieldDecorator} = this.props.form;

    const formItemLayout = {
      labelCol: {
        xs: {span: 24},
        sm: {span: 8},
      },
      wrapperCol: {
        xs: {span: 24},
        sm: {span: 8},
      },
    };

    return (
      <Form {...formItemLayout} onSubmit={this.handleSubmit}>
        <Form.Item label="账号">
          {getFieldDecorator('username', {
            rules: [
              {
                required: true,
                message: '请输入账号',
              },
            ],
          })(<Input/>)}
        </Form.Item>
        <Form.Item label="密码" hasFeedback>
          {getFieldDecorator('password', {
            rules: [
              {
                required: true,
                message: '请输入密码',
              }
            ],
          })(<Input.Password/>)}
        </Form.Item>
        <Form.Item
          wrapperCol={{
            xs: {span: 24, offset: 0},
            sm: {span: 8, offset: 8},
          }}
        >
          <Button htmlType="button" onClick={this.getTicketPeopleList}>
            获取乘车人
          </Button>
        </Form.Item>
        <Form.Item label="乘车人">
          {getFieldDecorator('ticketPeople', {
            initialValue: [],
            rules: [
              {required: true, message: '请选择乘车人'},
            ],
          })(
            <Select>
              {
                (this.state.ticketPeopleList).map((item, index) => {
                  return <Option key={index} value={item.code}>{item.passenger_name}</Option>
                })
              }
            </Select>
          )}
        </Form.Item>
        <Form.Item label="乘车日期">
          {getFieldDecorator('trainDate', {
            rules: [{
              type: 'object',
              required: true,
              message: '请选择乘车日期'
            }]
          })(<DatePicker/>)}
        </Form.Item>
        <Form.Item label="出发站点">
          {getFieldDecorator('fromStation', {
            rules: [
              {
                required: true,
                message: '请输入出发站点',
              },
            ],
          })(<Input/>)}
        </Form.Item>
        <Form.Item label="到达站点">
          {getFieldDecorator('toStation', {
            rules: [
              {
                required: true,
                message: '请输入到达站点',
              },
            ],
          })(<Input/>)}
        </Form.Item>
        <Form.Item
          wrapperCol={{
            xs: {span: 24, offset: 0},
            sm: {span: 8, offset: 8},
          }}
        >
          <Button htmlType="button">
            查询车次
          </Button>
        </Form.Item>
      </Form>
    );
  }
}

const WrappedGrabTicketForm = Form.create({})(GrabTicketForm);

export default class App extends Component {
  render() {
    return <WrappedGrabTicketForm/>;
  }
}