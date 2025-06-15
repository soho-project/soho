import React, { useRef,useState,useEffect } from 'react'
import {Form, Input, InputNumber, Radio, Modal, Select, message,Checkbox,DatePicker,TreeSelect} from 'antd'
import TextArea from "antd/es/input/TextArea";
import {useLingui, Trans} from "@lingui/react/macro"
import ImgCrop from "antd-img-crop";
import {LoadingOutlined, PlusOutlined} from "@ant-design/icons";
import moment from 'moment';
import apis from '@/apis'
import store from "store";
import QuillEditor from "@/component/form/editor";
import CodeEditor from "@/component/form/codeEditor"
import JsonEditor from "@/component/form/jsonEditor"
import UploadWithInput from '@/component/form/upload/UploadWithInput';
import UploadAvatar from '@/component/form/upload/UploadAvatar';
import {addRootNode} from "@/utils/tree";
import Upload from '@/component/form/upload/UploadFile'
import dayjs from 'dayjs';
import {FormExtendItems} from "./ExtendedComponents"

const {createUserAvatar, createAdminContentImage} = apis

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

export default (props) => {
  const formRef = useRef()
  const { i18n, t } = useLingui()

  const [state,setState] = useState({
    value: [],
    loading: false, //上传 loading
  });
  const { baseState={}, item = {}, onOk, form, ...modalProps } = props
  const {options={}, optionValueLabel={}, trees={}} = baseState;

  const handleOk = () => {
    const { item = {}, onOk } = props
    formRef.current.validateFields()
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


  const initData = (item) => {
    return {
      ...item,

      password: null,
    
    }
  }

  useEffect(() => {
    if (props.open) {
      let data = initData(item)
      if(item?.id) {
        formRef.current.setFieldsValue(data)
      } else {
        formRef.current.resetFields()
        formRef.current.setFieldsValue(data)
      }
    }
  }, [item])
  
  
    return (
      <Modal {...modalProps} onOk={handleOk} width={1300}>
        <Form ref={formRef} name="control-ref" layout="horizontal">
         <FormItem  name='username' rules={[{ required: false }]} label={t("Username")} hasFeedback {...formItemLayout}>
            <Input disabled={false} maxLength={128} />
          </FormItem>
         <FormItem  name='phone' rules={[{ required: false }]} label={t("Phone")} hasFeedback {...formItemLayout}>
            <Input disabled={false} maxLength={11} />
          </FormItem>
         <FormItem  name='nickName' rules={[{ required: false }]} label={t("Nick Name")} hasFeedback {...formItemLayout}>
            <Input disabled={false} maxLength={45} />
          </FormItem>
         <FormItem  name='realName' rules={[{ required: false }]} label={t("Real Name")} hasFeedback {...formItemLayout}>
            <Input disabled={false} maxLength={45} />
          </FormItem>
         <FormItem  name='avatar' rules={[{ required: false }]} label={t("Avatar")} hasFeedback {...formItemLayout}>
           <UploadAvatar />
          </FormItem>
         <FormItem  name='email' rules={[{ required: false }]} label={t("Email")} hasFeedback {...formItemLayout}>
            <Input disabled={false} maxLength={255} />
          </FormItem>
         <FormItem  name='password' rules={[{ required: false }]} label={t("Password")} hasFeedback {...formItemLayout}>
            <Input.Password
             disabled={false}
 />
          </FormItem>
         <FormItem  name='sex' rules={[{ required: false }]} label={t("Sex")} hasFeedback {...formItemLayout}>
           <Select
             showSearch
             placeholder={t("Sex")}
             optionFilterProp="children"
             options={optionValueLabel?.sex}
             disabled={false}
             />
          </FormItem>
         <FormItem  name='age' rules={[{ required: false }]} label={t("Age")} hasFeedback {...formItemLayout}>
            <InputNumber  maxLength={11} step="1"  style={{width:200}} disabled={false}  />
          </FormItem>
          <FormExtendItems 
            item={item} 
            hasFeedback
            formRef={formRef}
            {...formItemLayout} />
        </Form>
      </Modal>
    )
}