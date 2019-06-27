import React, {Component} from 'react';
import {Button, DatePicker, Form, Input, Select, message, Table, notification} from 'antd';
import axios from "axios";
import './App.css';

const {Option} = Select;

class GrabTicketForm extends React.Component {

  state = {
    ticketPeopleList: [],
    leftTicketList: [],
  };

  baseUrl = "http://aliyun.hellozjf.com:12307";

  columns = [
    {
      title: '车次',
      dataIndex: 'stationTrain',
      key: 'stationTrain',
    },
    {
      title: '出发时间',
      dataIndex: 'fromTime',
      key: 'fromTime',
    },
    {
      title: '到达时间',
      dataIndex: 'toTime',
      key: 'toTime',
    },
    {
      title: '出发站点',
      key: 'fromStation',
      dataIndex: 'fromStation',
    },
    {
      title: '到达站点',
      key: 'toStation',
      dataIndex: 'toStation',
    },
    {
      title: '二等座',
      key: 'secondClass',
      dataIndex: 'secondClass',
    },
    {
      title: '抢票',
      key: 'action',
      render: (text, record) => (
        <Button onClick={() => this.grabTicket(record.stationTrain)}>抢二等座</Button>
      ),
    },
  ];

  handleSubmit = e => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        console.log('Received values of form: ', values);
      }
    });
  };

  grabTicket = (stationTrain) => {
    let formInfo = this.props.form.getFieldsValue();
    console.debug(formInfo);
    axios({
      url: '/order/grabbing',
      method: 'post',
      baseURL: this.baseUrl,
      timeout: 10000,
      params: {
        trainDate: formInfo.trainDate.format("YYYY-MM-DD"),
        stationTrain: stationTrain,
        fromStation: formInfo.fromStation,
        toStation: formInfo.toStation,
        seatType: '二等座',
        ticketPeople: formInfo.ticketPeople,
        username: formInfo.username,
        password: formInfo.password,
        email: '908686171@qq.com'
      }
    }).then((response) => {
      console.debug(response);
      if (response.status == 200 && response.data.code == 0) {
        message.success('开始抢票，请注意查收邮件')
      } else {
        console.error(`failed status=${response.status} code=${response.data.code}`);
        message.error('开始抢票失败');
      }
    }).catch((error) => {
      message.error(error);
    });
  };

  getTicketPeopleList = () => {
    let formInfo = this.props.form.getFieldsValue();
    console.debug(formInfo);
    axios({
      url: '/order/queryTicketPeopleList',
      method: 'get',
      baseURL: this.baseUrl,
      timeout: 10000,
      params: {
        username: formInfo.username,
        password: formInfo.password
      }
    }).then((response) => {
      console.debug(response);
      if (response.status == 200 && response.data.code == 0) {
        this.setState({
          ticketPeopleList: response.data.data
        });
        message.success('获取乘车人列表成功')
      } else {
        console.error(`failed status=${response.status} code=${response.data.code}`);
        message.error('获取乘车人列表失败');
      }
    }).catch((error) => {
      message.error(error);
    });
  };

  getLeftTicketList = () => {
    let formInfo = this.props.form.getFieldsValue();
    console.debug(formInfo);
    axios({
      url: '/order/queryLeftTicketList',
      method: 'get',
      baseURL: this.baseUrl,
      timeout: 10000,
      params: {
        username: formInfo.username,
        password: formInfo.password,
        trainDate: formInfo.trainDate.format("YYYY-MM-DD"),
        fromStation: formInfo.fromStation,
        toStation: formInfo.toStation
      }
    }).then((response) => {
      console.debug(response);
      if (response.status == 200 && response.data.code == 0) {
        message.success('获取车票信息成功');
        let leftTicketList = response.data.data;
        leftTicketList = leftTicketList.filter((item, index, data) => {
          return item.stationTrain.indexOf('D') == 0 || item.stationTrain.indexOf('G') == 0;
        });
        this.setState({
          leftTicketList: leftTicketList
        })
      } else {
        console.error(`failed status=${response.status} code=${response.data.code}`);
        message.error('获取车票信息失败');
      }
    }).catch((error) => {
      message.error(error);
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
            获取乘车人列表
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
                  return <Option key={index} value={item.passenger_name}>{item.passenger_name}</Option>
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
          <Button htmlType="button" onClick={this.getLeftTicketList}>
            查询车次
          </Button>
          <Button htmlType="button" onClick={this.queryState}>
            获取抢票状态
          </Button>
          <Button htmlType="button" onClick={this.stopGrabbing}>
            手动停止抢票
          </Button>
        </Form.Item>
        <Table columns={this.columns} rowKey={record => record.stationTrain} dataSource={this.state.leftTicketList}/>
      </Form>
    );
  }

  queryState = () => {

    let formInfo = this.props.form.getFieldsValue();
    console.debug(formInfo);
    axios({
      url: '/order/queryState',
      method: 'get',
      baseURL: this.baseUrl,
      timeout: 10000,
      params: {
        username: formInfo.username,
      }
    }).then((response) => {
      console.debug(response);
      if (response.status == 200 && response.data.code == 0) {
        message.info(`当前抢票状态为${response.data.data.stateString}`)
      } else {
        console.error(`failed status=${response.status} code=${response.data.code}`);
        message.error('获取抢票状态失败');
      }
    }).catch((error) => {
      message.error(error);
    });
  };

  stopGrabbing = () => {

    let formInfo = this.props.form.getFieldsValue();
    console.debug(formInfo);
    axios({
      url: '/order/stopGrabbing',
      method: 'post',
      baseURL: this.baseUrl,
      timeout: 10000,
      params: {
        username: formInfo.username,
      }
    }).then((response) => {
      console.debug(response);
      if (response.status == 200 && response.data.code == 0) {
        message.success('停止抢票成功');
      } else {
        console.error(`failed status=${response.status} code=${response.data.code}`);
        message.error('获取抢票状态失败');
      }
    }).catch((error) => {
      message.error(error);
    });
  }
}

const WrappedGrabTicketForm = Form.create({})(GrabTicketForm);

export default class App extends Component {
  render() {
    return <WrappedGrabTicketForm/>;
  }
}