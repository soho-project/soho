import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import {Form, Input, InputNumber, Radio, Modal, Select, Upload, message,Checkbox,DatePicker,TreeSelect} from 'antd'
import TextArea from "antd/es/input/TextArea";
import {t, Trans} from "@lingui/macro"
import ImgCrop from "antd-img-crop";
import {LoadingOutlined, PlusOutlined} from "@ant-design/icons";
import moment from 'moment';
import api from '../../../services/api'
import store from "store";
import QuillEditor from "../../../components/Editor/QuillEditor";
import {addRootNode} from "../../../utils/tree";
const {createUserAvatar, createAdminContentImage} = api

const { Option } = Select;
const FormItem = Form.Item
//时间格式化
const datetimeFormat = "YYYY-MM-DD HH:mm:ss"
const dateFormat = "YYYY-MM-DD"
const formItemLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 14,
  },
}

class ChatUserFriendApplyModal extends PureComponent {
  formRef = React.createRef()

  state = {
    value: [],
    loading: false, //上传 loading
  };


  handleOk = () => {
    const { item = {}, onOk } = this.props
    this.formRef.current.validateFields()
      .then(values => {
        const data = {
          ...values,
          id: item.id,          //时间值处理
        }
        onOk(data)
      })
      .catch(errorInfo => {
        console.log(errorInfo)
      })
  }

  render() {
    const { item = {}, onOk, form,options,trees, ...modalProps } = this.props
    let initData = {
                    ...item};
    //初始化时间处理
    return (

      <Modal {...modalProps} onOk={this.handleOk} width={1300}>
        <Form ref={this.formRef} name="control-ref" initialValues={{ ...initData }} layout="horizontal">
         <FormItem  name='chatUid' rules={[{ required: 0 }]}            label={t`Chat Uid`} hasFeedback {...formItemLayout}>
            <InputNumber
 max={11} step="1"  style={{width:200}}             disabled={false}
 />
          </FormItem>
         <FormItem  name='friendUid' rules={[{ required: 0 }]}            label={t`Friend Uid`} hasFeedback {...formItemLayout}>
            <InputNumber
 max={11} step="1"  style={{width:200}}             disabled={false}
 />
          </FormItem>
         <FormItem  name='status' rules={[{ required: 0 }]}            label={t`Status`} hasFeedback {...formItemLayout}>
            <InputNumber
 max={4} step="1"  style={{width:200}}             disabled={false}
 />
          </FormItem>
         <FormItem  name='ask' rules={[{ required: 0 }]}            label={t`Ask`} hasFeedback {...formItemLayout}>
            <Input             disabled={false}
 maxLength={200} />
          </FormItem>
         <FormItem  name='answer' rules={[{ required: 0 }]}            label={t`Answer`} hasFeedback {...formItemLayout}>
            <Input             disabled={false}
 maxLength={200} />
          </FormItem>
        </Form>
      </Modal>
    )
  }
}

ChatUserFriendApplyModal.propTypes = {
  type: PropTypes.string,
  item: PropTypes.object,
  onOk: PropTypes.func,
}

export default ChatUserFriendApplyModal
