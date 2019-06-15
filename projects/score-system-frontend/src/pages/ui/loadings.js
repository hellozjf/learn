import React from 'react'
import {Card, Button, Spin, Icon, Alert} from 'antd'
import './ui.less'

export default class Loadings extends React.Component {
  render() {
    const icon = <Icon type="loading" style={{fontSize:24}}/>

    return (
      <div>
        <Card title="Spin用法" className="card-wrap">
          <Spin size="small"/>
          {/*字符串不能省略px，这里使用了多个值必须用字符串包裹*/}
          <Spin style={{margin:'0 10px'}}/>
          <Spin size="large"/>
          {/*数字可以省略px*/}
          <Spin indicator={icon} style={{marginLeft: 10}}/>
        </Card>
        <Card title="内容遮罩" className="card-wrap">
          <Alert
            message="React"
            description="欢迎来到React高级实战课程"
            type="info"
          />
          <Spin>
            <Alert
              message="React"
              description="欢迎来到React高级实战课程"
              type="warning"
            />
          </Spin>
          <Spin tip="加载中...">
            <Alert
              message="React"
              description="欢迎来到React高级实战课程"
              type="warning"
            />
          </Spin>
          <Spin indicator={icon}>
            <Alert
              message="React"
              description="欢迎来到React高级实战课程"
              type="warning"
            />
          </Spin>
        </Card>
      </div>
    );
  }
}