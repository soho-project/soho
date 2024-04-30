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
import UploadEditor from '../../../components/Upload/UploadEditor';
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

class TemporalCategoryModal extends PureComponent {
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
parent_id: 0,
                    ...item};
    //初始化时间处理
    return (

      <Modal {...modalProps} onOk={this.handleOk} width={1300}>
        <Form ref={this.formRef} name="control-ref" initialValues={{ ...initData }} layout="horizontal">
         <FormItem  name='name' rules={[{ required: 0 }]}            label={t`Name`} hasFeedback {...formItemLayout}>
            <Input             disabled={false}
 maxLength={255} />
          </FormItem>
         <FormItem  name='title' rules={[{ required: 0 }]}            label={t`Title`} hasFeedback {...formItemLayout}>
            <Input             disabled={false}
 maxLength={255} />
          </FormItem>
         <FormItem  name='parentId' rules={[{ required: 0 }]}            label={t`Parent Id`} hasFeedback {...formItemLayout}>
       <TreeSelect
             showSearch
             style={{ width: '100%' }}
             // value={value}
             dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
             placeholder="Please select"
             allowClear
             treeDefaultExpandAll
             // onChange={onChange}
             disabled={false}
             treeData={addRootNode(trees?.parentId)}
           />
          </FormItem>
         <FormItem  name='notes' rules={[{ required: 0 }]}            label={t`Notes`} hasFeedback {...formItemLayout}>
            <Input             disabled={false}
 maxLength={1000} />
          </FormItem>
        </Form>
      </Modal>
    )
  }
}

TemporalCategoryModal.propTypes = {
  type: PropTypes.string,
  item: PropTypes.object,
  onOk: PropTypes.func,
}

export default TemporalCategoryModal
